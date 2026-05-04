package com.harrowhaus.crypseal.runtime.models

import com.harrowhaus.crypseal.runtime.tools.ToolResult

data class ModelMessage(
    val role: String,
    val content: String
)

data class ModelResponse(
    val text: String,
    val toolCallName: String? = null,
    val toolCallArgsJson: String? = null,
    val isMalformed: Boolean = false
)

interface ModelRuntime {
    val name: String
    suspend fun generateResponse(messages: List<ModelMessage>): ModelResponse
}

class MockModelRuntime(private val cannedResponses: List<ModelResponse>) : ModelRuntime {
    override val name = "MockRuntime"
    private var callCount = 0

    override suspend fun generateResponse(messages: List<ModelMessage>): ModelResponse {
        val response = if (callCount < cannedResponses.size) {
            cannedResponses[callCount]
        } else {
            ModelResponse("Mock exhausted.")
        }
        callCount++
        return response
    }
}
