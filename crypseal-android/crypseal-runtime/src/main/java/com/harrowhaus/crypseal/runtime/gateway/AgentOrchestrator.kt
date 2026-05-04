package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.guard.PolicyAction
import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.tools.ToolArgParser
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry
import java.io.File

class AgentOrchestrator(
    private val model: ModelRuntime,
    private val toolRegistry: ToolRegistry,
    private val contextBuilder: ContextBuilder,
    private val outputRepair: ModelOutputRepair,
    private val failureDetector: FailureDetector,
    private val policyGate: PolicyGate? = null,
    private val approvalCallback: ApprovalCallback? = null,
    private val sessionLane: SessionLane? = null,
    private val projectRoot: File? = null
) {

    companion object {
        private val MUTATING_TOOLS = setOf("apply_patch", "run_command", "git_commit", "write_file")
    }

    suspend fun runActLoop(
        sessionHistory: MutableList<CrypsealEvent>,
        maxSteps: Int = 10,
        isPlanMode: Boolean = false
    ): OrchestrationResult {

        for (step in 1..maxSteps) {
            val context = contextBuilder.buildContext(sessionHistory, isPlanMode)
            val rawResponse = model.generateResponse(context)
            val response = resolveResponse(rawResponse)

            // Log the model's text as an assistant message
            sessionHistory.add(event(EventType.AGENT_MESSAGE, response.text))

            val toolName = response.toolCallName
            if (toolName == null) {
                return OrchestrationResult(true, "Agent completed autonomous step.")
            }

            // ---- Plan Mode gate ----
            if (isPlanMode && isMutatingTool(toolName)) {
                sessionHistory.add(event(EventType.TOOL_RESULT,
                    "Error: Cannot execute mutating tool '$toolName' in PLAN mode."))
                continue
            }

            // ---- Failure loop check ----
            val toolSignature = "$toolName:${response.toolCallArgsJson.orEmpty()}"
            if (failureDetector.isLooping(toolSignature)) {
                return OrchestrationResult(false, "Failure loop detected on tool: $toolName")
            }

            // ---- Parse args ----
            val parseResult = ToolArgParser.parse(response.toolCallArgsJson)
            if (!parseResult.success) {
                failureDetector.recordFailure(toolSignature)
                sessionHistory.add(event(EventType.TOOL_CALL, toolCallPayload(toolName, response)))
                sessionHistory.add(event(EventType.TOOL_RESULT, "Error: ${parseResult.error}"))
                continue
            }
            val args = parseResult.args!!

            // ---- Policy gate ----
            if (policyGate != null) {
                val verdict = policyGate.evaluate(toolName, args)
                when (verdict.action) {
                    PolicyAction.DENY -> {
                        // DENY: never execute, never ask for approval
                        sessionHistory.add(event(EventType.TOOL_CALL, toolCallPayload(toolName, response)))
                        sessionHistory.add(event(EventType.TOOL_RESULT,
                            "DENIED: ${verdict.reason} [${verdict.riskLevel}]"))
                        failureDetector.recordFailure(toolSignature)
                        continue
                    }
                    PolicyAction.ASK -> {
                        // ASK: request approval via callback
                        sessionHistory.add(event(EventType.TOOL_CALL, toolCallPayload(toolName, response)))

                        val approvalRequest = ApprovalDriftChecker.bindRequest(
                            sessionId = "active",
                            toolName = toolName,
                            args = args,
                            verdict = verdict,
                            projectRoot = projectRoot
                        )

                        sessionHistory.add(event(EventType.APPROVAL_REQUEST,
                            "WAITING: ${verdict.reason} [${verdict.riskLevel}] (requestId=${approvalRequest.requestId})"))

                        if (approvalCallback == null) {
                            // No callback — cannot proceed, skip tool
                            continue
                        }

                        // Transition lane state
                        sessionLane?.setWaitingForApproval()

                        val approvalResponse = approvalCallback.requestApproval(approvalRequest)

                        // Restore lane state
                        sessionLane?.setExecuting()

                        // Emit approval response event
                        sessionHistory.add(event(EventType.APPROVAL_RESPONSE,
                            "${approvalResponse.decision}: requestId=${approvalResponse.requestId}" +
                            (if (approvalResponse.userNote != null) " note=${approvalResponse.userNote}" else "")))

                        if (approvalResponse.decision == ApprovalDecision.DENY) {
                            continue
                        }

                        // Approved — check for drift before executing
                        val drift = ApprovalDriftChecker.checkDrift(approvalRequest, args, projectRoot)
                        if (drift != null) {
                            sessionHistory.add(event(EventType.TOOL_RESULT,
                                "DRIFT_BLOCKED: $drift"))
                            failureDetector.recordFailure(toolSignature)
                            continue
                        }

                        // Fall through to execution
                    }
                    PolicyAction.ALLOW -> {
                        // Fall through to execution — no approval needed
                    }
                }
            }

            // ---- Emit TOOL_CALL event (for ALLOW path) ----
            // Only emit if not already emitted by ASK/DENY paths above
            if (policyGate == null || policyGate.evaluate(toolName, args).action == PolicyAction.ALLOW) {
                sessionHistory.add(event(EventType.TOOL_CALL, toolCallPayload(toolName, response)))
            }

            // ---- Resolve tool ----
            val tool = toolRegistry.getTool(toolName)
            if (tool == null) {
                failureDetector.recordFailure(toolSignature)
                sessionHistory.add(event(EventType.TOOL_RESULT, "Error: Tool '$toolName' not found."))
                continue
            }

            // ---- Execute tool with real parsed args ----
            val toolResult = tool.execute(args)

            // ---- Record outcome ----
            if (toolResult.success) {
                failureDetector.recordSuccess()
            } else {
                failureDetector.recordFailure(toolSignature)
            }

            sessionHistory.add(event(EventType.TOOL_RESULT,
                if (toolResult.success) toolResult.output
                else "Error: ${toolResult.error ?: toolResult.output}"))
        }

        return OrchestrationResult(false, "Max steps reached.")
    }

    private fun resolveResponse(raw: ModelResponse): ModelResponse {
        if (raw.toolCallName != null) return raw
        val text = raw.text
        if (text.contains("\"tool\"") || text.contains("\"name\"") || text.contains("{")) {
            return outputRepair.repairToolCall(text)
        }
        return raw
    }

    private fun isMutatingTool(name: String): Boolean = name in MUTATING_TOOLS

    private fun event(type: EventType, payload: String) = CrypsealEvent(
        sessionId = "active", type = type, payload = payload
    )

    private fun toolCallPayload(toolName: String, response: ModelResponse): String =
        "{\"tool\":\"$toolName\",\"args\":${response.toolCallArgsJson ?: "{}"}}"
}

data class OrchestrationResult(
    val success: Boolean,
    val reason: String
)
