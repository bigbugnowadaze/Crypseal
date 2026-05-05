package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.harrowhaus.crypseal.runtime.inference.RuntimeRegistry

@Composable
fun SettingsScreen(
    activeRuntime: String = "Mock / Default",
    permissionProfile: String = "ASK / Medium",
    runtimeRegistry: RuntimeRegistry? = null
) {
    val activeRuntimeId = runtimeRegistry?.activeRuntimeId?.collectAsState()
    val availableRuntimes = runtimeRegistry?.getAvailableRuntimes() ?: emptyList()
    
    val currentSelection = availableRuntimes.find { it.descriptor.id == activeRuntimeId?.value }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Runtime & Policy Settings", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Active Runtime: ${currentSelection?.descriptor?.name ?: activeRuntime}", style = MaterialTheme.typography.bodyLarge)
        Text("Runtime Status: ${currentSelection?.health?.status?.name ?: "UNKNOWN"}", style = MaterialTheme.typography.bodyMedium)
        if (currentSelection?.health?.message != null) {
            Text("Message: ${currentSelection.health.message}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Permission Profile: $permissionProfile", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Available Runtimes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        availableRuntimes.forEach { selection ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                RadioButton(
                    selected = selection.descriptor.id == activeRuntimeId?.value,
                    onClick = { runtimeRegistry?.setActiveRuntime(selection.descriptor.id) }
                )
                Column {
                    Text(selection.descriptor.name, style = MaterialTheme.typography.bodyLarge)
                    Text(selection.descriptor.description, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
