package com.harrowhaus.crypseal.runtime.inference

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RuntimeRegistryTest {

    private lateinit var registry: RuntimeRegistry

    @Before
    fun setup() {
        registry = RuntimeRegistry()
    }

    @Test
    fun `registry initializes with mock runtime`() {
        val runtimes = registry.getAvailableRuntimes()
        assertTrue(runtimes.isNotEmpty())
        assertEquals("mock", runtimes[0].descriptor.id)
        assertEquals("mock", registry.activeRuntimeId.value)
    }

    @Test
    fun `can register and select new runtime`() {
        val descriptor = RuntimeDescriptor("test", "Test Runtime", RuntimeType.TERMUX_SERVER, "")
        registry.register(descriptor, RuntimeHealth(RuntimeStatus.READY))
        
        val success = registry.setActiveRuntime("test")
        assertTrue(success)
        assertEquals("test", registry.activeRuntimeId.value)
        assertEquals("Test Runtime", registry.getActiveRuntimeSelection()?.descriptor?.name)
    }

    @Test
    fun `cannot select invalid runtime`() {
        val success = registry.setActiveRuntime("invalid_id")
        assertFalse(success)
        assertEquals("mock", registry.activeRuntimeId.value)
    }
}
