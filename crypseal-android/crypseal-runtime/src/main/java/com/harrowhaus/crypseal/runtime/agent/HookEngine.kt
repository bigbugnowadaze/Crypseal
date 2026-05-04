package com.harrowhaus.crypseal.runtime.agent

interface PreCommandHook {
    val name: String
    fun evaluate(command: String, args: Map<String, Any>): HookResult
}

data class HookResult(
    val allow: Boolean,
    val blockReason: String? = null
)

class HookEngine {
    private val hooks = mutableListOf<PreCommandHook>()

    fun registerHook(hook: PreCommandHook) {
        hooks.add(hook)
    }

    fun evaluatePreCommand(command: String, args: Map<String, Any>): HookResult {
        for (hook in hooks) {
            val result = hook.evaluate(command, args)
            if (!result.allow) {
                return result // Fast fail on first block
            }
        }
        return HookResult(true)
    }
}
