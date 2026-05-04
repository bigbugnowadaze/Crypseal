package com.harrowhaus.crypseal.shellbridge

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
