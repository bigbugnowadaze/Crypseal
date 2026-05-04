package com.harrowhaus.crypseal.ui.screens

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
