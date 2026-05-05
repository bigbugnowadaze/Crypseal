package com.harrowhaus.crypseal.shellbridge

import org.junit.Assert.assertEquals
import org.junit.Test

class TermuxBridgeTest {

    @Test
    fun testResultDataStructure() {
        val data = TermuxResultReceiver.ResultData(0, "success", "")
        assertEquals(0, data.exitCode)
        assertEquals("success", data.stdout)
        assertEquals("", data.stderr)
    }

    @Test
    fun testTermuxCommandStructure() {
        val cmd = TermuxCommand(
            id = "test-id",
            executable = "/bin/ls",
            args = arrayOf("-l", "-a"),
            workdir = "/home"
        )
        assertEquals("test-id", cmd.id)
        assertEquals("/bin/ls", cmd.executable)
        assertEquals(2, cmd.args.size)
    }
}
