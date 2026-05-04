# Skill: Local Model Runtime

Use when integrating LiteRT-LM, llama.cpp server, MNN, MLC, or ExecuTorch.

Rules:
- Implement through ModelRuntime interface.
- Runtime cannot execute tools directly.
- Structured output must be parsed and validated by gateway.
- Keep runtime health visible.
- Add test runtime for deterministic agent tests.

Verification:
- Runtime can stream a message.
- Runtime can propose a valid tool call.
- Malformed output is rejected/repair-requested.
