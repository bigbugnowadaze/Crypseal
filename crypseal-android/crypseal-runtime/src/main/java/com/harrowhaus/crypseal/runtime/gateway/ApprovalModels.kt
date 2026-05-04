package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.guard.RiskLevel
import java.util.UUID

/**
 * A request for user approval before executing a tool call.
 * Created when PolicyGate returns ASK.
 */
data class ApprovalRequest(
    val requestId: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val toolName: String,
    val parsedArgs: Map<String, Any>,
    val verdict: PolicyVerdict,
    val createdAt: Long = System.currentTimeMillis(),
    /** Optional binding for drift detection on commands. */
    val boundCommand: String? = null,
    /** Optional binding for drift detection on file content. */
    val boundFilePath: String? = null,
    val boundFileHash: String? = null
)

/**
 * The user's response to an ApprovalRequest.
 */
data class ApprovalResponse(
    val requestId: String,
    val decision: ApprovalDecision,
    val timestamp: Long = System.currentTimeMillis(),
    val userNote: String? = null,
    val scope: ApprovalScope = ApprovalScope.ONCE
)

enum class ApprovalDecision {
    APPROVE,
    DENY
}

enum class ApprovalScope {
    /** Approval applies to this single execution only. */
    ONCE,
    /** Approval applies for the rest of this session (future milestone). */
    SESSION,
    /** Approval applies to all matching patterns (future milestone). */
    ALWAYS_FOR_PATTERN
}

/**
 * Tracks in-flight approval state within a session.
 */
enum class ApprovalState {
    PENDING,
    APPROVED,
    DENIED,
    EXPIRED
}
