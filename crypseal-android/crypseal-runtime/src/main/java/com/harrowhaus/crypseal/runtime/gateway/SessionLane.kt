package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.storage.jsonl.JsonlEventWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionLane(
    private val sessionId: String,
    private val eventWriter: JsonlEventWriter
) {
    private val mutex = Mutex()
    private var isInterrupted = false
    
    private val _laneState = MutableStateFlow(LaneState.IDLE)
    val laneState: StateFlow<LaneState> = _laneState

    suspend fun enqueueAction(action: suspend () -> Unit) {
        // Enforce serialization using Mutex
        mutex.withLock {
            isInterrupted = false
            _laneState.value = LaneState.EXECUTING
            try {
                action()
            } finally {
                if (!isInterrupted) {
                    _laneState.value = LaneState.IDLE
                }
            }
        }
    }

    fun interrupt() {
        isInterrupted = true
        _laneState.value = LaneState.INTERRUPTED
    }

    fun checkInterrupt() {
        if (isInterrupted) {
            throw InterruptedException("Session lane execution interrupted.")
        }
    }

    fun emitEvent(type: EventType, payload: String) {
        val event = CrypsealEvent(
            sessionId = sessionId,
            type = type,
            payload = payload
        )
        eventWriter.appendEvent(sessionId, event)
    }
}

enum class LaneState {
    IDLE,
    EXECUTING,
    INTERRUPTED,
    WAITING_FOR_APPROVAL,
    FAILED,
    COMPLETED
}
