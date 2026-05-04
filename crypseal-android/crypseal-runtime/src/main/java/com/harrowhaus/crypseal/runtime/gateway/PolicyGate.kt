package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.guard.CommandClassifier
import com.harrowhaus.crypseal.guard.PathSandbox
import com.harrowhaus.crypseal.guard.PolicyAction
import com.harrowhaus.crypseal.guard.RiskLevel
import java.io.File

/**
 * Evaluates every tool call against project security policy before execution.
 * Sits between the orchestrator and actual tool dispatch.
 */
class PolicyGate(
    private val projectRoot: File,
    private val pathSandbox: PathSandbox = PathSandbox(projectRoot),
    private val commandClassifier: CommandClassifier = CommandClassifier()
) {

    companion object {
        /** Tools that only read state — safe by default if args pass sandbox checks. */
        private val READ_TOOLS = setOf("read_file", "list_files", "grep_search", "git_status")

        /** Tools that mutate project state — require at least ASK. */
        private val MUTATING_TOOLS = setOf("apply_patch", "write_file", "git_commit")
    }

    /**
     * Evaluate a tool call and return the policy verdict.
     * Must be called BEFORE tool.execute().
     */
    fun evaluate(toolName: String, args: Map<String, Any>): PolicyVerdict {
        return when (toolName) {
            "read_file", "list_files" -> evaluateFileRead(toolName, args)
            "run_command" -> evaluateRunCommand(args)
            "apply_patch", "write_file" -> evaluateFileMutation(toolName, args)
            "git_status" -> PolicyVerdict(PolicyAction.ALLOW, RiskLevel.LOW_STATUS,
                "Read-only git operation")
            "git_commit" -> PolicyVerdict(PolicyAction.ASK, RiskLevel.MEDIUM_EDIT,
                "Git commit requires approval")
            "grep_search" -> PolicyVerdict(PolicyAction.ALLOW, RiskLevel.LOW_READ,
                "Search operation")
            else -> PolicyVerdict(PolicyAction.ASK, RiskLevel.MEDIUM_EDIT,
                "Unknown tool '$toolName' requires approval")
        }
    }

    private fun evaluateFileRead(toolName: String, args: Map<String, Any>): PolicyVerdict {
        val path = args["path"] as? String
            ?: return PolicyVerdict(PolicyAction.DENY, RiskLevel.LOW_READ,
                "Missing required 'path' argument for $toolName")

        if (!pathSandbox.isPathSafe(path)) {
            return PolicyVerdict(PolicyAction.DENY, RiskLevel.HIGH_SECRET_ACCESS,
                "Path '$path' is outside sandbox or targets a protected location")
        }

        return PolicyVerdict(PolicyAction.ALLOW, RiskLevel.LOW_READ,
            "Safe project-local read")
    }

    private fun evaluateFileMutation(toolName: String, args: Map<String, Any>): PolicyVerdict {
        val path = args["path"] as? String
            ?: return PolicyVerdict(PolicyAction.DENY, RiskLevel.MEDIUM_EDIT,
                "Missing required 'path' argument for $toolName")

        if (!pathSandbox.isPathSafe(path)) {
            return PolicyVerdict(PolicyAction.DENY, RiskLevel.HIGH_SECRET_ACCESS,
                "Path '$path' is outside sandbox or targets a protected location")
        }

        return PolicyVerdict(PolicyAction.ASK, RiskLevel.MEDIUM_EDIT,
            "File mutation '$toolName' on '$path' requires approval")
    }

    private fun evaluateRunCommand(args: Map<String, Any>): PolicyVerdict {
        val cmd = (args["cmd"] as? String)
            ?: (args["command"] as? String)
            ?: return PolicyVerdict(PolicyAction.DENY, RiskLevel.MEDIUM_EDIT,
                "Missing 'cmd' or 'command' argument for run_command")

        val (action, riskLevel) = commandClassifier.classifyCommand(cmd)
        val reason = when (action) {
            PolicyAction.ALLOW -> "Command classified as safe: '$cmd'"
            PolicyAction.ASK -> "Command requires approval: '$cmd'"
            PolicyAction.DENY -> "Command blocked by policy: '$cmd'"
        }
        return PolicyVerdict(action, riskLevel, reason)
    }
}

/**
 * The result of a policy evaluation.
 */
data class PolicyVerdict(
    val action: PolicyAction,
    val riskLevel: RiskLevel,
    val reason: String
)
