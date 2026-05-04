package com.harrowhaus.crypseal.runtime.tools

class ToolRegistry {
    private val tools = mutableMapOf<String, Tool>()

    fun register(tool: Tool) {
        tools[tool.name] = tool
    }

    fun getTool(name: String): Tool? {
        return tools[name]
    }

    fun getAllSchemas(): List<String> {
        return tools.values.map { it.schema }
    }
}
