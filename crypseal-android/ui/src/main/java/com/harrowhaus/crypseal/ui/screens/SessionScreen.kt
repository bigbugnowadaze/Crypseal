package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.gateway.EventType
import com.harrowhaus.crypseal.shellbridge.TermuxCommandRunner
import com.harrowhaus.crypseal.runtime.inference.RuntimeRegistry
import kotlinx.coroutines.launch
import org.json.JSONObject

import com.harrowhaus.crypseal.runtime.gateway.*
import com.harrowhaus.crypseal.runtime.context.*
import com.harrowhaus.crypseal.runtime.tools.*
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    projectPath: String?,
    commandRunner: TermuxCommandRunner,
    events: List<CrypsealEvent>,
    onEventsChanged: (List<CrypsealEvent>) -> Unit,
    inputMessage: String,
    onInputMessageChanged: (String) -> Unit,
    runtimeRegistry: RuntimeRegistry
) {
    val scope = rememberCoroutineScope()
    val sessionId = projectPath ?: "active_session"
    val projectRoot = projectPath?.let { File(it) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(projectRoot?.name ?: "No Project Selected") })
        
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
        
        // M7 Real Agent Task Button
        if (projectRoot != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("M7 Task", style = MaterialTheme.typography.labelSmall)
                        Text("Read main.py and explain", style = MaterialTheme.typography.titleMedium)
                    }
                    Button(onClick = {
                        scope.launch {
                            val activeId = runtimeRegistry.activeRuntimeId.value ?: return@launch
                            val model = runtimeRegistry.getRuntime(activeId) ?: return@launch
                            
                            val toolRegistry = ToolRegistry().apply {
                                register(FileReadTool(projectRoot))
                            }
                            
                            val orchestrator = AgentOrchestrator(
                                model = model,
                                toolRegistry = toolRegistry,
                                contextBuilder = ContextBuilder(projectRoot, Compactor()),
                                outputRepair = ModelOutputRepair(),
                                failureDetector = FailureDetector(),
                                projectRoot = projectRoot
                            )
                            
                            val currentEvents = events.toMutableList()
                            currentEvents.add(CrypsealEvent(sessionId = sessionId, type = EventType.USER_MESSAGE, payload = "Read main.py and explain what it does."))
                            onEventsChanged(currentEvents.toList())
                            
                            orchestrator.runActLoop(currentEvents, maxSteps = 3)
                            onEventsChanged(currentEvents.toList())
                        }
                    }) {
                        Text("Run")
                    }
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
