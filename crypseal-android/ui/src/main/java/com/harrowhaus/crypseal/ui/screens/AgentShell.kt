package com.harrowhaus.crypseal.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.harrowhaus.crypseal.shellbridge.TermuxCommandRunner
import com.harrowhaus.crypseal.shellbridge.TermuxSetupChecker

enum class ShellRoute(val title: String, val icon: ImageVector) {
    PROJECTS("Projects", Icons.Default.List),
    SESSION("Session", Icons.Default.PlayArrow),
    DIAGNOSTICS("Diagnostics", Icons.Default.Build),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun AgentShell(
    setupChecker: TermuxSetupChecker,
    commandRunner: TermuxCommandRunner
) {
    var currentRoute by remember { mutableStateOf(ShellRoute.SESSION) }
    var currentProjectId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation)
            ) {
                ShellRoute.values().forEach { route ->
                    NavigationBarItem(
                        icon = { Icon(route.icon, contentDescription = route.title) },
                        label = { Text(route.title) },
                        selected = currentRoute == route,
                        onClick = { currentRoute = route }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentRoute) {
                ShellRoute.PROJECTS -> ProjectListScreen(
                    onProjectSelected = { id ->
                        currentProjectId = id
                        currentRoute = ShellRoute.SESSION
                    }
                )
                ShellRoute.SESSION -> SessionScreen(
                    projectId = currentProjectId,
                    commandRunner = commandRunner
                )
                ShellRoute.DIAGNOSTICS -> DiagnosticScreen(
                    setupChecker = setupChecker,
                    commandRunner = commandRunner
                )
                ShellRoute.SETTINGS -> SettingsScreen()
            }
        }
    }
}
