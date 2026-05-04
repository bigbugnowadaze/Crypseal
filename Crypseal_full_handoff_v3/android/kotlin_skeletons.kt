package com.harrowhaus.crypseal

interface ModelRuntime {
    val id: String
    val displayName: String
    suspend fun isAvailable(): RuntimeHealth
    suspend fun load(modelRef: ModelRef): LoadResult
    fun stream(request: ModelRequest): kotlinx.coroutines.flow.Flow<ModelEvent>
    suspend fun unload()
}

data class RuntimeHealth(val available: Boolean, val message: String? = null)
data class ModelRef(val id: String, val path: String? = null)
data class LoadResult(val ok: Boolean, val message: String? = null)
data class ModelRequest(val sessionId: String, val messages: List<AgentMessage>, val tools: List<ToolSpec>)
sealed class ModelEvent { data class Token(val text: String): ModelEvent(); data class Action(val action: AgentAction): ModelEvent() }
data class AgentMessage(val role: String, val content: String)
data class ToolSpec(val name: String, val schemaJson: String)

sealed class AgentAction {
    data class Message(val content: String): AgentAction()
    data class ToolCall(val tool: String, val args: Map<String, Any?>, val reason: String?): AgentAction()
}

interface Tool {
    val name: String
    suspend fun validate(args: Map<String, Any?>, ctx: ToolContext): ValidationResult
    suspend fun execute(args: Map<String, Any?>, ctx: ToolContext): ToolResult
}

data class ToolContext(val sessionId: String, val projectRoot: String)
data class ValidationResult(val ok: Boolean, val risk: RiskLevel, val message: String? = null)
data class ToolResult(val status: String, val payload: Map<String, Any?>)
enum class RiskLevel { LOW_READ, LOW_STATUS, LOW_TEST, MEDIUM_EDIT, MEDIUM_PACKAGE_INSTALL, MEDIUM_NETWORK, HIGH_DELETE, HIGH_EXTERNAL_STORAGE, HIGH_SECRET_ACCESS, HIGH_INLINE_EVAL, BLOCKED }
