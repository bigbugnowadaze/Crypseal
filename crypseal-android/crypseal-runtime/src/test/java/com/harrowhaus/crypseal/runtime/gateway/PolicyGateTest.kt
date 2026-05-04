package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.guard.PolicyAction
import com.harrowhaus.crypseal.guard.RiskLevel
import com.harrowhaus.crypseal.runtime.context.Compactor
import com.harrowhaus.crypseal.runtime.context.ContextBuilder
import com.harrowhaus.crypseal.runtime.models.MockModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelOutputRepair
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.tools.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class PolicyGateTest {

    private lateinit var tempDir: File
    private lateinit var gate: PolicyGate

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("crypseal_policy_test").toFile()
        // Create project structure
        File(tempDir, "src").mkdirs()
        File(tempDir, "src/main.kt").writeText("fun main() {}")
        File(tempDir, ".git").mkdirs()
        File(tempDir, ".git/config").writeText("[core]")
        File(tempDir, ".env").writeText("SECRET=abc")
        File(tempDir, ".ssh").mkdirs()
        File(tempDir, ".ssh/id_rsa").writeText("private-key")
        gate = PolicyGate(tempDir)
    }

    // ---- File read policy ----

    @Test
    fun testSafeFileReadAllowed() {
        val verdict = gate.evaluate("read_file", mapOf("path" to "src/main.kt"))
        assertEquals(PolicyAction.ALLOW, verdict.action)
        assertEquals(RiskLevel.LOW_READ, verdict.riskLevel)
    }

    @Test
    fun testTraversalFileReadDenied() {
        val verdict = gate.evaluate("read_file", mapOf("path" to "../../../etc/passwd"))
        assertEquals(PolicyAction.DENY, verdict.action)
        assertEquals(RiskLevel.HIGH_SECRET_ACCESS, verdict.riskLevel)
    }

    @Test
    fun testProtectedEnvReadDenied() {
        val verdict = gate.evaluate("read_file", mapOf("path" to ".env"))
        assertEquals(PolicyAction.DENY, verdict.action)
    }

    @Test
    fun testProtectedSshReadDenied() {
        val verdict = gate.evaluate("read_file", mapOf("path" to ".ssh/id_rsa"))
        assertEquals(PolicyAction.DENY, verdict.action)
        assertEquals(RiskLevel.HIGH_SECRET_ACCESS, verdict.riskLevel)
    }

    @Test
    fun testMissingPathArgDenied() {
        val verdict = gate.evaluate("read_file", emptyMap())
        assertEquals(PolicyAction.DENY, verdict.action)
    }

    // ---- Run command policy ----

    @Test
    fun testSafeCommandAllowed() {
        val verdict = gate.evaluate("run_command", mapOf("cmd" to "pwd"))
        assertEquals(PolicyAction.ALLOW, verdict.action)
        assertEquals(RiskLevel.LOW_READ, verdict.riskLevel)
    }

    @Test
    fun testDestructiveCommandDenied() {
        val verdict = gate.evaluate("run_command", mapOf("cmd" to "rm -rf /"))
        assertEquals(PolicyAction.DENY, verdict.action)
        assertEquals(RiskLevel.BLOCKED_DESTRUCTIVE, verdict.riskLevel)
    }

    @Test
    fun testInlineEvalCommandAsk() {
        val verdict = gate.evaluate("run_command", mapOf("cmd" to "python -c 'print(1)'"))
        assertEquals(PolicyAction.ASK, verdict.action)
        assertEquals(RiskLevel.HIGH_INLINE_EVAL, verdict.riskLevel)
    }

    @Test
    fun testNpmInstallAsk() {
        val verdict = gate.evaluate("run_command", mapOf("cmd" to "npm install"))
        assertEquals(PolicyAction.ASK, verdict.action)
    }

    @Test
    fun testMissingCmdArgDenied() {
        val verdict = gate.evaluate("run_command", emptyMap())
        assertEquals(PolicyAction.DENY, verdict.action)
    }

    @Test
    fun testCommandAcceptsBothCmdAndCommandKey() {
        val verdict = gate.evaluate("run_command", mapOf("command" to "ls -la"))
        assertEquals(PolicyAction.ALLOW, verdict.action)
    }

    // ---- Orchestrator integration with PolicyGate ----

    @Test
    fun testOrchestratorSafeReadExecutes(): Unit = runBlocking {
        File(tempDir, "hello.py").writeText("print('Hello World')")

        val registry = ToolRegistry()
        registry.register(FileReadTool(tempDir))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Reading file.", "read_file", "{\"path\":\"hello.py\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate
        )
        val history = mutableListOf<CrypsealEvent>()
        val result = orchestrator.runActLoop(history)

        assertTrue("Orchestrator should succeed", result.success)
        assertTrue("Should have TOOL_RESULT with file content",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("Hello World") })
    }

    @Test
    fun testOrchestratorTraversalReadBlocked(): Unit = runBlocking {
        val registry = ToolRegistry()
        registry.register(FileReadTool(tempDir))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Reading etc.", "read_file", "{\"path\":\"../../../etc/passwd\"}"),
            ModelResponse("Ok.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        assertTrue("Should have DENIED result",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("DENIED") })
        // The tool should never have executed — no file content in results
        assertFalse("FileReadTool should not have executed",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("1: ") })
    }

    @Test
    fun testOrchestratorSafeCommandDispatches(): Unit = runBlocking {
        val mockRunner = MockCommandRunner()
        mockRunner.addResponse("pwd", CommandOutput(0, "/project", ""))

        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Checking directory.", "run_command", "{\"cmd\":\"pwd\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate
        )
        val history = mutableListOf<CrypsealEvent>()
        val result = orchestrator.runActLoop(history)

        assertTrue("Should succeed", result.success)
        assertTrue("Should have command output",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("/project") })
    }

    @Test
    fun testOrchestratorDestructiveCommandBlocked(): Unit = runBlocking {
        val mockRunner = MockCommandRunner()
        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Deleting everything.", "run_command", "{\"cmd\":\"rm -rf /\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        assertTrue("Should have DENIED result",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("DENIED") })
        assertTrue("Should reference BLOCKED_DESTRUCTIVE risk level",
            history.any { it.type == EventType.TOOL_RESULT && it.payload.contains("BLOCKED_DESTRUCTIVE") })
    }

    @Test
    fun testOrchestratorInlineEvalWaiting(): Unit = runBlocking {
        val mockRunner = MockCommandRunner()
        val registry = ToolRegistry()
        registry.register(RunCommandTool(mockRunner))

        val mockModel = MockModelRuntime(listOf(
            ModelResponse("Running python.", "run_command", "{\"cmd\":\"python -c 'print(1)'\"}"),
            ModelResponse("Done.")
        ))

        val orchestrator = AgentOrchestrator(
            mockModel, registry, ContextBuilder(tempDir, Compactor()),
            ModelOutputRepair(), FailureDetector(), gate
        )
        val history = mutableListOf<CrypsealEvent>()
        orchestrator.runActLoop(history)

        assertTrue("Should have APPROVAL_REQUEST for ASK verdict",
            history.any { it.type == EventType.APPROVAL_REQUEST && it.payload.contains("WAITING") })
    }

    @Test
    fun cleanUp() {
        tempDir.deleteRecursively()
    }
}
