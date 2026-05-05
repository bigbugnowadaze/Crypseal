package com.harrowhaus.crypseal.runtime.inference

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.harrowhaus.crypseal.runtime.models.ModelMessage
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LiteRtGemmaRuntime(
    private val context: Context,
    private val modelPath: String = "/data/data/com.harrowhaus.crypseal/files/models/gemma.bin"
) : ModelRuntime {

    override val name: String = "LiteRT Gemma 4"
    
    private var llmInference: LlmInference? = null

    init {
        val file = java.io.File(modelPath)
        if (file.exists()) {
            try {
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelPath)
                    .setMaxTokens(1024)
                    .setTemperature(0.7f)
                    .setTopK(40)
                    .build()
                llmInference = LlmInference.createFromOptions(context, options)
            } catch (e: Exception) {
                // Will be caught by checkHealth/generateResponse
            }
        }
    }
    
    suspend fun checkHealth(): RuntimeHealth = withContext(Dispatchers.IO) {
        val file = java.io.File(modelPath)
        if (!file.exists()) {
            return@withContext RuntimeHealth(
                status = RuntimeStatus.NEEDS_SETUP,
                message = "Model file not found at $modelPath. Please download the Gemma E2B model and place it in the app's files directory."
            )
        }
        
        if (llmInference == null) {
            return@withContext RuntimeHealth(
                status = RuntimeStatus.FAILED,
                message = "Inference engine failed to initialize with model at $modelPath."
            )
        }
        
        return@withContext RuntimeHealth(RuntimeStatus.READY)
    }

    override suspend fun generateResponse(messages: List<ModelMessage>): ModelResponse = withContext(Dispatchers.IO) {
        val health = checkHealth()
        if (!health.canGenerate || llmInference == null) {
            return@withContext ModelResponse(
                text = "Error: LiteRT Runtime is not ready. ${health.message}",
                isMalformed = true
            )
        }
        
        // Gemma 4 specific prompt format (simplified for M6)
        val prompt = messages.joinToString("\n") { 
            if (it.role == "user") "<start_of_turn>user\n${it.content}<end_of_turn>\n"
            else "<start_of_turn>model\n${it.content}<end_of_turn>\n"
        } + "<start_of_turn>model\n"

        return@withContext try {
            val result = llmInference?.generateResponse(prompt) ?: ""
            ToolCallParser.parse(result)
        } catch (e: Exception) {
            ModelResponse("Inference error: ${e.message}", isMalformed = true)
        }
    }
}
