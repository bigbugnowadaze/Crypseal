package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.storage.jsonl.JsonlEventWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class SessionLaneTest {

    @Test
    fun testSerializationAndInterrupt() = runBlocking {
        val tempDir = Files.createTempDirectory("crypseal_events").toFile()
        val writer = JsonlEventWriter(tempDir)
        val lane = SessionLane("test-session", writer)
        
        var counter = 0
        var interrupted = false
        
        launch(Dispatchers.Default) {
            lane.enqueueAction {
                delay(50)
                try {
                    lane.checkInterrupt()
                    counter++
                } catch (e: InterruptedException) {
                    interrupted = true
                }
            }
        }
        
        launch(Dispatchers.Default) {
            delay(10)
            lane.interrupt()
        }
        
        delay(100)
        assertTrue(interrupted)
        assertEquals(0, counter)
        
        tempDir.deleteRecursively()
    }
}
