package com.harrowhaus.crypseal.runtime.inference

import com.harrowhaus.crypseal.runtime.models.MockModelRuntime
import com.harrowhaus.crypseal.runtime.models.ModelResponse
import com.harrowhaus.crypseal.runtime.models.ModelRuntime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RuntimeRegistry {
    private val _runtimes = mutableMapOf<String, RuntimeSelection>()
    private val _activeRuntimeId = MutableStateFlow<String?>(null)

    val activeRuntimeId: StateFlow<String?> = _activeRuntimeId.asStateFlow()

    init {
        // Register default Mock runtime
        register(
            RuntimeDescriptor(
                id = "mock",
                name = "Mock Runtime",
                type = RuntimeType.MOCK,
                description = "For testing and development. Uses canned responses."
            ),
            RuntimeHealth(RuntimeStatus.READY)
        )
    }

    fun register(descriptor: RuntimeDescriptor, health: RuntimeHealth) {
        _runtimes[descriptor.id] = RuntimeSelection(descriptor, health)
        if (_activeRuntimeId.value == null && health.canGenerate) {
            _activeRuntimeId.value = descriptor.id
        }
    }

    fun getAvailableRuntimes(): List<RuntimeSelection> {
        return _runtimes.values.toList()
    }

    fun getActiveRuntimeSelection(): RuntimeSelection? {
        val id = _activeRuntimeId.value ?: return null
        return _runtimes[id]
    }

    fun setActiveRuntime(id: String): Boolean {
        if (_runtimes.containsKey(id)) {
            _activeRuntimeId.value = id
            return true
        }
        return false
    }

    fun updateHealth(id: String, health: RuntimeHealth) {
        val selection = _runtimes[id]
        if (selection != null) {
            _runtimes[id] = selection.copy(health = health)
        }
    }
}
