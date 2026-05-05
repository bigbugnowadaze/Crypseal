package com.harrowhaus.crypseal.shellbridge

import android.content.Context
import android.content.IntentFilter
import com.harrowhaus.crypseal.runtime.gateway.EventType
import com.harrowhaus.crypseal.runtime.tools.CommandOutput
import com.harrowhaus.crypseal.runtime.tools.CommandRunner
import kotlinx.coroutines.CompletableDeferred
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
        val deferred = CompletableDeferred<CommandOutput>()

        // Emit COMMAND_START
        onEvent?.invoke(EventType.COMMAND_START, "{\"id\":\"$commandId\",\"command\":\"$command\",\"workdir\":\"$defaultWorkdir\"}")

        // For simplicity in this milestone, we register a one-shot receiver.
        val receiver = TermuxResultReceiver { id, exitCode, stdout, stderr ->
            if (id == commandId) {
                // Emit COMMAND_END
                onEvent?.invoke(EventType.COMMAND_END, "{\"id\":\"$id\",\"exitCode\":$exitCode,\"stdout\":\"$stdout\",\"stderr\":\"$stderr\"}")
                deferred.complete(CommandOutput(exitCode, stdout, stderr))
            }
        }

        val action = "com.harrowhaus.crypseal.TERMUX_RESULT_$commandId"
        
        // Register receiver
        val filter = IntentFilter(action)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        try {
            // Build TermuxCommand
            val termuxCmd = TermuxCommand(
                id = commandId,
                executable = "/data/data/com.termux/files/usr/bin/bash",
                args = arrayOf("-c", command),
                workdir = defaultWorkdir,
                background = true, // We want stdout/stderr via PendingIntent
                sessionAction = action
            )

            intentRunner.executeCommand(termuxCmd)

            val result = withTimeoutOrNull(timeoutMs) {
                deferred.await()
            }

            return result ?: CommandOutput(-1, "", "Command timed out after $timeoutMs ms")

        } finally {
            context.unregisterReceiver(receiver)
        }
    }
}
