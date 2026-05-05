package com.harrowhaus.crypseal.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.harrowhaus.crypseal.shellbridge.TermuxCommandRunner
import com.harrowhaus.crypseal.shellbridge.TermuxSetupChecker
import com.harrowhaus.crypseal.ui.screens.AgentShell

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val setupChecker = TermuxSetupChecker(this)
        val commandRunner = TermuxCommandRunner(this) { type, payload ->
            Log.d("CrypsealEvent", "Type: $type, Payload: $payload")
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AgentShell(setupChecker, commandRunner)
                }
            }
        }
    }
}
