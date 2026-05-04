import os

root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-shell-bridge\src\main\java\com\harrowhaus\crypseal\shellbridge"
os.makedirs(root, exist_ok=True)

setup_checker_kt = """package com.harrowhaus.crypseal.shellbridge

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class TermuxSetupChecker(private val context: Context) {

    fun isTermuxInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.termux", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun hasRunCommandPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            "com.termux.permission.RUN_COMMAND"
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getSetupState(): TermuxSetupState {
        if (!isTermuxInstalled()) return TermuxSetupState.NOT_INSTALLED
        if (!hasRunCommandPermission()) return TermuxSetupState.PERMISSION_DENIED
        return TermuxSetupState.READY
    }
}

enum class TermuxSetupState {
    NOT_INSTALLED,
    PERMISSION_DENIED,
    READY
}
"""

intent_runner_kt = """package com.harrowhaus.crypseal.shellbridge

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

data class TermuxCommand(
    val id: String,
    val executable: String,
    val args: Array<String>,
    val workdir: String,
    val background: Boolean = false,
    val sessionAction: String = "com.harrowhaus.crypseal.TERMUX_RESULT"
)

class TermuxIntentRunner(private val context: Context) {

    fun executeCommand(command: TermuxCommand) {
        val intent = Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra("com.termux.RUN_COMMAND_PATH", command.executable)
            putExtra("com.termux.RUN_COMMAND_ARGUMENTS", command.args)
            putExtra("com.termux.RUN_COMMAND_WORKDIR", command.workdir)
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", command.background)
            
            val resultIntent = Intent(command.sessionAction).apply {
                putExtra("command_id", command.id)
                // Set package explicitly to prevent broadcast leaking
                setPackage(context.packageName)
            }
            
            val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                command.id.hashCode(), 
                resultIntent, 
                flags
            )
            putExtra("com.termux.RUN_COMMAND_PENDING_INTENT", pendingIntent)
        }
        
        context.startService(intent)
    }
}
"""

result_receiver_kt = """package com.harrowhaus.crypseal.shellbridge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TermuxResultReceiver(
    private val onResult: (String, Int, String, String) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val commandId = intent.getStringExtra("command_id") ?: return
        val exitCode = intent.getIntExtra("com.termux.RUN_COMMAND_EXIT_CODE", -1)
        val stdout = intent.getStringExtra("com.termux.RUN_COMMAND_STDOUT") ?: ""
        val stderr = intent.getStringExtra("com.termux.RUN_COMMAND_STDERR") ?: ""
        
        onResult(commandId, exitCode, stdout, stderr)
    }
}
"""

process_monitor_kt = """package com.harrowhaus.crypseal.shellbridge

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProcessState(
    val commandId: String,
    val isRunning: Boolean,
    val stdoutTail: String
)

class TermuxProcessMonitor {
    private val activeProcesses = mutableMapOf<String, MutableStateFlow<ProcessState>>()

    fun monitorProcess(commandId: String): StateFlow<ProcessState> {
        return activeProcesses.getOrPut(commandId) {
            MutableStateFlow(ProcessState(commandId, true, ""))
        }
    }

    fun updateOutput(commandId: String, newOutput: String) {
        val flow = activeProcesses[commandId] ?: return
        val current = flow.value
        flow.value = current.copy(stdoutTail = current.stdoutTail + "\\n" + newOutput)
    }

    fun markFinished(commandId: String) {
        val flow = activeProcesses[commandId] ?: return
        val current = flow.value
        flow.value = current.copy(isRunning = false)
        activeProcesses.remove(commandId)
    }
}
"""

def write_file(filename, content):
    with open(os.path.join(root, filename), "w") as f:
        f.write(content)

write_file("TermuxSetupChecker.kt", setup_checker_kt)
write_file("TermuxIntentRunner.kt", intent_runner_kt)
write_file("TermuxResultReceiver.kt", result_receiver_kt)
write_file("TermuxProcessMonitor.kt", process_monitor_kt)

print("Epic B Implemented.")
