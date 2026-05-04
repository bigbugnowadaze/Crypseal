import os

# Context Builder & Compactor (Epic F01, F05)
context_dir = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\main\java\com\harrowhaus\crypseal\runtime\context"
os.makedirs(context_dir, exist_ok=True)

context_builder_kt = """package com.harrowhaus.crypseal.runtime.context

import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent
import com.harrowhaus.crypseal.runtime.models.ModelMessage
import java.io.File

class ContextBuilder(
    private val projectRoot: File,
    private val compactor: Compactor
) {
    fun buildContext(sessionHistory: List<CrypsealEvent>, isPlanMode: Boolean): List<ModelMessage> {
        val messages = mutableListOf<ModelMessage>()
        
        // 1. System Instruction
        val systemPrompt = buildString {
            append("You are Crypseal, a local Android coding agent.\\n")
            if (isPlanMode) {
                append("MODE: PLAN_ONLY. You may only use read/search tools. Formulate a plan.\\n")
            } else {
                append("MODE: ACT. You may edit files and execute commands.\\n")
            }
            
            val agentMd = File(projectRoot, ".crypseal/AGENT.md")
            if (agentMd.exists()) {
                append("PROJECT INSTRUCTIONS:\\n${agentMd.readText()}\\n")
            }
        }
        messages.add(ModelMessage("system", systemPrompt))
        
        // 2. Compacted History
        val compactedHistory = compactor.compactHistory(sessionHistory)
        
        // 3. Assemble
        for (event in compactedHistory) {
            val role = if (event.type.name.contains("USER")) "user" else "assistant"
            messages.add(ModelMessage(role, event.payload))
        }
        
        return messages
    }
}
"""

compactor_kt = """package com.harrowhaus.crypseal.runtime.context

import com.harrowhaus.crypseal.runtime.gateway.CrypsealEvent

class Compactor(private val maxTokens: Int = 4000) {

    fun compactHistory(events: List<CrypsealEvent>): List<CrypsealEvent> {
        // Very basic mock compaction logic
        // If history is too long (mocked as > 50 events), truncate and summarize older ones
        if (events.size <= 50) return events

        val recent = events.takeLast(20)
        val summaryEvent = CrypsealEvent(
            sessionId = events.first().sessionId,
            type = com.harrowhaus.crypseal.runtime.gateway.EventType.AGENT_MESSAGE,
            payload = "[Prior history compacted due to context limits. ${events.size - 20} events omitted.]"
        )
        
        return listOf(summaryEvent) + recent
    }
}
"""

with open(os.path.join(context_dir, "ContextBuilder.kt"), "w") as f:
    f.write(context_builder_kt)

with open(os.path.join(context_dir, "Compactor.kt"), "w") as f:
    f.write(compactor_kt)

# Agent Orchestrator & Failure Detector (Epic F02, F03, F04)
gateway_dir = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\main\java\com\harrowhaus\crypseal\runtime\gateway"
os.makedirs(gateway_dir, exist_ok=True)

failure_detector_kt = """package com.harrowhaus.crypseal.runtime.gateway

class FailureDetector {
    private val toolHistory = mutableListOf<String>()
    
    fun recordFailure(toolJson: String) {
        toolHistory.add(toolJson)
    }

    fun recordSuccess() {
        toolHistory.clear()
    }

    fun isLooping(toolJson: String): Boolean {
        // If the exact same failing tool payload is requested 3 times in a row
        if (toolHistory.size >= 2) {
            val lastTwo = toolHistory.takeLast(2)
            if (lastTwo.all { it == toolJson }) {
                return true
            }
        }
        return false
    }
}
"""

