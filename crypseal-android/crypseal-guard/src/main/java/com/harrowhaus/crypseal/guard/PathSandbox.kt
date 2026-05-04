package com.harrowhaus.crypseal.guard

import java.io.File

class PathSandbox(private val projectRoot: File) {

    private val protectedPatterns = listOf(
        Regex("^\\.git(/.*)?$"),
        Regex("^\\.crypseal(/.*)?$"),
        Regex("^\\.Crypseal(/.*)?$"),
        Regex("^\\.termux(/.*)?$"),
        Regex("^\\.ssh(/.*)?$"),
        Regex("^\\.gnupg(/.*)?$"),
        Regex("^\\.env(\\..*)?$"),
        Regex("^(.*\\/)?\\.bashrc$"),
        Regex("^(.*\\/)?\\.zshrc$"),
        Regex("^(.*\\/)?\\.profile$")
    )

    fun isPathSafe(targetPath: String): Boolean {
        val target = File(targetPath)
        val absoluteTarget = if (target.isAbsolute) target else File(projectRoot, targetPath)
        
        // Normalize to resolve ../
        val normalizedTarget = absoluteTarget.normalize()
        val normalizedRoot = projectRoot.normalize()

        // 1. Prevent traversal outside project root (unless explicitly allowed via some other external storage mode not handled here)
        if (!normalizedTarget.absolutePath.startsWith(normalizedRoot.absolutePath)) {
            return false
        }

        // 2. Check protected paths
        val relativePath = normalizedTarget.absolutePath.removePrefix(normalizedRoot.absolutePath).removePrefix("/")
        
        // If it perfectly matches root, it's safe (just asking for root dir)
        if (relativePath.isEmpty()) return true

        for (pattern in protectedPatterns) {
            if (pattern.matches(relativePath)) {
                return false
            }
        }

        return true
    }
}
