package com.harrowhaus.crypseal.runtime.models

class ModelOutputRepair {
    
    fun repairToolCall(rawOutput: String): ModelResponse {
        // Common LLM failures: 
        // 1. Wrapping JSON in Markdown block ```json ... ```
        // 2. Extra trailing commas
        // 3. Missing closing brace
        
        var clean = rawOutput.trim()
        
        // Remove markdown wrappers
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json").trim()
        }
        if (clean.startsWith("```")) {
            clean = clean.removePrefix("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```").trim()
        }

        // Extremely naive brace repair (append missing '}')
        val openCount = clean.count { it == '{' }
        val closeCount = clean.count { it == '}' }
        if (openCount > closeCount) {
            val diff = openCount - closeCount
            clean += "}".repeat(diff)
        }

        // Simple regex extraction for tool call parsing (usually done via Gson/Moshi)
        // Here we simulate successful parsing vs failure
        val hasName = clean.contains("\"name\"") || clean.contains("\"tool\"")
        val hasArgs = clean.contains("\"args\"") || clean.contains("\"arguments\"")

        if (hasName && hasArgs) {
            // Extracted
            return ModelResponse(
                text = "Repaired tool call",
                toolCallName = "extracted_tool",
                toolCallArgsJson = "{...}",
                isMalformed = false
            )
        }

        return ModelResponse(
            text = rawOutput,
            isMalformed = true
        )
    }
}
