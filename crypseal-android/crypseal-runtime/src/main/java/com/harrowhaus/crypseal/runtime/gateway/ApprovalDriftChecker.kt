package com.harrowhaus.crypseal.runtime.gateway

import java.io.File
import java.security.MessageDigest

/**
 * Checks that the conditions under which approval was granted
 * have not drifted before actual execution.
 *
 * Drift scenarios:
 *   1. Command string changed between approval and execution
 *   2. File content changed after approval (hash mismatch)
 */
object ApprovalDriftChecker {

    /**
     * Verify that an approved request's bindings still match at execution time.
     * @return null if no drift, or a description of the drift detected.
     */
    fun checkDrift(
        request: ApprovalRequest,
        currentArgs: Map<String, Any>,
        projectRoot: File? = null
    ): String? {
        // Check command drift
        if (request.boundCommand != null) {
            val currentCmd = (currentArgs["cmd"] as? String)
                ?: (currentArgs["command"] as? String)
            if (currentCmd != request.boundCommand) {
                return "Command drift: approved '${request.boundCommand}' but execution has '${currentCmd}'"
            }
        }

        // Check file drift
        if (request.boundFilePath != null && request.boundFileHash != null && projectRoot != null) {
            val file = File(projectRoot, request.boundFilePath)
            if (!file.exists()) {
                return "File drift: '${request.boundFilePath}' no longer exists"
            }
            val currentHash = hashFile(file)
            if (currentHash != request.boundFileHash) {
                return "File drift: '${request.boundFilePath}' was modified after approval"
            }
        }

        return null // No drift
    }

    /**
     * Build an ApprovalRequest with drift bindings from the tool call context.
     */
    fun bindRequest(
        sessionId: String,
        toolName: String,
        args: Map<String, Any>,
        verdict: PolicyVerdict,
        projectRoot: File? = null
    ): ApprovalRequest {
        val boundCommand = when (toolName) {
            "run_command" -> (args["cmd"] as? String) ?: (args["command"] as? String)
            else -> null
        }

        val boundFilePath = when (toolName) {
            "apply_patch", "write_file" -> args["path"] as? String
            else -> null
        }

        val boundFileHash = if (boundFilePath != null && projectRoot != null) {
            val file = File(projectRoot, boundFilePath)
            if (file.exists()) hashFile(file) else null
        } else null

        return ApprovalRequest(
            sessionId = sessionId,
            toolName = toolName,
            parsedArgs = args,
            verdict = verdict,
            boundCommand = boundCommand,
            boundFilePath = boundFilePath,
            boundFileHash = boundFileHash
        )
    }

    fun hashFile(file: File): String {
        val bytes = file.readBytes()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
