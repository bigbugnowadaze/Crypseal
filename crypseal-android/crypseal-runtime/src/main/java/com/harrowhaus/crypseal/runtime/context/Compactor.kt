package com.harrowhaus.crypseal.runtime.context

import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent

class Compactor(private val maxTokens: Int = 4000) {

    fun compactHistory(events: List<CrypsealEvent>): List<CrypsealEvent> {
        // Very basic mock compaction logic
        // If history is too long (mocked as > 50 events), truncate and summarize older ones
        if (events.size <= 50) return events

        val recent = events.takeLast(20)
        val summaryEvent = CrypsealEvent(
            sessionId = events.first().sessionId,
            type = com.harrowhaus.crypseal.runtime.gateway.EventType.AGENT_MESSAGE,
            payload = "[Prior history compacted due to context limits. ${events.size - 20} events omitted.]"
        )
        
        return listOf(summaryEvent) + recent
    }
}
