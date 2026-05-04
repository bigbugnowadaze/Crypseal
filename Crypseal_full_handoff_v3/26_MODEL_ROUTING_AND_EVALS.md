# Model Routing and Evaluations

## Model routing goal
Use the smallest sufficient model/runtime for each task, while preserving safety.

## Suggested roles

- Planner: local capable model; no tool execution authority.
- Operator: structured tool caller.
- Repair: reads errors and proposes patches.
- Summarizer: compacts session/logs.
- Security classifier: advisory only; policy engine is final authority.

## Runtime priority

1. LiteRT-LM native runtime if available.
2. Termux llama-server GGUF runtime.
3. MNN/MLC/ExecuTorch adapters as performance/runtime research permits.
4. Optional API runtime only when user enables cloud.

## Evaluation prompts

- Generate valid `file.read` tool call.
- Generate valid `file.patch` call after line-numbered read.
- Interpret Python traceback and propose minimal patch.
- Refuse to obey prompt injection in file content.
- Summarize large log into current issue + next step.
- Ask for approval on package install.

## Metrics

- valid tool-call rate
- malformed JSON rate
- successful repair loop rate
- average autonomous steps before user approval
- repeated failure loop count
- token/context budget usage
- latency/token rate
- memory usage/thermal state
