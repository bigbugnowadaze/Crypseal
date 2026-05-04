package com.harrowhaus.crypseal.runtime.release

import com.harrowhaus.crypseal.runtime.context.Compactor
import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.gateway.AgentOrchestrator
import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.gateway.EventType
import com.harrowhaus.crypseal.runtime.gateway.FailureDetector
import com.harrowhaus.crypseal.runtime.models.MockModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.tools.FileReadTool
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class GoldenPathDemoTest {

    @Test
    fun testPythonEndToEndGoldenPath(): Unit = runBlocking {
        // Setup a project with a real file
        val tempDir = Files.createTempDirectory("crypseal_demo").toFile()
        File(tempDir, "main.py").writeText("print('Hello World')")

        val builder = ContextBuilder(tempDir, Compactor())
        val registry = ToolRegistry()
        registry.register(FileReadTool(tempDir))

        // Golden path: model reads the file, then finishes
        val modelResponses = listOf(
            ModelResponse("I will read the python file first.", "read_file", "{\"path\":\"main.py\"}"),
            ModelResponse("The file contains a hello world script. Task complete.")
        )
        val mockModel = MockModelRuntime(modelResponses)

        val orchestrator = AgentOrchestrator(
            mockModel, registry, builder, ModelOutputRepair(), FailureDetector()
        )
        val history = mutableListOf<CrypsealEvent>()

        // Run the loop
        val result = orchestrator.runActLoop(history, maxSteps = 5, isPlanMode = false)

        // Verify success
        assertTrue("Orchestrator should complete successfully", result.success)

        // Verify TOOL_CALL event was emitted
        val toolCalls = history.filter { it.type == EventType.TOOL_CALL }
        assertTrue("Should have emitted a TOOL_CALL event", toolCalls.isNotEmpty())
        assertTrue("TOOL_CALL should reference read_file",
            toolCalls.any { it.payload.contains("read_file") })

        // Verify TOOL_RESULT contains the file content with "Hello World"
        val toolResults = history.filter { it.type == EventType.TOOL_RESULT }
        assertTrue("Should have a TOOL_RESULT with file content",
            toolResults.any { it.payload.contains("Hello World") })

        // Verify the file was read with line numbers (FileReadTool format)
        assertTrue("FileReadTool should format output with line numbers",
            toolResults.any { it.payload.contains("1: print('Hello World')") })

        tempDir.deleteRecursively()
    }
}
