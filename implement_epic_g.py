import os

root = r"c:\CHANGERS\Crypseal\crypseal-android\ui\src\main\java\com\harrowhaus\crypseal\ui"
os.makedirs(os.path.join(root, "screens"), exist_ok=True)
os.makedirs(os.path.join(root, "components"), exist_ok=True)
os.makedirs(os.path.join(root, "navigation"), exist_ok=True)
os.makedirs(os.path.join(root, "notifications"), exist_ok=True)

# Screens
project_screen_kt = """package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
"""

settings_screen_kt = """package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    activeRuntime: String,
    permissionProfile: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Runtime & Policy Settings", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Active Runtime: $activeRuntime", style = MaterialTheme.typography.bodyLarge)
        Text("Permission Profile: $permissionProfile", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { /* Switch Runtime */ }) {
            Text("Configure Runtime")
        }
    }
}
"""

# Components
tool_card_kt = """package com.harrowhaus.crypseal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolCard(
    toolName: String,
    content: String,
    isApprovalRequired: Boolean,
    onApprove: () -> Unit = {},
    onDeny: () -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Tool: $toolName", style = MaterialTheme.typography.labelLarge)
            Text(content, style = MaterialTheme.typography.bodyMedium)
            
            if (isApprovalRequired) {
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = onApprove) { Text("Approve") }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = onDeny) { Text("Deny") }
                }
            }
        }
    }
}
"""

diff_viewer_kt = """package com.harrowhaus.crypseal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiffViewer(
    targetFile: String,
    diffContent: String,
    onApply: () -> Unit,
    onReject: () -> Unit,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Patch: $targetFile", style = MaterialTheme.typography.titleMedium)
            
            // Mock Diff highlighting
            Text(diffContent, style = MaterialTheme.typography.bodySmall)
            
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onApply) { Text("Apply") }
                Button(onClick = onEdit) { Text("Edit") }
                OutlinedButton(onClick = onReject) { Text("Reject") }
            }
        }
    }
}
"""

# Notifications
notification_manager_kt = """package com.harrowhaus.crypseal.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class CrypsealNotificationManager(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "crypseal_agent_status"

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Agent Status",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun showApprovalNeeded(sessionId: String, toolName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Crypseal: Approval Required")
            .setContentText("Agent requests permission to run: $toolName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
            
        notificationManager.notify(sessionId.hashCode(), notification)
    }

    fun showActiveRun(sessionId: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Crypseal: Agent Active")
            .setContentText("Executing autonomous run...")
            .setOngoing(true)
            .build()
            
        notificationManager.notify(sessionId.hashCode(), notification)
    }

    fun clearActiveRun(sessionId: String) {
        notificationManager.cancel(sessionId.hashCode())
    }
}
"""

def write_file(folder, filename, content):
    with open(os.path.join(root, folder, filename), "w") as f:
        f.write(content)

write_file("screens", "ProjectScreen.kt", project_screen_kt)
write_file("screens", "SettingsScreen.kt", settings_screen_kt)
write_file("components", "ToolCard.kt", tool_card_kt)
write_file("components", "DiffViewer.kt", diff_viewer_kt)
write_file("notifications", "CrypsealNotificationManager.kt", notification_manager_kt)

print("Epic G Implemented.")
