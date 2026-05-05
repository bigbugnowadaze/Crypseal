# ADR M6: Local Model Runtime Integration

## Status
Accepted

## Context
Crypseal is transitioning from a mocked execution environment (M0-M5) to a real, local, on-device AI runtime. To maintain privacy and offline capability, the app needs to run a local language model (LLM). However, on-device inference on Android is fragmented. The official Google path is LiteRT (formerly TFLite) for LLMs, but this requires specific model formats (E2B/E4B for Gemma) and device capabilities. Alternatively, many users running Termux already run models via `llama.cpp`.

To ensure Crypseal works robustly across devices, we need a flexible `RuntimeRegistry` that supports multiple backends without breaking the `AgentOrchestrator`.

## Decision
We will implement a multi-runtime registry with the following options:

1. **Primary: LiteRT Gemma Runtime**
   - **Why:** The official, optimized path for Android using the `org.tensorflow:tensorflow-lite-gpu` and task-vision libraries (or specific LiteRT-LM wrappers).
   - **Model Strategy:** Requires the user to download a specific `.tflite` or `.bin` Gemma model (e.g., Gemma 4 E2B/E4B) to a safe app directory.
   - **Current State:** Setup scaffolding will be built, but full model distribution is deferred until the core inference logic is verified. It will likely report `NEEDS_SETUP` initially.

2. **Fallback: Termux Llama-Server Runtime**
   - **Why:** Highly flexible, compatible with almost any `.gguf` model, and runs independently in Termux. It exposes an OpenAI-compatible REST API at `http://127.0.0.1:8080/v1/chat/completions`.
   - **Model Strategy:** The user manages the model and server manually in Termux. Crypseal simply talks to the endpoint.
   - **Current State:** Fully implementable in M6 as an HTTP client. It will fail safely if the server is unreachable.

3. **Testing: Mock Runtime**
   - **Why:** Required for fast JVM unit tests and isolated UI testing.
   - **Current State:** Already exists, will be registered in the new `RuntimeRegistry`.

## Consequences
### Positive
- Crypseal does not get locked into a single inference engine.
- Users can choose between ease-of-use (LiteRT) and power-user flexibility (Termux).
- The `AgentOrchestrator` remains completely unaware of the underlying model execution.

### Negative / Risks
- **Model Distribution:** Distributing large E2B models inside an APK is impossible. We must build a robust "Setup" UX for LiteRT.
- **Context Limits:** On-device models have strict memory and context window limits. Structured tool-call parsing must be extremely efficient to avoid wasting tokens.

## Deferred
- Automatic downloading of LiteRT models from the internet.
- Managing the lifecycle of the Termux llama.cpp server (we only act as a client for now).
