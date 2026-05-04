package com.harrowhaus.crypseal.shellbridge

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
