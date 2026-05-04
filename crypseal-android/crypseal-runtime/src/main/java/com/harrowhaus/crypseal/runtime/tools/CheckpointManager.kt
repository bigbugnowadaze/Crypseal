package com.harrowhaus.crypseal.runtime.tools

import java.io.File

class CheckpointManager(private val checkpointsDir: File) {

    init {
        checkpointsDir.mkdirs()
    }

    fun createCheckpoint(targetFile: File, sessionId: String): String {
        val checkpointId = "${sessionId}_${System.currentTimeMillis()}"
        val snapshotFile = File(checkpointsDir, checkpointId)
        if (targetFile.exists()) {
            targetFile.copyTo(snapshotFile, overwrite = true)
        } else {
            snapshotFile.writeText("") // Empty file flag
        }
        return checkpointId
    }

    fun revertCheckpoint(checkpointId: String, targetFile: File): Boolean {
        val snapshotFile = File(checkpointsDir, checkpointId)
        if (!snapshotFile.exists()) return false
        
        snapshotFile.copyTo(targetFile, overwrite = true)
        return true
    }
}
