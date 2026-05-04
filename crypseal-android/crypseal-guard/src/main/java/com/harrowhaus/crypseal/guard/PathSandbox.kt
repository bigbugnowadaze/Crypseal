package com.harrowhaus.crypseal.guard

import java.io.File

class PathSandbox(private val projectRoot: File) {

    private val protectedPatterns = listOf(
        Regex("^\\.git(/.*|\\\\.*)?$"),
        Regex("^\\.crypseal(/.*|\\\\.*)?$"),
        Regex("^\\.Crypseal(/.*|\\\\.*)?$"),
        Regex("^\\.termux(/.*|\\\\.*)?$"),
        Regex("^\\.ssh(/.*|\\\\.*)?$"),
        Regex("^\\.gnupg(/.*|\\\\.*)?$"),
        Regex("^\\.env(\\..*)?\$"),
        Regex("^(.*[/\\\\])?\\.bashrc\$"),
        Regex("^(.*[/\\\\])?\\.zshrc\$"),
        Regex("^(.*[/\\\\])?\\.profile\$")
    )

    fun isPathSafe(targetPath: String): Boolean {
        val target = File(targetPath)
        val absoluteTarget = if (target.isAbsolute) target else File(projectRoot, targetPath)

        // Normalize to resolve ../ sequences
        val normalizedTarget = absoluteTarget.canonicalFile
        val normalizedRoot = projectRoot.canonicalFile

        // 1. Prevent traversal outside project root
        if (!normalizedTarget.path.startsWith(normalizedRoot.path)) {
            return false
        }

        // 2. Compute relative path using canonical paths
        val rootPath = normalizedRoot.path
        val targetAbsPath = normalizedTarget.path
        var relativePath = targetAbsPath.removePrefix(rootPath)

        // Strip leading separator
        if (relativePath.startsWith(File.separator)) {
            relativePath = relativePath.removePrefix(File.separator)
        }

        // Normalize separators to forward-slash for regex matching
        val normalized = relativePath.replace('\\', '/')

        // If it perfectly matches root, it's safe
        if (normalized.isEmpty()) return true

        // 3. Check against protected patterns
        for (pattern in protectedPatterns) {
            if (pattern.matches(normalized)) {
                return false
            }
        }

        return true
    }
}
