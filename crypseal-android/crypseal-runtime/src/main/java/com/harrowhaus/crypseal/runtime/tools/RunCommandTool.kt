package com.harrowhaus.crypseal.runtime.tools

/**
 * Tool that executes shell commands via a CommandRunner.
 * Accepts args: {"cmd":"..."} or {"command":"..."}
 * Actual execution is delegated to the injected CommandRunner.
 */
class RunCommandTool(
    private val commandRunner: CommandRunner
) : Tool() {
    override val name = "run_command"
    override val description = "Execute a shell command and return stdout/stderr."
    override val schema = """{"type":"object","properties":{"cmd":{"type":"string"},"command":{"type":"string"}} }"""
    override val group = "execution"

    override suspend fun execute(args: Map<String, Any>): ToolResult {
        val cmd = (args["cmd"] as? String)
            ?: (args["command"] as? String)
            ?: return ToolResult(false, "", "Missing 'cmd' or 'command' argument")

        val output = commandRunner.run(cmd)
        return if (output.success) {
            ToolResult(true, output.stdout)
        } else {
            ToolResult(false, output.stdout, "Exit code ${output.exitCode}: ${output.stderr}")
        }
    }
}
