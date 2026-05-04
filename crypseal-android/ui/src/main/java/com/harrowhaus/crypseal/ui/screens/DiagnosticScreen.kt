package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harrowhaus.crypseal.shellbridge.TermuxSetupChecker
import com.harrowhaus.crypseal.shellbridge.TermuxCommandRunner
import com.harrowhaus.crypseal.shellbridge.TermuxSetupState
import kotlinx.coroutines.launch

@Composable
fun DiagnosticScreen(
    setupChecker: TermuxSetupChecker,
    commandRunner: TermuxCommandRunner
) {
    var setupState by remember { mutableStateOf(setupChecker.getSetupState()) }
    var outputText by remember { mutableStateOf("Ready to run diagnostics...") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = "Crypseal Diagnostics", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Setup status
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Termux Setup Status", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                StatusRow(label = "Installed", value = setupChecker.isTermuxInstalled())
                StatusRow(label = "RUN_COMMAND permission", value = setupChecker.hasRunCommandPermission())
                Text(
                    text = "Overall: $setupState",
                    color = if (setupState == TermuxSetupState.READY) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = { setupState = setupChecker.getSetupState() }) {
                    Text("Refresh Status")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Commands
        Text(text = "Diagnostic Commands", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DiagnosticButton("pwd", commandRunner, scope) { outputText = it }
            DiagnosticButton("ls", commandRunner, scope) { outputText = it }
            DiagnosticButton("python --version", commandRunner, scope) { outputText = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Output area
        Text(text = "Output", style = MaterialTheme.typography.titleMedium)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = outputText,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatusRow(label: String, value: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label)
        Text(
            text = if (value) "YES" else "NO",
            color = if (value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DiagnosticButton(
    label: String,
    runner: TermuxCommandRunner,
    scope: kotlinx.coroutines.CoroutineScope,
    onOutput: (String) -> Unit
) {
    Button(onClick = {
        scope.launch {
            onOutput("Running $label...")
            val result = runner.run(label)
            onOutput("Exit code: ${result.exitCode}\nSTDOUT:\n${result.stdout}\nSTDERR:\n${result.stderr}")
        }
    }) {
        Text(label.split(" ")[0])
    }
}
