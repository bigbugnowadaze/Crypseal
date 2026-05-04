package com.harrowhaus.crypseal.runtime.context

import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.models.ModelMessage
import java.io.File

class ContextBuilder(
    private val projectRoot: File,
    private val compactor: Compactor
) {
    fun buildContext(sessionHistory: List<CrypsealEvent>, isPlanMode: Boolean): List<ModelMessage> {
        val messages = mutableListOf<ModelMessage>()
        
        // 1. System Instruction
        val systemPrompt = buildString {
            append("You are Crypseal, a local Android coding agent.\n")
            if (isPlanMode) {
                append("MODE: PLAN_ONLY. You may only use read/search tools. Formulate a plan.\n")
            } else {
                append("MODE: ACT. You may edit files and execute commands.\n")
            }
            
            val agentMd = File(projectRoot, ".crypseal/AGENT.md")
            if (agentMd.exists()) {
                append("PROJECT INSTRUCTIONS:\n${agentMd.readText()}\n")
            }
        }
        messages.add(ModelMessage("system", systemPrompt))
        
        // 2. Compacted History
        val compactedHistory = compactor.compactHistory(sessionHistory)
        
        // 3. Assemble
        for (event in compactedHistory) {
            val role = if (event.type.name.contains("USER")) "user" else "assistant"
            messages.add(ModelMessage(role, event.payload))
        }
        
        return messages
    }
}
