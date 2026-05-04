package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    projectName: String,
    events: List<Any>, // Mock type for event stream
    onSendMessage: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Project: $projectName") })
        
        LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
            items(events.size) { index ->
                // Render ToolCards or MessageCards based on event type
                Text("Event ${index + 1}")
            }
        }
        
        Row(modifier = Modifier.padding(8.dp)) {
            // Input field and send button would go here
            Button(onClick = { onSendMessage("Mock Action") }) {
                Text("Send / Steer")
            }
        }
    }
}
