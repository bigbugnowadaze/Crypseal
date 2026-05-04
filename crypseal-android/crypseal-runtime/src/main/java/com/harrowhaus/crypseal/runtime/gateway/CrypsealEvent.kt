package com.harrowhaus.crypseal.runtime.gateway

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
