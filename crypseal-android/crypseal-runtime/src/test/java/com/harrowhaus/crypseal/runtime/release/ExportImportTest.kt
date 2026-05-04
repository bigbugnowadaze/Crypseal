package com.harrowhaus.crypseal.runtime.release

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class ExportImportTest {

    @Test
    fun testProjectExport() {
        val tempDir = Files.createTempDirectory("crypseal_export").toFile()
        val dummyFile = File(tempDir, "test.txt")
        dummyFile.writeText("test content")
        
        val exporter = ProjectExporter(tempDir)
        val zipFile = File(tempDir, "export.zip")
        
        val success = exporter.exportProject(zipFile)
        assertTrue(success)
        assertTrue(zipFile.exists())
        assertTrue(zipFile.length() > 0)
        
        tempDir.deleteRecursively()
    }
}
