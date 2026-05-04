# Local Model Runtime Strategy

## Principle
The app must not depend on one model engine forever. Use an interface-first model runtime layer.

## Interface

```kotlin
interface ModelRuntime {
    val id: String
    val displayName: String
    suspend fun isAvailable(): RuntimeHealth
    suspend fun load(modelRef: ModelRef): LoadResult
    fun stream(request: ModelRequest): Flow<ModelEvent>
    suspend fun unload()
}
```

## Primary runtime: LiteRT-LM

Why:
- Android-first path.
- Google AI Edge Gallery provides relevant open-source app patterns.
- LiteRT-LM supports Android, hardware acceleration, multimodality, and constrained function calling.

Implementation tasks:
1. Build a minimal `LiteRtLmRuntime` wrapper.
2. Implement model download/import/selection metadata.
3. Support streamed tokens if SDK permits.
4. Support function-call/tool-call structured output.
5. Fall back to JSON repair parser when model produces malformed structure.
6. Add thermal/memory health reporting.

## Runtime fallback: Termux llama-server

Why:
- Community prior art shows llama.cpp can run in Termux.
- Useful for GGUF models and independent experimentation.

Implementation:
- Termux starts `llama-server` with approved command.
- App connects to `http://127.0.0.1:<port>`.
- Runtime adapter wraps OpenAI-compatible endpoint when available.
- Health checker verifies server model, context length, latency.

## Performance alternatives

### MNN
Use when Android CPU/GPU speed matters and supported models are available.

### MLC
Use when compiled model workflows or mobile GPU optimizations are better for a target device.

### ExecuTorch
Use for PyTorch ecosystem models and future Android AAR integrations.

## Cloud/API provider option

Cloud should be optional, not identity. Use for hard tasks, not default. The app should remain local-first and should clearly mark when data leaves device.

## Model output policy

Never trust model output for:
- paths
- command safety
- approval IDs
- file hashes
- working directory
- environment variables
- user identity/secrets

All tool-call arguments must be normalized by app state.

## Prompting policy

Use small, flat tool schemas. Mobile local models are more reliable with narrow JSON schemas than deeply nested ones. Prefer one tool call per step for early versions.

## Model roles

- `PlannerModel`: plan and task breakdown.
- `OperatorModel`: propose next tool call.
- `RepairModel`: read failures and propose patches.
- `SummarizerModel`: compact logs and session history.
- `ClassifierModel` optional: policy assistance only; never final authority.
