package com.harrowhaus.crypseal.runtime.tools

/**
 * Interface for executing shell commands.
 * Real implementation delegates to Termux; mock for tests.
 */
interface CommandRunner {
    /**
     * Execute a command and return stdout/stderr.
     * @param command The shell command string
     * @param timeoutMs Maximum execution time in milliseconds
     * @return CommandOutput with exit code, stdout, stderr
     */
    suspend fun run(command: String, timeoutMs: Long = 30_000): CommandOutput
}

data class CommandOutput(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
) {
    val success: Boolean get() = exitCode == 0
}

/**
 * Mock command runner for unit tests.
 * Returns canned responses for known commands.
 */
class MockCommandRunner : CommandRunner {

    private val cannedResponses = mutableMapOf<String, CommandOutput>()

    fun addResponse(command: String, output: CommandOutput) {
        cannedResponses[command] = output
    }

    override suspend fun run(command: String, timeoutMs: Long): CommandOutput {
        return cannedResponses[command]
            ?: CommandOutput(0, "mock output for: $command", "")
    }
}
