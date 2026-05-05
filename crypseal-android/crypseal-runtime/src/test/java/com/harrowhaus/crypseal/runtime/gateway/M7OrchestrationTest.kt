package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.context.Compactor
import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.*
import com.harrowhaus.crypseal.runtime.tools.Tool
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry
import com.harrowhaus.crypseal.runtime.tools.ToolResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class M7OrchestrationTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var projectRoot: File
    private lateinit var toolRegistry: ToolRegistry
    private lateinit var contextBuilder: ContextBuilder

    @Before
    fun setup() {
        projectRoot = tempFolder.newFolder("M7TestProject")
        File(projectRoot, "main.py").writeText("print('hello')")
        
        toolRegistry = ToolRegistry()
        contextBuilder = ContextBuilder(projectRoot, Compactor())
    }

    @Test
    fun `orchestrator completes full loop with read_file tool`() = runTest {
        // 1. Setup Mock Model with 2 steps: 
        // Step 1: Request tool_call
        // Step 2: Give final answer
        val mockModel = MockModelRuntime(listOf(
            ModelResponse(
                text = "I need to read the file.",
                toolCallName = "read_file",
                toolCallArgsJson = "{\"path\":\"main.py\"}"
            ),
            ModelResponse(
                text = "The file prints hello."
            )
        ))

        // 2. Setup Mock Tool
        val mockTool = object : Tool() {
            override val name = "read_file"
            override val description = ""
            override val schema = ""
            override val group = ""
            override suspend fun execute(args: Map<String, Any>): ToolResult {
                return ToolResult(true, "print('hello')")
            }
        }
        toolRegistry.register(mockTool)

        val orchestrator = AgentOrchestrator(
            model = mockModel,
            toolRegistry = toolRegistry,
            contextBuilder = contextBuilder,
            outputRepair = ModelOutputRepair(),
            failureDetector = FailureDetector(),
            projectRoot = projectRoot
        )

        val history = mutableListOf<CrypsealEvent>()
        history.add(CrypsealEvent(sessionId = "test", type = EventType.USER_MESSAGE, payload = "Explain main.py"))

        val result = orchestrator.runActLoop(history, maxSteps = 3)

        assertTrue(result.success)
        
        // Verify history contains: User -> Agent(Text) -> ToolCall -> ToolResult -> Agent(Final)
        assertEquals(5, history.size)
        assertEquals(EventType.USER_MESSAGE, history[0].type)
        assertEquals(EventType.AGENT_MESSAGE, history[1].type)
        assertEquals(EventType.TOOL_CALL, history[2].type)
        assertEquals(EventType.TOOL_RESULT, history[3].type)
        assertEquals(EventType.AGENT_MESSAGE, history[4].type)
        
        assertTrue(history[3].payload.contains("print('hello')"))
        assertEquals("The file prints hello.", history[4].payload)
    }
}
