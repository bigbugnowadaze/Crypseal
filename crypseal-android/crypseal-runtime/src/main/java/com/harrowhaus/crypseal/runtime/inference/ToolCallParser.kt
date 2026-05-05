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
        
        // Try to find a JSON block starting with { and ending with }
        val startIndex = trimmed.indexOf("{")
        val endIndex = trimmed.lastIndexOf("}")
        
        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            return ModelResponse(text = rawText)
        }

        val jsonCandidate = trimmed.substring(startIndex, endIndex + 1)

        return try {
            val json = JSONObject(jsonCandidate)
            
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
                // Contains JSON but not a tool call we recognize
                ModelResponse(
                    text = rawText
                )
            }
        } catch (e: JSONException) {
            // Malformed JSON block
            ModelResponse(
                text = rawText,
                isMalformed = true
            )
        }
    }
}
