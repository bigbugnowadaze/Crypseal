package com.harrowhaus.crypseal.runtime.release

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ProjectExporter(private val projectRoot: File) {

    fun exportProject(outputZipFile: File): Boolean {
        if (!projectRoot.exists() || !projectRoot.isDirectory) return false

        try {
            ZipOutputStream(FileOutputStream(outputZipFile)).use { zos ->
                projectRoot.walkTopDown().forEach { file ->
                    if (file.isFile) {
                        val relativePath = file.toRelativeString(projectRoot)
                        val zipEntry = ZipEntry(relativePath)
                        zos.putNextEntry(zipEntry)
                        file.inputStream().use { input ->
                            input.copyTo(zos)
                        }
                        zos.closeEntry()
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun exportSessionLogs(sessionId: String, outputZipFile: File): Boolean {
        val sessionDir = File(projectRoot, ".crypseal/sessions")
        val jsonlFile = File(sessionDir, "$sessionId.jsonl")
        
        if (!jsonlFile.exists()) return false
        
        try {
            ZipOutputStream(FileOutputStream(outputZipFile)).use { zos ->
                zos.putNextEntry(ZipEntry(jsonlFile.name))
                jsonlFile.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
