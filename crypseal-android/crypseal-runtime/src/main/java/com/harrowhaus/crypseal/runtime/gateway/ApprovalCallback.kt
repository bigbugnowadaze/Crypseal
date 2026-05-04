package com.harrowhaus.crypseal.runtime.gateway

/**
 * Interface for requesting user approval for tool execution.
 * The orchestrator calls this when PolicyGate returns ASK.
 * Implementations can be:
 *   - UI-driven (shows approval dialog, waits for tap)
 *   - Auto-approve (for testing/trusted mode)
 *   - Auto-deny (for testing/restricted mode)
 */
interface ApprovalCallback {
    /**
     * Request approval for a tool execution.
     * Implementations may suspend (e.g., waiting for user input).
     * @return ApprovalResponse with the user's decision
     */
    suspend fun requestApproval(request: ApprovalRequest): ApprovalResponse
}

/**
 * Always approves. For tests and trusted-environment scenarios.
 */
class AutoApproveCallback : ApprovalCallback {
    override suspend fun requestApproval(request: ApprovalRequest): ApprovalResponse {
        return ApprovalResponse(
            requestId = request.requestId,
            decision = ApprovalDecision.APPROVE,
            userNote = "Auto-approved"
        )
    }
}

/**
 * Always denies. For tests and locked-down scenarios.
 */
class AutoDenyCallback : ApprovalCallback {
    override suspend fun requestApproval(request: ApprovalRequest): ApprovalResponse {
        return ApprovalResponse(
            requestId = request.requestId,
            decision = ApprovalDecision.DENY,
            userNote = "Auto-denied"
        )
    }
}

/**
 * Records all approval requests for test assertions,
 * then delegates to a configurable decision.
 */
class RecordingApprovalCallback(
    private val defaultDecision: ApprovalDecision = ApprovalDecision.APPROVE
) : ApprovalCallback {

    private val _requests = mutableListOf<ApprovalRequest>()
    val requests: List<ApprovalRequest> get() = _requests.toList()

    override suspend fun requestApproval(request: ApprovalRequest): ApprovalResponse {
        _requests.add(request)
        return ApprovalResponse(
            requestId = request.requestId,
            decision = defaultDecision,
            userNote = "Recorded (decision: $defaultDecision)"
        )
    }
}
