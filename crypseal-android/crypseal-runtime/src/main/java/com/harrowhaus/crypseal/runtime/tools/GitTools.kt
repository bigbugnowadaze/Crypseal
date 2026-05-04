package com.harrowhaus.crypseal.runtime.tools

class GitStatusTool : Tool() {
    override val name = "git_status"
    override val description = "Returns git status."
    override val schema = """{"type":"object"}"""
    override val group = "git"

    override suspend fun execute(args: Map<String, Any>): ToolResult {
        // Implementation delegates to Termux in reality
        return ToolResult(true, "Mock git status")
    }
}
