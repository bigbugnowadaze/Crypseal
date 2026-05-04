package com.harrowhaus.crypseal.guard

import java.io.File
import java.security.MessageDigest

data class ApprovalBinding(
    val command: String,
    val filePath: String?,
    val fileHash: String?
)

class ApprovalEngine {

    private val activeApprovals = mutableMapOf<String, ApprovalBinding>()

    fun requestApproval(id: String, command: String, fileToBind: File? = null): ApprovalBinding {
        val hash = fileToBind?.let { calculateHash(it) }
        val binding = ApprovalBinding(command, fileToBind?.absolutePath, hash)
        // Store pending logic here if needed
        return binding
    }

    fun approve(id: String, binding: ApprovalBinding) {
        activeApprovals[id] = binding
    }

    fun isApprovedAndValid(id: String, currentCommand: String, fileToBind: File? = null): Boolean {
        val binding = activeApprovals[id] ?: return false
        
        if (binding.command != currentCommand) return false
        
        if (binding.filePath != null && fileToBind != null) {
            if (binding.filePath != fileToBind.absolutePath) return false
            val currentHash = calculateHash(fileToBind)
            if (binding.fileHash != currentHash) {
                return false // Drift detected
            }
        }
        
        return true
    }

    private fun calculateHash(file: File): String? {
        if (!file.exists() || !file.isFile) return null
        val bytes = file.readBytes()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
