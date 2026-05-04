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
    fun testPythonEndToEndGoldenPath() = runBlocking {
        // Setup sandbox
        val tempDir = Files.createTempDirectory("crypseal_demo").toFile()
        File(tempDir, "main.py").writeText("print('Hello World')")
        
        val builder = ContextBuilder(tempDir, Compactor())
        val registry = ToolRegistry()
        registry.register(FileReadTool(tempDir))
        
        // Define golden path sequence of model replies
        val modelResponses = listOf(
            ModelResponse("I will read the python file first.", "read_file", "{\"path\":\"main.py\"}"),
            ModelResponse("It's a hello world script. I'll execute it.", "run_command", "{\"cmd\":\"python main.py\"}"),
            ModelResponse("Execution successful. Golden path complete.")
        )
        val mockModel = MockModelRuntime(modelResponses)
        
        val orchestrator = AgentOrchestrator(
            mockModel, registry, builder, ModelOutputRepair(), FailureDetector()
        )
        val history = mutableListOf<CrypsealEvent>()
        
        // Run Loop
        val result = orchestrator.runActLoop(history, maxSteps = 5, isPlanMode = false)
        
        assertTrue(result.success)
        assertTrue("History should contain agent tool calls and messages", history.size >= 3)
        
        val reads = history.filter { it.type == EventType.TOOL_RESULT && it.payload.contains("Hello World") }
        assertTrue("Should have successfully read main.py", reads.isNotEmpty())
        
        tempDir.deleteRecursively()
    }
}
