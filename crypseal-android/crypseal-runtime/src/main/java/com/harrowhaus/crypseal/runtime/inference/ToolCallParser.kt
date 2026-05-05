package com.harrowhaus.crypseal.runtime.inference

import com.harrowhaus.crypseal.runtime.models.ModelResponse
import org.json.JSONException
import org.json.JSONObject

object ToolCallParser {

    /**
     * Parses a model's raw text output to see if it contains a structured tool call.
     * Supports formats:
     * { "tool": "name", "args": {...} }
     * { "name": "name", "arguments": {...} }
     * { "type": "tool_call", "tool": "name", "args": {...} }
     */
    fun parse(rawText: String): ModelResponse {
        val trimmed = rawText.trim()
        
        // If it doesn't look like JSON at all, treat it as normal text
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return ModelResponse(text = rawText)
        }

        return try {
            val json = JSONObject(trimmed)
            
            // Try different known schemas
            val toolName = json.optString("tool", null) ?: json.optString("name", null)
            val argsObj = json.optJSONObject("args") ?: json.optJSONObject("arguments")

            if (toolName != null && argsObj != null) {
                // It's a valid tool call
                ModelResponse(
                    text = rawText,
                    toolCallName = toolName,
                    toolCallArgsJson = argsObj.toString()
                )
            } else {
                // Looks like JSON but doesn't match our tool schema
                ModelResponse(
                    text = rawText,
                    isMalformed = true
                )
            }
        } catch (e: JSONException) {
            // Malformed JSON
            ModelResponse(
                text = rawText,
                isMalformed = true
            )
        }
    }
}
