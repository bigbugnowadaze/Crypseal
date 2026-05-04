import os

root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\main\java\com\harrowhaus\crypseal\runtime\gateway"
os.makedirs(root, exist_ok=True)

gateway_kt = """package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.storage.jsonl.JsonlEventWriter
import com.harrowhaus.crypseal.runtime.storage.db.ProjectEntity
import com.harrowhaus.crypseal.runtime.storage.db.SessionEntity
import com.harrowhaus.crypseal.runtime.storage.db.CrypsealDatabase
import java.util.UUID

class CrypsealGateway(
    private val db: CrypsealDatabase,
    private val eventWriter: JsonlEventWriter
) {
    private val activeLanes = mutableMapOf<String, SessionLane>()

    fun createProject(name: String, rootPath: String): ProjectEntity {
        val project = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            rootPath = rootPath,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString(),
            lastSessionId = null
        )
        db.projectDao().insertProject(project)
        return project
    }

    fun createSession(projectId: String, title: String?): SessionEntity {
        val session = SessionEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            title = title,
            state = "Idle",
            jsonlPath = "",
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString()
        )
        db.sessionDao().insertSession(session)
        return session
    }

    fun getLane(sessionId: String): SessionLane {
        return activeLanes.getOrPut(sessionId) {
            SessionLane(sessionId, eventWriter)
        }
    }
}
"""

session_lane_kt = """package com.harrowhaus.crypseal.runtime.gateway

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
"""

with open(os.path.join(root, "CrypsealGateway.kt"), "w") as f:
    f.write(gateway_kt)

with open(os.path.join(root, "SessionLane.kt"), "w") as f:
    f.write(session_lane_kt)

test_root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\test\java\com\harrowhaus\crypseal\runtime\gateway"
os.makedirs(test_root, exist_ok=True)

test_kt = """package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.storage.jsonl.JsonlEventWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class SessionLaneTest {

    @Test
    fun testSerializationAndInterrupt() = runBlocking {
        val tempDir = Files.createTempDirectory("crypseal_events").toFile()
        val writer = JsonlEventWriter(tempDir)
        val lane = SessionLane("test-session", writer)
        
        var counter = 0
        var interrupted = false
        
        launch(Dispatchers.Default) {
            lane.enqueueAction {
                delay(50)
                try {
                    lane.checkInterrupt()
                    counter++
                } catch (e: InterruptedException) {
                    interrupted = true
                }
            }
        }
        
        launch(Dispatchers.Default) {
            delay(10)
            lane.interrupt()
        }
        
        delay(100)
        assertTrue(interrupted)
        assertEquals(0, counter)
        
        tempDir.deleteRecursively()
    }
}
"""

with open(os.path.join(test_root, "SessionLaneTest.kt"), "w") as f:
    f.write(test_kt)

print("A04 and A05 Implemented.")
