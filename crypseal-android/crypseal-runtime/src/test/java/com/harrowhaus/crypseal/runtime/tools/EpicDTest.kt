package com.harrowhaus.crypseal.runtime.tools

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class EpicDTest {

    @Test
    fun testToolRegistry() {
        val registry = ToolRegistry()
        val gitTool = GitStatusTool()
        registry.register(gitTool)
        
        val retrieved = registry.getTool("git_status")
        assertNotNull(retrieved)
        assertEquals("git_status", retrieved?.name)
    }

    @Test
    fun testFileReadAndTruncate(): Unit = runBlocking {
        val tempDir = Files.createTempDirectory("crypseal_test").toFile()
        val testFile = File(tempDir, "test.txt")
        val lines = (1..1000).joinToString("\n") { "Line $it" }
        testFile.writeText(lines)
        
        val reader = FileReadTool(tempDir)
        val result = reader.execute(mapOf("path" to "test.txt"))
        
        assertTrue(result.success)
        assertTrue(result.output.contains("1: Line 1"))
        assertTrue(result.output.contains("800: Line 800"))
        assertTrue(result.output.contains("... (file truncated, 200 lines omitted)"))
        assertFalse(result.output.contains("801: Line 801"))
        
        tempDir.deleteRecursively()
    }

    @Test
    fun testCheckpointRevert(): Unit = runBlocking {
        val tempDir = Files.createTempDirectory("crypseal_test").toFile()
        val cpDir = File(tempDir, "checkpoints")
        val target = File(tempDir, "target.kt")
        target.writeText("original content")
        
        val cm = CheckpointManager(cpDir)
        val cpId = cm.createCheckpoint(target, "session-1")
        
        val patcher = PatchApplyTool(tempDir, cm, "session-1")
        val res = patcher.execute(mapOf("path" to "target.kt", "patch" to "modified content"))
        assertTrue(res.success)
        assertEquals("modified content", target.readText())
        
        val reverted = cm.revertCheckpoint(cpId, target)
        assertTrue(reverted)
        assertEquals("original content", target.readText())
        
        tempDir.deleteRecursively()
    }
}
