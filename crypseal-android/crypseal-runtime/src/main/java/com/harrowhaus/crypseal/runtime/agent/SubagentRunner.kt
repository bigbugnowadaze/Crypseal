package com.harrowhaus.crypseal.runtime.agent

import com.harrowhaus.crypseal.runtime.gateway.AgentOrchestrator
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry

class SubagentRunner(
    private val baseOrchestrator: AgentOrchestrator,
    private val fullRegistry: ToolRegistry
) {

    suspend fun runExploreAgent(prompt: String): String {
        // ExploreAgent is read-only. We mock building a restricted tool registry.
        val restrictedRegistry = ToolRegistry()
        
        fullRegistry.getTool("read_file")?.let { restrictedRegistry.register(it) }
        fullRegistry.getTool("git_status")?.let { restrictedRegistry.register(it) }
        
        // Normally we'd spawn a fresh orchestrator instance configured with the restrictedRegistry
        // and runActLoop(emptyHistory) returning the summary.
        // For the scaffold, we simulate the restriction successfully.
        
        val summary = "ExploreAgent evaluated: '$prompt' using restricted tools."
        return summary
    }
}
