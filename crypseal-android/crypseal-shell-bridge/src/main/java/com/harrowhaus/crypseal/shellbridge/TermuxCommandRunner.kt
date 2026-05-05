package com.harrowhaus.crypseal.shellbridge

import android.content.Context
import com.harrowhaus.crypseal.runtime.gateway.EventType
import com.harrowhaus.crypseal.runtime.tools.CommandOutput
import com.harrowhaus.crypseal.runtime.tools.CommandRunner
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID

class TermuxCommandRunner(
    private val context: Context,
    private val defaultWorkdir: String = "/data/data/com.termux/files/home",
    private val onEvent: ((EventType, String) -> Unit)? = null
) : CommandRunner {

    private val intentRunner = TermuxIntentRunner(context)

    override suspend fun run(command: String, timeoutMs: Long): CommandOutput {
        val commandId = UUID.randomUUID().toString()
        
        // Register interest in this command ID
        val deferred = TermuxResultReceiver.watchCommand(commandId)

        // Emit COMMAND_START
        onEvent?.invoke(EventType.COMMAND_START, "{\"id\":\"$commandId\",\"command\":\"$command\",\"workdir\":\"$defaultWorkdir\"}")

        try {
            val termuxCmd = TermuxCommand(
                id = commandId,
                executable = "/data/data/com.termux/files/usr/bin/bash",
                args = arrayOf("-c", command),
                workdir = defaultWorkdir,
                background = true,
                sessionAction = "com.harrowhaus.crypseal.TERMUX_RESULT"
            )

            intentRunner.executeCommand(termuxCmd)

            val result = withTimeoutOrNull(timeoutMs) {
                deferred.await()
            }

            if (result != null) {
                // Emit COMMAND_END
                onEvent?.invoke(EventType.COMMAND_END, "{\"id\":\"$commandId\",\"exitCode\":${result.exitCode},\"stdout\":\"${result.stdout}\",\"stderr\":\"${result.stderr}\"}")
                return CommandOutput(result.exitCode, result.stdout, result.stderr)
            } else {
                return CommandOutput(-1, "", "Command timed out after $timeoutMs ms")
            }

        } finally {
            TermuxResultReceiver.unwatchCommand(commandId)
        }
    }
}
