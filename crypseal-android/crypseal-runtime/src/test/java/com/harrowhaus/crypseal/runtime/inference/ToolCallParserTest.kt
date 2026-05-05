package com.harrowhaus.crypseal.runtime.inference

import org.junit.Assert.*
import org.junit.Test

class ToolCallParserTest {

    @Test
    fun `parse valid tool call standard format`() {
        val json = """
            {
                "tool": "read_file",
                "args": {
                    "path": "main.py"
                }
            }
        """.trimIndent()
        
        val response = ToolCallParser.parse(json)
        assertFalse(response.isMalformed)
        assertEquals("read_file", response.toolCallName)
        assertTrue(response.toolCallArgsJson!!.contains("\"path\":\"main.py\"") || response.toolCallArgsJson!!.contains("\"path\": \"main.py\""))
    }

    @Test
    fun `parse valid tool call alternative format`() {
        val json = """
            {
                "name": "run_command",
                "arguments": {
                    "command": "ls -la"
                }
            }
        """.trimIndent()
        
        val response = ToolCallParser.parse(json)
        assertFalse(response.isMalformed)
        assertEquals("run_command", response.toolCallName)
        assertTrue(response.toolCallArgsJson!!.contains("ls -la"))
    }

    @Test
    fun `parse normal text returns as text`() {
        val text = "This is just a normal assistant message telling you what I plan to do."
        val response = ToolCallParser.parse(text)
        assertFalse(response.isMalformed)
        assertNull(response.toolCallName)
        assertEquals(text, response.text)
    }

    @Test
    fun `parse tool call with surrounding text`() {
        val raw = """
            I'll check the file now.
            {
                "tool": "read_file",
                "args": {
                    "path": "main.py"
                }
            }
            Let me know if you need anything else.
        """.trimIndent()
        
        val response = ToolCallParser.parse(raw)
        assertFalse(response.isMalformed)
        assertEquals("read_file", response.toolCallName)
        assertTrue(response.text.contains("I'll check the file now."))
    }

    @Test
    fun `parse malformed json returns isMalformed true`() {
        val json = """
            {
                "tool": "read_file",
                "args": {
                    "path": "main.py"
                // broken syntax here
            }
        """.trimIndent()
        
        val response = ToolCallParser.parse(json)
        assertTrue(response.isMalformed)
        assertNull(response.toolCallName)
    }
}
