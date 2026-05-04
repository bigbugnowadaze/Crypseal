package com.harrowhaus.crypseal.ui.components

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
