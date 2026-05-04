package com.harrowhaus.crypseal.shellbridge

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProcessState(
    val commandId: String,
    val isRunning: Boolean,
    val stdoutTail: String
)

class TermuxProcessMonitor {
    private val activeProcesses = mutableMapOf<String, MutableStateFlow<ProcessState>>()

    fun monitorProcess(commandId: String): StateFlow<ProcessState> {
        return activeProcesses.getOrPut(commandId) {
            MutableStateFlow(ProcessState(commandId, true, ""))
        }
    }

    fun updateOutput(commandId: String, newOutput: String) {
        val flow = activeProcesses[commandId] ?: return
        val current = flow.value
        flow.value = current.copy(stdoutTail = current.stdoutTail + "\n" + newOutput)
    }

    fun markFinished(commandId: String) {
        val flow = activeProcesses[commandId] ?: return
        val current = flow.value
        flow.value = current.copy(isRunning = false)
        activeProcesses.remove(commandId)
    }
}
