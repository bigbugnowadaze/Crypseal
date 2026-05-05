package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProjectListScreen(
    onProjectSelected: (String) -> Unit
) {
    // In M5, we can use an app-private list or a safe Termux default path.
    // For now, let's show a minimal stateful list.
    var projects by remember { mutableStateOf(listOf("Termux Local", "Diagnostic Target")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Projects", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = { projects = projects + "New Project ${projects.size + 1}" }) {
                Icon(Icons.Default.Add, contentDescription = "Create Project")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (projects.isEmpty()) {
            Text("No projects found.", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(projects) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProjectSelected(project) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(project, style = MaterialTheme.typography.titleMedium)
                            Text("Path: /data/data/com.termux/files/home/$project", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
