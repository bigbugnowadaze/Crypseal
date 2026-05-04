package com.harrowhaus.crypseal.runtime.tools

import java.io.File

class PatchApplyTool(
    private val projectRoot: File,
    private val checkpointManager: CheckpointManager,
    private val sessionId: String
) : Tool() {
    override val name = "apply_patch"
    override val description = "Applies a unified diff patch to a file."
    override val schema = """{"type":"object","properties":{"path":{"type":"string"},"patch":{"type":"string"}} }"""
    override val group = "file_ops"

    override suspend fun execute(args: Map<String, Any>): ToolResult {
        val path = args["path"] as? String ?: return ToolResult(false, "", "Missing path")
        val patchContent = args["patch"] as? String ?: return ToolResult(false, "", "Missing patch string")
        
        val target = File(projectRoot, path)
        
        // 1. Checkpoint
        val checkpointId = checkpointManager.createCheckpoint(target, sessionId)

        // 2. Mock applying patch - normally we'd run 'patch' via Termux or JGit
        // Here we just simulate an overwrite for the test
        // Real implementation would invoke diffutils
        if (!target.exists()) {
            target.parentFile?.mkdirs()
            target.createNewFile()
        }
        
        target.writeText(patchContent)

        return ToolResult(true, "Patch applied. Checkpoint ID: $checkpointId")
    }
}
