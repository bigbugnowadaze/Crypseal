package com.harrowhaus.crypseal.runtime.storage.jsonl

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
        val jsonString = """{"eventId":"${event.eventId}","sessionId":"${event.sessionId}","type":"${event.type.name}","createdAt":${event.createdAt},"payload":${event.payload}}"""
        
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
        val search = "\"$key\":\""
        val start = json.indexOf(search)
        if (start == -1) return ""
        val valueStart = start + search.length
        val end = json.indexOf("\"", valueStart)
        return json.substring(valueStart, end)
    }

    private fun extractNumber(json: String, key: String): Long {
        val search = "\"$key\":"
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
        val search = "\"$key\":"
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
