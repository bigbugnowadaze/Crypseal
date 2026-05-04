package com.harrowhaus.crypseal.runtime.models

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class EpicETest {

    @Test
    fun testMockRuntimeDeterministic(): Unit = runBlocking {
        val canned = listOf(
            ModelResponse("First response", "toolA", "{}"),
            ModelResponse("Second response", "toolB", "{}")
        )
        val mock = MockModelRuntime(canned)
        
        val r1 = mock.generateResponse(emptyList())
        assertEquals("First response", r1.text)
        assertEquals("toolA", r1.toolCallName)
        
        val r2 = mock.generateResponse(emptyList())
        assertEquals("Second response", r2.text)
        assertEquals("toolB", r2.toolCallName)
        
        val r3 = mock.generateResponse(emptyList())
        assertEquals("Mock exhausted.", r3.text)
    }

    @Test
    fun testModelOutputRepair() {
        val repair = ModelOutputRepair()
        
        val valid = """
            ```json
            {
              "tool": "read_file",
              "args": {"path": "main.kt"}
            }
            ```
        """.trimIndent()
        
        val resValid = repair.repairToolCall(valid)
        assertFalse(resValid.isMalformed)
        
        val malformed = """
            {
              "tool": "read_file",
              "args": {"path": "main.kt"
        """.trimIndent()
        
        val resRepaired = repair.repairToolCall(malformed)
        // With basic brace injection it should pass the extraction check
        assertFalse(resRepaired.isMalformed)
        
        val garbage = "Just regular text talking about a tool."
        val resGarbage = repair.repairToolCall(garbage)
        assertTrue(resGarbage.isMalformed)
    }
}
