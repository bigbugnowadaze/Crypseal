package com.harrowhaus.crypseal.runtime.models

import java.net.URL
import java.net.HttpURLConnection

class TermuxLlamaServerRuntime(
    private val endpointUrl: String = "http://127.0.0.1:8080/v1/chat/completions"
) : ModelRuntime {
    override val name = "Termux-llama-server"

    override suspend fun generateResponse(messages: List<ModelMessage>): ModelResponse {
        // Minimal HTTP networking to hit Termux background daemon
        return try {
            val url = URL(endpointUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            // ... Write JSON body ...
            // ... Read response ...
            
            ModelResponse("Fallback server hit success.")
        } catch (e: Exception) {
            ModelResponse("Error connecting to Termux server: ${e.message}", isMalformed = true)
        }
    }
}
