package com.harrowhaus.crypseal.runtime.gateway

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
