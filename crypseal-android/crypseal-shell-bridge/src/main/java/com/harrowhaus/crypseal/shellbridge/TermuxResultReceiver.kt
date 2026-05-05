package com.harrowhaus.crypseal.shellbridge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CompletableDeferred

class TermuxResultReceiver : BroadcastReceiver() {

    companion object {
        private val pendingCommands = mutableMapOf<String, CompletableDeferred<ResultData>>()

        fun watchCommand(commandId: String): CompletableDeferred<ResultData> {
            val deferred = CompletableDeferred<ResultData>()
            pendingCommands[commandId] = deferred
            return deferred
        }

        fun unwatchCommand(commandId: String) {
            pendingCommands.remove(commandId)
        }
    }

    data class ResultData(val exitCode: Int, val stdout: String, val stderr: String)

    override fun onReceive(context: Context, intent: Intent) {
        val commandId = intent.getStringExtra("command_id") ?: return
        
        val resultBundle = intent.getBundleExtra("result")
        if (resultBundle != null) {
            for (key in resultBundle.keySet()) {
                Log.e("TermuxResult", "Result Bundle Extra: $key = ${resultBundle.get(key)}")
            }
        }

        // Try getting from bundle first (using short keys), fallback to intent (long keys)
        val exitCode = resultBundle?.getInt("exitCode", -1) 
            ?: intent.getIntExtra("com.termux.RUN_COMMAND_EXIT_CODE", -1)
            
        val stdout = resultBundle?.getString("stdout") 
            ?: intent.getStringExtra("com.termux.RUN_COMMAND_STDOUT") ?: ""
            
        val stderr = resultBundle?.getString("stderr") 
            ?: intent.getStringExtra("com.termux.RUN_COMMAND_STDERR") ?: ""
        
        Log.e("TermuxResult", "Parsed result for $commandId: exit=$exitCode, stdout_len=${stdout.length}")
        
        pendingCommands[commandId]?.complete(ResultData(exitCode, stdout, stderr))
    }

}
