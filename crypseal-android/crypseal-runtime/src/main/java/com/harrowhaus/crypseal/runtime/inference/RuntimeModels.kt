package com.harrowhaus.crypseal.runtime.inference

enum class RuntimeStatus {
    UNAVAILABLE,
    NEEDS_SETUP,
    READY,
    LOADING,
    RUNNING,
    FAILED
}

enum class RuntimeType {
    MOCK,
    LITE_RT,
    TERMUX_SERVER,
    REMOTE_API
}

data class RuntimeHealth(
    val status: RuntimeStatus,
    val message: String? = null,
    val canGenerate: Boolean = (status == RuntimeStatus.READY || status == RuntimeStatus.RUNNING)
)

data class RuntimeDescriptor(
    val id: String,
    val name: String,
    val type: RuntimeType,
    val description: String
)

data class RuntimeSelection(
    val descriptor: RuntimeDescriptor,
    val health: RuntimeHealth
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1024,
    val topP: Float = 0.9f
)

data class ModelAsset(
    val id: String,
    val name: String,
    val path: String,
    val sizeBytes: Long
)

data class ModelAssetState(
    val asset: ModelAsset,
    val isAvailable: Boolean,
    val downloadProgress: Float? = null
)
