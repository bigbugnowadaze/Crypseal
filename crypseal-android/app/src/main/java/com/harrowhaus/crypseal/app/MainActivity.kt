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

import com.harrowhaus.crypseal.runtime.inference.RuntimeRegistry
import com.harrowhaus.crypseal.runtime.inference.TermuxLlamaServerRuntime
import com.harrowhaus.crypseal.runtime.inference.LiteRtGemmaRuntime
import com.harrowhaus.crypseal.runtime.inference.RuntimeDescriptor
import com.harrowhaus.crypseal.runtime.inference.RuntimeHealth
import com.harrowhaus.crypseal.runtime.inference.RuntimeStatus
import com.harrowhaus.crypseal.runtime.inference.RuntimeType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val setupChecker = TermuxSetupChecker(this)
        val commandRunner = TermuxCommandRunner(this) { type, payload ->
            Log.d("CrypsealEvent", "Type: $type, Payload: $payload")
        }
        
        val runtimeRegistry = RuntimeRegistry().apply {
            register(
                RuntimeDescriptor("llama_server", "Termux Llama Server", RuntimeType.TERMUX_SERVER, "Local llama.cpp REST API"),
                RuntimeHealth(RuntimeStatus.READY) // Default to ready for now
            )
            register(
                RuntimeDescriptor("litert_gemma", "LiteRT Gemma 4", RuntimeType.LITE_RT, "On-device Gemma inference"),
                RuntimeHealth(RuntimeStatus.NEEDS_SETUP, "Model file missing")
            )
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AgentShell(setupChecker, commandRunner, runtimeRegistry)
                }
            }
        }
    }
}
