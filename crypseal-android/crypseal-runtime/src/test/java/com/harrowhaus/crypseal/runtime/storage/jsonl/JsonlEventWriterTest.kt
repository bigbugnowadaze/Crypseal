package com.harrowhaus.crypseal.runtime.storage.jsonl

import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.gateway.EventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class JsonlEventWriterTest {

    @Test
    fun testAppendAndReplay() {
        val tempDir = Files.createTempDirectory("crypseal_events").toFile()
        val writer = JsonlEventWriter(tempDir)
        val sessionId = "session-123"

        val event1 = CrypsealEvent(
            sessionId = sessionId,
            type = EventType.USER_MESSAGE,
            payload = """{"text": "Hello Crypseal"}"""
        )

        val event2 = CrypsealEvent(
            sessionId = sessionId,
            type = EventType.TOOL_CALL,
            payload = """{"tool": "run_command", "args": {"cmd": "ls"}}"""
        )

        writer.appendEvent(sessionId, event1)
        writer.appendEvent(sessionId, event2)

        val replayed = writer.readEvents(sessionId)

        assertEquals(2, replayed.size)
        assertEquals(event1.eventId, replayed[0].eventId)
        assertEquals(event1.type, replayed[0].type)
        assertEquals(event1.payload, replayed[0].payload)
        
        assertEquals(event2.eventId, replayed[1].eventId)
        assertEquals(event2.type, replayed[1].type)
        assertEquals(event2.payload, replayed[1].payload)
        
        tempDir.deleteRecursively()
    }
}
