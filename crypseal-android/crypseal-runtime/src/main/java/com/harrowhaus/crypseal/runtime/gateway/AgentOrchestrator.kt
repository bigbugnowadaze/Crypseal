package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.tools.ToolArgParser
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry

class AgentOrchestrator(
    private val model: ModelRuntime,
    private val toolRegistry: ToolRegistry,
    private val contextBuilder: ContextBuilder,
    private val outputRepair: ModelOutputRepair,
    private val failureDetector: FailureDetector
) {

    companion object {
        private val MUTATING_TOOLS = setOf("apply_patch", "run_command", "git_commit")
    }

    suspend fun runActLoop(
        sessionHistory: MutableList<CrypsealEvent>,
        maxSteps: Int = 10,
        isPlanMode: Boolean = false
    ): OrchestrationResult {

        for (step in 1..maxSteps) {
            val context = contextBuilder.buildContext(sessionHistory, isPlanMode)
            val rawResponse = model.generateResponse(context)

            // Resolve the effective tool call:
            // 1. If the model returned structured fields, use them directly.
            // 2. Otherwise, attempt repair on the raw text.
            val response = resolveResponse(rawResponse)

            // Log the model's text as an assistant message
            sessionHistory.add(CrypsealEvent(
                sessionId = "active",
                type = EventType.AGENT_MESSAGE,
                payload = response.text
            ))

            val toolName = response.toolCallName
            if (toolName == null) {
                // No tool call — the model finished its turn
                return OrchestrationResult(true, "Agent completed autonomous step.")
            }

            // ---- Plan Mode gate ----
            if (isPlanMode && isMutatingTool(toolName)) {
                sessionHistory.add(CrypsealEvent(
                    sessionId = "active",
                    type = EventType.TOOL_RESULT,
                    payload = "Error: Cannot execute mutating tool '$toolName' in PLAN mode."
                ))
                continue
            }

            // ---- Failure loop check ----
            val toolSignature = "$toolName:${response.toolCallArgsJson.orEmpty()}"
            if (failureDetector.isLooping(toolSignature)) {
                return OrchestrationResult(false, "Failure loop detected on tool: $toolName")
            }

            // ---- Emit TOOL_CALL event ----
            sessionHistory.add(CrypsealEvent(
                sessionId = "active",
                type = EventType.TOOL_CALL,
                payload = "{\"tool\":\"$toolName\",\"args\":${response.toolCallArgsJson ?: "{}"}}"
            ))

            // ---- Resolve tool ----
            val tool = toolRegistry.getTool(toolName)
            if (tool == null) {
                failureDetector.recordFailure(toolSignature)
                sessionHistory.add(CrypsealEvent(
                    sessionId = "active",
                    type = EventType.TOOL_RESULT,
                    payload = "Error: Tool '$toolName' not found."
                ))
                continue
            }

            // ---- Parse args ----
            val parseResult = ToolArgParser.parse(response.toolCallArgsJson)
            if (!parseResult.success) {
                failureDetector.recordFailure(toolSignature)
                sessionHistory.add(CrypsealEvent(
                    sessionId = "active",
                    type = EventType.TOOL_RESULT,
                    payload = "Error: ${parseResult.error}"
                ))
                continue
            }

            // ---- Execute tool with real parsed args ----
            val toolResult = tool.execute(parseResult.args!!)

            // ---- Record outcome ----
            if (toolResult.success) {
                failureDetector.recordSuccess()
            } else {
                failureDetector.recordFailure(toolSignature)
            }

            sessionHistory.add(CrypsealEvent(
                sessionId = "active",
                type = EventType.TOOL_RESULT,
                payload = if (toolResult.success) toolResult.output
                          else "Error: ${toolResult.error ?: toolResult.output}"
            ))
        }

        return OrchestrationResult(false, "Max steps reached.")
    }

    /**
     * If the model already returned structured tool call fields, use them.
     * Otherwise fall back to text-based repair as a last resort.
     */
    private fun resolveResponse(raw: ModelResponse): ModelResponse {
        if (raw.toolCallName != null) {
            return raw
        }
        // Only attempt repair if the text looks like it might contain a tool call
        val text = raw.text
        if (text.contains("\"tool\"") || text.contains("\"name\"") || text.contains("{")) {
            return outputRepair.repairToolCall(text)
        }
        return raw
    }

    private fun isMutatingTool(name: String): Boolean {
        return name in MUTATING_TOOLS
    }
}

data class OrchestrationResult(
    val success: Boolean,
    val reason: String
)
