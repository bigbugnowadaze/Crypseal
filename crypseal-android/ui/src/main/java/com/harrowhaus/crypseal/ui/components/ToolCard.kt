package com.harrowhaus.crypseal.ui.components

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
