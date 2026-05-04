import os

root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\main\java\com\harrowhaus\crypseal\runtime"

os.makedirs(os.path.join(root, "gateway"), exist_ok=True)
os.makedirs(os.path.join(root, "storage", "jsonl"), exist_ok=True)

event_kt = """package com.harrowhaus.crypseal.runtime.gateway

import java.util.UUID

data class CrypsealEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val type: EventType,
    val createdAt: Long = System.currentTimeMillis(),
    val payload: String // Store JSON as string for easy JSONL appending without extra deps
)

enum class EventType {
    USER_MESSAGE,
    AGENT_MESSAGE,
    TOOL_CALL,
    TOOL_RESULT,
    APPROVAL_REQUEST,
    APPROVAL_RESPONSE,
    COMMAND_START,
    COMMAND_OUTPUT,
    COMMAND_END,
    ERROR
}
"""

with open(os.path.join(root, "gateway", "CrypsealEvent.kt"), "w") as f:
    f.write(event_kt)

jsonl_writer_kt = """package com.harrowhaus.crypseal.runtime.storage.jsonl

import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.gateway.EventType
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.FileReader

class JsonlEventWriter(private val sessionDir: File) {

    init {
        if (!sessionDir.exists()) {
            sessionDir.mkdirs()
        }
    }

    fun appendEvent(sessionId: String, event: CrypsealEvent) {
        val file = File(sessionDir, "$sessionId.jsonl")
        
        // Escape quotes in payload for valid JSON string representation if needed, 
        // but since payload is intended to be raw JSON, we insert it directly.
        val jsonString = \"\"\"{"eventId":"${event.eventId}","sessionId":"${event.sessionId}","type":"${event.type.name}","createdAt":${event.createdAt},"payload":${event.payload}}\"\"\"
        
        BufferedWriter(FileWriter(file, true)).use { writer ->
            writer.write(jsonString)
            writer.newLine()
        }
    }

    fun readEvents(sessionId: String): List<CrypsealEvent> {
        val file = File(sessionDir, "$sessionId.jsonl")
        if (!file.exists()) return emptyList()

        val events = mutableListOf<CrypsealEvent>()
        BufferedReader(FileReader(file)).use { reader ->
            var line = reader.readLine()
            while (line != null) {
                if (line.isNotBlank()) {
                    events.add(parseEvent(line))
                }
                line = reader.readLine()
            }
        }
        return events
    }

    private fun parseEvent(json: String): CrypsealEvent {
        // Simple manual parsing to avoid adding Gson/Kotlinx deps immediately
        val eventId = extractString(json, "eventId")
        val sessionId = extractString(json, "sessionId")
        val typeStr = extractString(json, "type")
        val createdAt = extractNumber(json, "createdAt")
        val payload = extractJsonObject(json, "payload")

        return CrypsealEvent(
            eventId = eventId,
            sessionId = sessionId,
            type = EventType.valueOf(typeStr),
            createdAt = createdAt,
            payload = payload
        )
    }

    private fun extractString(json: String, key: String): String {
        val search = "\\"$key\\":\\""
        val start = json.indexOf(search)
        if (start == -1) return ""
        val valueStart = start + search.length
        val end = json.indexOf("\\"", valueStart)
        return json.substring(valueStart, end)
    }

    private fun extractNumber(json: String, key: String): Long {
        val search = "\\"$key\\":"
        val start = json.indexOf(search)
        if (start == -1) return 0L
        val valueStart = start + search.length
        var end = valueStart
        while (end < json.length && json[end].isDigit()) {
            end++
        }
        return json.substring(valueStart, end).toLongOrNull() ?: 0L
    }

    private fun extractJsonObject(json: String, key: String): String {
        val search = "\\"$key\\":"
        val start = json.indexOf(search)
        if (start == -1) return "{}"
        val valueStart = start + search.length
        var end = valueStart
        var openBraces = 0
        for (i in valueStart until json.length) {
            if (json[i] == '{') openBraces++
            if (json[i] == '}') openBraces--
            if (openBraces == 0 && json[i] == '}') {
                end = i + 1
                break
            }
        }
        return json.substring(valueStart, end)
    }
}
"""

with open(os.path.join(root, "storage", "jsonl", "JsonlEventWriter.kt"), "w") as f:
    f.write(jsonl_writer_kt)

# Create a test file
test_root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\test\java\com\harrowhaus\crypseal\runtime\storage\jsonl"
os.makedirs(test_root, exist_ok=True)

test_kt = """package com.harrowhaus.crypseal.runtime.storage.jsonl

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
            payload = \"\"\"{"text": "Hello Crypseal"}\"\"\"
        )

        val event2 = CrypsealEvent(
            sessionId = sessionId,
            type = EventType.TOOL_CALL,
            payload = \"\"\"{"tool": "run_command", "args": {"cmd": "ls"}}\"\"\"
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
"""

with open(os.path.join(test_root, "JsonlEventWriterTest.kt"), "w") as f:
    f.write(test_kt)

print("A02 Implemented.")
