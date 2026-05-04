package com.harrowhaus.crypseal.runtime.tools

abstract class Tool {
    abstract val name: String
    abstract val description: String
    abstract val schema: String // Basic representation of JSON schema parameters
    abstract val group: String

    abstract suspend fun execute(args: Map<String, Any>): ToolResult
}

data class ToolResult(
    val success: Boolean,
    val output: String,
    val error: String? = null
)
