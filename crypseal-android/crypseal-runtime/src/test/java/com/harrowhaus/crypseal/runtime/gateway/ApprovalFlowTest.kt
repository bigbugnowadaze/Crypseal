package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.context.Compactor
import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.MockModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.storage.jsonl.JsonlEventWriter
import com.harrowhaus.crypseal.runtime.tools.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class ApprovalFlowTest {

    private lateinit var tempDir: File
    private lateinit var gate: PolicyGate

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("crypseal_approval_test").toFile()
        File(tempDir, "src").mkdirs()
        File(tempDir, "src/main.kt").writeText("fun main() {}")
        gate = PolicyGate(tempDir)
    }

    // ---- Task 6.1: ASK command with AutoApproveCallback executes ----

    @Test
    fun testAskCommandAutoApproveExecutes(): Unit = runBlocking {
        val mockRunner = MockCommandRunner()
        mockRunner.addResponse("npm install", CommandOutput(0, "added 42 packages", ""))

        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Installing deps.", "run_command", "{\"cmd\":\"npm install\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = AutoApproveCallback(),
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        val result = orchestrator.runActLoop(history)

        assertTrue("Should succeed", result.success)
        assertTrue("Should have tool result with npm output",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("added 42 packages") })
    }

    // ---- Task 6.2: ASK command with AutoDenyCallback does not execute ----

    @Test
    fun testAskCommandAutoDenyDoesNotExecute(): Unit = runBlocking {
        val mockRunner = MockCommandRunner()
        mockRunner.addResponse("npm install", CommandOutput(0, "should not appear", ""))

        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Installing deps.", "run_command", "{\"cmd\":\"npm install\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = AutoDenyCallback(),
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        val result = orchestrator.runActLoop(history)

        assertTrue("Should complete", result.success)
        assertFalse("Tool should not have executed",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("should not appear") })
    }

    // ---- Task 6.3: DENY command never calls approval callback ----

    @Test
    fun testDenyCommandNeverCallsApproval(): Unit = runBlocking {
        val recorder = RecordingApprovalCallback()

        val registry = ToolRegistry()
        registry.register(RunCommandTool(MockCommandRunner()))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Deleting.", "run_command", "{\"cmd\":\"rm -rf /\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = recorder,
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        assertTrue("Recorder should have zero requests", recorder.requests.isEmpty())
        assertTrue("Should have DENIED result",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("DENIED") })
    }

    // ---- Task 6.4: ALLOW command does not call approval callback ----

    @Test
    fun testAllowCommandNoApprovalCall(): Unit = runBlocking {
        val recorder = RecordingApprovalCallback()

        val registry = ToolRegistry()
        registry.register(FileReadTool(tempDir))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Reading file.", "read_file", "{\"path\":\"src/main.kt\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = recorder,
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        val result = orchestrator.runActLoop(history)

        assertTrue("Should succeed", result.success)
        assertTrue("Recorder should have zero requests (ALLOW skips callback)",
            recorder.requests.isEmpty())
        assertTrue("File content should appear in result",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("fun main()") })
    }

    // ---- Task 6.5: APPROVAL_REQUEST and APPROVAL_RESPONSE events emitted in order ----

    @Test
    fun testApprovalEventsEmittedInOrder(): Unit = runBlocking {
        val mockRunner = MockCommandRunner()
        mockRunner.addResponse("npm install", CommandOutput(0, "ok", ""))

        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Installing.", "run_command", "{\"cmd\":\"npm install\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = AutoApproveCallback(),
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        val approvalEvents = history.filter {
            it.type == EventType.APPROVAL_REQUEST || it.type == EventType.APPROVAL_RESPONSE
        }

        assertTrue("Should have at least 2 approval events", approvalEvents.size >= 2)
        assertEquals("First approval event should be REQUEST",
            EventType.APPROVAL_REQUEST, approvalEvents[0].type)
        assertEquals("Second approval event should be RESPONSE",
            EventType.APPROVAL_RESPONSE, approvalEvents[1].type)
        assertTrue("Response should indicate APPROVE",
            approvalEvents[1].payload.contains("APPROVE"))
    }

    // ---- Task 6.6: SessionLane enters WAITING_FOR_APPROVAL during approval ----

    @Test
    fun testSessionLaneWaitingState(): Unit = runBlocking {
        val eventsDir = Files.createTempDirectory("crypseal_events").toFile()
        val writer = JsonlEventWriter(eventsDir)
        val lane = SessionLane("test-session", writer)

        val stateLog = mutableListOf<LaneState>()

        // Custom callback that records lane state during approval
        val stateCapturingCallback = object : ApprovalCallback {
            override suspend fun requestApproval(request: ApprovalRequest): ApprovalResponse {
                stateLog.add(lane.laneState.value)
                return ApprovalResponse(
                    requestId = request.requestId,
                    decision = ApprovalDecision.APPROVE
                )
            }
        }

        val mockRunner = MockCommandRunner()
        mockRunner.addResponse("npm install", CommandOutput(0, "ok", ""))

        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Installing.", "run_command", "{\"cmd\":\"npm install\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = stateCapturingCallback,
            sessionLane = lane,
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        assertTrue("Lane should have been WAITING_FOR_APPROVAL during callback",
            stateLog.contains(LaneState.WAITING_FOR_APPROVAL))

        eventsDir.deleteRecursively()
    }

    // ---- Task 6.7: Approval drift blocks execution ----

    @Test
    fun testApprovalDriftBlocksExecution(): Unit = runBlocking {
        // Create a file that will be "approved" for mutation
        val targetFile = File(tempDir, "target.kt")
        targetFile.writeText("original content")

        val registry = ToolRegistry()
        registry.register(object : Tool() {
            override val name = "write_file"
            override val description = "Writes a file"
            override val schema = "{}"
            override val group = "file_ops"
            override suspend fun execute(args: Map<String, Any>): ToolResult {
                return ToolResult(true, "Written")
            }
        })

        // Custom callback that modifies the file between approval and execution
        // to simulate drift
        val driftCallback = object : ApprovalCallback {
            override suspend fun requestApproval(request: ApprovalRequest): ApprovalResponse {
                // Simulate file modification after approval was granted
                targetFile.writeText("MODIFIED after approval")
                return ApprovalResponse(
                    requestId = request.requestId,
                    decision = ApprovalDecision.APPROVE
                )
            }
        }

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Writing file.", "write_file",
                "{\"path\":\"target.kt\",\"content\":\"new content\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate,
            approvalCallback = driftCallback,
            projectRoot = tempDir
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        assertTrue("Should have DRIFT_BLOCKED result",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("DRIFT_BLOCKED") })
        assertFalse("Write tool should not have executed",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("Written") })
    }

    // ---- Task 6.8: Command drift detection ----

    @Test
    fun testCommandDriftDetection() {
        val request = ApprovalRequest(
            sessionId = "test",
            toolName = "run_command",
            parsedArgs = mapOf("cmd" to "npm install"),
            verdict = PolicyVerdict(
                com.harrowhaus.crypseal.guard.PolicyAction.ASK,
                com.harrowhaus.crypseal.guard.RiskLevel.MEDIUM_EDIT,
                "test"
            ),
            boundCommand = "npm install"
        )

        // No drift when command matches
        val noDrift = ApprovalDriftChecker.checkDrift(request, mapOf("cmd" to "npm install"))
        assertNull("Should have no drift", noDrift)

        // Drift when command changed
        val drift = ApprovalDriftChecker.checkDrift(request, mapOf("cmd" to "npm run build"))
        assertNotNull("Should detect drift", drift)
        assertTrue("Drift message should explain", drift!!.contains("Command drift"))
    }

    @Test
    fun cleanUp() {
        tempDir.deleteRecursively()
    }
}
