package com.harrowhaus.crypseal.runtime.inference

import com.harrowhaus.crypseal.runtime.models.ModelMessage
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class TermuxLlamaServerRuntime(
    private var endpointUrl: String = "http://127.0.0.1:8080/v1/chat/completions"
) : ModelRuntime {

    override val name: String = "Termux llama.cpp"

    override suspend fun generateResponse(messages: List<ModelMessage>): ModelResponse = withContext(Dispatchers.IO) {
        try {
            val url = URL(endpointUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 60000 // Allow up to 60s for local generation

            val jsonBody = JSONObject()
            val messagesArray = JSONArray()
            for (msg in messages) {
                val msgObj = JSONObject()
                msgObj.put("role", msg.role)
                msgObj.put("content", msg.content)
                messagesArray.put(msgObj)
            }
            jsonBody.put("messages", messagesArray)
            jsonBody.put("temperature", 0.7)

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseString)
                val choices = responseJson.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    val content = message?.optString("content") ?: ""
                    
                    return@withContext ToolCallParser.parse(content)
                }
                return@withContext ModelResponse("Error: Empty choices array from server", isMalformed = true)
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown HTTP error"
                return@withContext ModelResponse("Error: HTTP $responseCode - $errorStream", isMalformed = true)
            }

        } catch (e: Exception) {
            return@withContext ModelResponse("Error: Could not connect to local server at $endpointUrl. Is Termux running llama-server?", isMalformed = true)
        }
    }
}
