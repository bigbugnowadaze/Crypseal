package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry

class AgentOrchestrator(
    private val model: ModelRuntime,
    private val toolRegistry: ToolRegistry,
    private val contextBuilder: ContextBuilder,
    private val outputRepair: ModelOutputRepair,
    private val failureDetector: FailureDetector
) {

    suspend fun runActLoop(
        sessionHistory: MutableList<CrypsealEvent>, 
        maxSteps: Int = 10,
        isPlanMode: Boolean = false
    ): OrchestrationResult {
        
        for (step in 1..maxSteps) {
            val context = contextBuilder.buildContext(sessionHistory, isPlanMode)
            val rawResponse = model.generateResponse(context)
            val response = outputRepair.repairToolCall(rawResponse.text) // or rawResponse directly
            
            // Log model message
            sessionHistory.add(CrypsealEvent(
                sessionId = "mock",
                type = EventType.AGENT_MESSAGE,
                payload = response.text
            ))

            if (response.toolCallName != null) {
                // If Plan Mode, block mutating tools
                if (isPlanMode && isMutatingTool(response.toolCallName)) {
                    val rejectEvent = CrypsealEvent(
                        sessionId = "mock",
                        type = EventType.TOOL_RESULT,
                        payload = "Error: Cannot execute mutating tool '${response.toolCallName}' in PLAN mode."
                    )
                    sessionHistory.add(rejectEvent)
                    continue
                }

                // Check Failure Loop
                val toolJsonString = "${response.toolCallName}:${response.toolCallArgsJson}"
                if (failureDetector.isLooping(toolJsonString)) {
                    return OrchestrationResult(false, "Failure loop detected on tool: ${response.toolCallName}")
                }

                val tool = toolRegistry.getTool(response.toolCallName)
                if (tool == null) {
                    failureDetector.recordFailure(toolJsonString)
                    sessionHistory.add(CrypsealEvent(
                        sessionId = "mock",
                        type = EventType.TOOL_RESULT,
                        payload = "Error: Tool '${response.toolCallName}' not found."
                    ))
                    continue
                }

                // Execute tool
                val result = tool.execute(emptyMap()) // Mock empty args parsed from json
                if (result.success) {
                    failureDetector.recordSuccess()
                } else {
                    failureDetector.recordFailure(toolJsonString)
                }

                sessionHistory.add(CrypsealEvent(
                    sessionId = "mock",
                    type = EventType.TOOL_RESULT,
                    payload = result.output
                ))
            } else {
                // No tool call means the model stopped acting and is just chatting
                return OrchestrationResult(true, "Agent completed autonomous step.")
            }
        }
        
        return OrchestrationResult(false, "Max steps reached.")
    }

    private fun isMutatingTool(name: String): Boolean {
        // Mock hardcoded list for Plan mode
        return name in listOf("apply_patch", "run_command", "git_commit")
    }
}

data class OrchestrationResult(
    val success: Boolean,
    val reason: String
)
