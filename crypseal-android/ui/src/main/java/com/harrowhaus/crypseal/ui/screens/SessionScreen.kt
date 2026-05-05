package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.gateway.EventType
import com.harrowhaus.crypseal.shellbridge.TermuxCommandRunner
import com.harrowhaus.crypseal.runtime.inference.RuntimeRegistry
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    projectId: String?,
    commandRunner: TermuxCommandRunner,
    events: List<CrypsealEvent>,
    onEventsChanged: (List<CrypsealEvent>) -> Unit,
    inputMessage: String,
    onInputMessageChanged: (String) -> Unit,
    runtimeRegistry: RuntimeRegistry
) {
    val scope = rememberCoroutineScope()
    val sessionId = projectId ?: "active_session"

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(projectId ?: "No Project Selected") })
        
        // Timeline
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                EventCard(event) { newEvent ->
                    onEventsChanged(events + newEvent)
                }
            }
        }
        
        // Guided Flow: Run Python Diagnostic
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Guided Flow", style = MaterialTheme.typography.labelSmall)
                    Text("Run Python Diagnostic", style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = {
                    scope.launch {
                        var currentEvents = events
                        currentEvents = currentEvents + CrypsealEvent(sessionId = sessionId, type = EventType.USER_MESSAGE, payload = "Run Python Diagnostic")
                        currentEvents = currentEvents + CrypsealEvent(sessionId = sessionId, type = EventType.COMMAND_START, payload = "{\"command\":\"python --version\"}")
                        onEventsChanged(currentEvents)
                        
                        val result = commandRunner.run("python --version")
                        
                        val payload = JSONObject().apply {
                            put("exitCode", result.exitCode)
                            put("stdout", result.stdout)
                            put("stderr", result.stderr)
                        }.toString()
                        
                        currentEvents = currentEvents + CrypsealEvent(sessionId = sessionId, type = EventType.COMMAND_END, payload = payload)
                        
                        if (result.exitCode != 0) {
                            currentEvents = currentEvents + CrypsealEvent(
                                sessionId = sessionId, 
                                type = EventType.AGENT_MESSAGE, 
                                payload = "Python is not installed in Termux yet. Open Termux and run: pkg install python"
                            )
                        } else {
                            currentEvents = currentEvents + CrypsealEvent(
                                sessionId = sessionId, 
                                type = EventType.AGENT_MESSAGE, 
                                payload = "Python is successfully installed and verified."
                            )
                        }
                        onEventsChanged(currentEvents)
                    }
                }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Run")
                }
            }
        }
        
        // Input Area
        Row(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { onInputMessageChanged(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Agent...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (inputMessage.isNotBlank()) {
                    onEventsChanged(events + CrypsealEvent(
                        sessionId = sessionId,
                        type = EventType.USER_MESSAGE,
                        payload = inputMessage
                    ))
                    onInputMessageChanged("")
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun EventCard(event: CrypsealEvent, onEventAction: ((CrypsealEvent) -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when(event.type) {
                EventType.USER_MESSAGE -> MaterialTheme.colorScheme.surfaceVariant
                EventType.AGENT_MESSAGE -> MaterialTheme.colorScheme.secondaryContainer
                EventType.APPROVAL_REQUEST -> MaterialTheme.colorScheme.errorContainer
                EventType.COMMAND_START, EventType.COMMAND_END -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = event.type.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            when (event.type) {
                EventType.COMMAND_END -> {
                    var parsedExitCode: Int? = null
                    var parsedStdout: String? = null
                    var parsedStderr: String? = null
                    var parseError = false
                    
                    try {
                        val json = JSONObject(event.payload)
                        parsedExitCode = json.optInt("exitCode")
                        parsedStdout = json.optString("stdout")
                        parsedStderr = json.optString("stderr")
                    } catch (e: Exception) {
                        parseError = true
                    }
                    
                    if (parseError) {
                        Text(event.payload)
                    } else {
                        Text("Exit Code: $parsedExitCode")
                        if (!parsedStdout.isNullOrBlank()) {
                            Text("STDOUT:\n$parsedStdout", style = MaterialTheme.typography.bodySmall)
                        }
                        if (!parsedStderr.isNullOrBlank()) {
                            Text("STDERR:\n$parsedStderr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                EventType.APPROVAL_REQUEST -> {
                    Text("Crypseal needs your approval before running this command.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(event.payload, style = MaterialTheme.typography.bodySmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { 
                            onEventAction?.invoke(CrypsealEvent(sessionId = event.sessionId, type = EventType.APPROVAL_RESPONSE, payload = "DENIED"))
                        }) { Text("Deny") }
                        Button(onClick = { 
                            onEventAction?.invoke(CrypsealEvent(sessionId = event.sessionId, type = EventType.APPROVAL_RESPONSE, payload = "APPROVED"))
                        }) { Text("Approve Once") }
                    }
                }
                else -> {
                    Text(event.payload, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
