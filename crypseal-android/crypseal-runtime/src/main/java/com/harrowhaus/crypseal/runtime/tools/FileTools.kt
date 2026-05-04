package com.harrowhaus.crypseal.runtime.tools

import java.io.File

class FileReadTool(private val projectRoot: File) : Tool() {
    override val name = "read_file"
    override val description = "Read file contents with line numbers and truncation limits."
    override val schema = """{"type":"object","properties":{"path":{"type":"string"}} }"""
    override val group = "file_ops"

    override suspend fun execute(args: Map<String, Any>): ToolResult {
        val path = args["path"] as? String ?: return ToolResult(false, "", "Missing path")
        val target = File(projectRoot, path)
        
        if (!target.exists() || !target.isFile) {
            return ToolResult(false, "", "File not found")
        }

        val lines = target.readLines()
        val truncated = lines.take(800) // Truncation limit for LLM context
        
        val numbered = truncated.mapIndexed { index, line ->
            "${index + 1}: $line"
        }.joinToString("\n")

        val output = if (lines.size > 800) {
            numbered + "\n... (file truncated, ${lines.size - 800} lines omitted)"
        } else {
            numbered
        }

        return ToolResult(true, output)
    }
}