orchestrator_kt = """package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry

class AgentOrchestrator(
    private val model: ModelRuntime,
    private val toolRegistry: ToolRegistry,
    private val contextBuilder: ContextBuilder,
    private val outputRepair: ModelOutputRepair,
    private val failureDetector: FailureDetector
) {

    suspend fun runActLoop(
        sessionHistory: MutableList<CrypsealEvent>, 
        maxSteps: Int = 10,
        isPlanMode: Boolean = false
    ): OrchestrationResult {
        
        for (step in 1..maxSteps) {
            val context = contextBuilder.buildContext(sessionHistory, isPlanMode)
            val rawResponse = model.generateResponse(context)
            val response = outputRepair.repairToolCall(rawResponse.text) // or rawResponse directly
            
            // Log model message
            sessionHistory.add(CrypsealEvent(
                sessionId = "mock",
                type = EventType.AGENT_MESSAGE,
                payload = response.text
            ))

            if (response.toolCallName != null) {
                // If Plan Mode, block mutating tools
                if (isPlanMode && isMutatingTool(response.toolCallName)) {
                    val rejectEvent = CrypsealEvent(
                        sessionId = "mock",
                        type = EventType.TOOL_RESULT,
                        payload = "Error: Cannot execute mutating tool '${response.toolCallName}' in PLAN mode."
                    )
                    sessionHistory.add(rejectEvent)
                    continue
                }

                // Check Failure Loop
                val toolJsonString = "${response.toolCallName}:${response.toolCallArgsJson}"
                if (failureDetector.isLooping(toolJsonString)) {
                    return OrchestrationResult(false, "Failure loop detected on tool: ${response.toolCallName}")
                }

                val tool = toolRegistry.getTool(response.toolCallName)
                if (tool == null) {
                    failureDetector.recordFailure(toolJsonString)
                    sessionHistory.add(CrypsealEvent(
                        sessionId = "mock",
                        type = EventType.TOOL_RESULT,
                        payload = "Error: Tool '${response.toolCallName}' not found."
                    ))
                    continue
                }

                // Execute tool
                val result = tool.execute(emptyMap()) // Mock empty args parsed from json
                if (result.success) {
                    failureDetector.recordSuccess()
                } else {
                    failureDetector.recordFailure(toolJsonString)
                }

                sessionHistory.add(CrypsealEvent(
                    sessionId = "mock",
                    type = EventType.TOOL_RESULT,
                    payload = result.output
                ))
            } else {
                // No tool call means the model stopped acting and is just chatting
                return OrchestrationResult(true, "Agent completed autonomous step.")
            }
        }
        
        return OrchestrationResult(false, "Max steps reached.")
    }

    private fun isMutatingTool(name: String): Boolean {
        // Mock hardcoded list for Plan mode
        return name in listOf("apply_patch", "run_command", "git_commit")
    }
}

data class OrchestrationResult(
    val success: Boolean,
    val reason: String
)
"""

with open(os.path.join(gateway_dir, "FailureDetector.kt"), "w") as f:
    f.write(failure_detector_kt)

with open(os.path.join(gateway_dir, "AgentOrchestrator.kt"), "w") as f:
    f.write(orchestrator_kt)

# Tests
test_root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\test\java\com\harrowhaus\crypseal\runtime\gateway"
os.makedirs(test_root, exist_ok=True)

test_kt = """package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.context.Compactor
import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.MockModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.tools.Tool
import com.harrowhaus.crypseal.runtime.tools.ToolRegistry
import com.harrowhaus.crypseal.runtime.tools.ToolResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class EpicFTest {

    @Test
    fun testFailureDetectorLoop() {
        val detector = FailureDetector()
        val toolStr = "run_command:{\\"cmd\\":\\"broken\\"}"
        
        assertFalse(detector.isLooping(toolStr))
        detector.recordFailure(toolStr)
        assertFalse(detector.isLooping(toolStr))
        detector.recordFailure(toolStr)
        assertTrue(detector.isLooping(toolStr)) // 3rd attempt
    }

    @Test
    fun testAgentOrchestratorPlanMode() = runBlocking {
        val tempDir = Files.createTempDirectory("crypseal_test").toFile()
        val builder = ContextBuilder(tempDir, Compactor())
        val registry = ToolRegistry()
        
        // Mock a model trying to apply a patch
        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Let's edit", "apply_patch", "{}")
        ))
        
        val orchestrator = AgentOrchestrator(mockModel, registry, builder, ModelOutputRepair(), FailureDetector())
        val history = mutableListOf<CrypsealEvent>()
        
        // Run in PLAN mode
        val result = orchestrator.runActLoop(history, isPlanMode = true)
        
        assertTrue(history.any { it.payload.contains("Cannot execute mutating tool 'apply_patch' in PLAN mode") })
        
        tempDir.deleteRecursively()
    }
}
"""

with open(os.path.join(test_root, "EpicFTest.kt"), "w") as f:
    f.write(test_kt)

print("Epic F Implemented.")
