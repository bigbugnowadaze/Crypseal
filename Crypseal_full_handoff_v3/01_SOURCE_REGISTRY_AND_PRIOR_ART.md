# Source Registry and Prior-Art Map

This file is the source-backed foundation. Antigravity must use these sources as references for design decisions, not as vague inspiration. Before implementing a subsystem, re-open the relevant sources and verify any API names, versions, licenses, and examples.

## A. Android/on-device model runtimes

### Google AI Edge Gallery
URL: https://github.com/google-ai-edge/gallery
Relevant facts: open-source Android/iOS showcase for on-device GenAI; runs models locally/offline; official support for Gemma 4 is referenced in the repo.
Use in Crypseal: study model loading, mobile inference lifecycle, prompt/task UI, local model management, benchmark UI, on-device chat patterns.
Decision: use as the primary Android model-runtime reference, not as the final product shell.

### LiteRT-LM
URL: https://ai.google.dev/edge/litert-lm/overview
Relevant facts: cross-platform; Android support; GPU/NPU acceleration; multimodal; function calling/tool use with constrained decoding; supports Gemma, Llama, Phi, Qwen.
Use in Crypseal: primary local runtime target for Android-native models and constrained tool output.
Decision: implement `ModelRuntime` interface with `LiteRtLmRuntime` first when SDK integration permits.

### Gemma 4 function calling
URL: https://ai.google.dev/gemma/docs/capabilities/text/function-calling-gemma4
Relevant facts: Gemma 4 function-calling docs describe passing tool definitions to models and receiving tool-call structures.
Use: model output should be function-call shaped, but app must validate and not trust IDs or paths.
Decision: keep schemas flat and narrow for mobile models; route tool calls through `ToolRegistry`.

### MLC LLM Android
URL: https://llm.mlc.ai/docs/deploy/android.html
Relevant facts: Android SDK/app path; requires physical phone because mobile GPU matters.
Use: fallback/reference for native mobile model deployment and packaged model workflows.
Decision: secondary runtime track after LiteRT-LM.

### Alibaba MNN / MNN Chat
URL: https://github.com/alibaba/MNN/blob/master/apps/Android/MnnLlmChat/README.md
Relevant facts: mobile inference engine; Android chat app; supports Qwen/Gemma/Llama/TinyLlama/MobileLLM/DeepSeek/Phi/SmolLM; privacy-first on-device; reports speed advantages.
Use: performance fallback or comparative runtime track.
Decision: design `ModelRuntime` to permit future MNN adapter.

### ExecuTorch
URL: https://github.com/pytorch/executorch
URL: https://docs.pytorch.org/executorch/stable/using-executorch-android.html
Relevant facts: PyTorch on-device inference for smartphones; Java/Kotlin AAR integration; backends include XNNPACK/Vulkan/Qualcomm.
Use: future adapter path, especially if PyTorch model availability matters.
Decision: runtime abstraction must not be hardcoded to LiteRT-only.

### llama.cpp on Android/Termux
URL: https://www.reddit.com/r/LocalLLaMA/comments/1sdx6zz/running_a_local_llm_on_android_with_termux_and/
Relevant facts: local Android llama.cpp via Termux and llama-server is practical in community examples.
Use: fallback path where Termux runs `llama-server` and app connects over localhost.
Decision: `TermuxLlamaServerRuntime` should be a supported adapter after native runtime.

## B. Crypseal Shell Bridge

### Termux RUN_COMMAND Intent
URL: https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent
Relevant facts: third-party apps must declare/request `com.termux.permission.RUN_COMMAND`; Termux must set `allow-external-apps=true`; target SDK >=30 requires package visibility handling; result extras exist.
Use: primary bridge for Android app -> Termux execution.
Decision: implement `TermuxIntentRunner`, `TermuxSetupChecker`, `TermuxResultReceiver`, and a bootstrap diagnostics flow.

## C. Agent architecture and coding-agent prior art

### OpenClaw architecture, tools, exec, approvals
URLs:
- https://docs.openclaw.ai/concepts/architecture
- https://docs.openclaw.ai/concepts/agent-loop
- https://docs.openclaw.ai/tools
- https://docs.openclaw.ai/tools/exec
- https://docs.openclaw.ai/tools/exec-approvals
Relevant facts: gateway/control-plane architecture; sessions; tools/skills/plugins; exec approval interlock; canonical cwd/argv/executable binding; file hash binding for interpreter commands; approval-pending flow; strict inline eval hardening; allowlist/safe bins.
Use: core architecture pattern for Crypseal.
Decision: implement a local Crypseal Gateway inside Android, with session lanes, tool registry, approval engine, event stream, and Termux node.

### OpenAI Codex CLI / sandbox / approvals
URLs:
- https://developers.openai.com/codex/cli
- https://developers.openai.com/codex/agent-approvals-security
- https://developers.openai.com/codex/concepts/sandboxing
Relevant facts: local terminal coding agent can read/change/run code in selected directory; sandbox and approval policy control boundaries; network disabled by default; spawned commands inherit sandbox boundaries.
Use: security and workspace-boundary reference.
Decision: Android app must enforce project-root sandbox and permissions independently of model intent.

### Cline Plan & Act and auto approve
URLs:
- https://docs.cline.bot/core-workflows/plan-and-act
- https://docs.cline.bot/features/auto-approve
Relevant facts: Plan mode explores without changing files; Act mode executes; auto-approve/YOLO is dangerous; user-visible permission levels matter.
Use: UI/UX permission modes and Plan/Act separation.
Decision: support Plan, Ask, Accept Edits, Auto-Safe, Locked, and Disposable Sandbox modes.

### Aider repository map
URLs:
- https://aider.chat/docs/repomap.html
- https://aider.chat/2023/10/22/repomap.html
Relevant facts: concise repo map with important classes/functions/signatures helps LLM understand large repos; tree-sitter AST can support symbol extraction.
Use: repo context indexing.
Decision: implement layered context: ripgrep/file map first, tree-sitter/LSP later, repo map always summarized and budgeted.

### SWE-agent / Agent-Computer Interface
URL: https://swe-agent.com/0.7/background/
Relevant facts: ACI means simple LM-centric commands and feedback formats for browsing, viewing, editing, executing code; summarizers help long context.
Use: tool design and feedback design.
Decision: tool outputs must be compact, line-numbered, deterministic, and model-friendly.

### OpenHands
URLs:
- https://github.com/OpenHands/OpenHands
- https://openreview.net/forum?id=OJd3ayDDoF
Relevant facts: agents interact like human developers by writing code, using command line, browsing web; platform supports multiple LLMs, sandboxed execution, evaluations.
Use: confirms full developer-agent platform pattern.
Decision: build an extensible agent runtime, not a static chat wrapper.

### ReAct
URL: https://arxiv.org/abs/2210.03629
Relevant facts: interleaving reasoning and acting improves task solving and reduces hallucination by interacting with tools/environments.
Use: agent loop pattern.
Decision: every plan/act/observe cycle must persist observations and feed them back to model; no unsupported guesses after command failures.

### MCP
URL: https://www.anthropic.com/news/model-context-protocol
Relevant facts: MCP is a protocol for connecting assistants to external tools/data sources.
Use: plugin/tool future compatibility.
Decision: use MCP-inspired tool abstraction internally, but do not expose arbitrary MCP execution until sandbox/policy is mature.

## D. Android platform prior art and constraints

### Foreground service types
URL: https://developer.android.com/develop/background-work/services/fgs/service-types
Relevant facts: Android 14+ requires declared foreground service types and permissions.
Use: long-running agent sessions, local server monitors, Termux callbacks.
Decision: foreground service is required for active long-running runs; declare correct service types and user-visible notifications.

### WorkManager long-running work
URL: https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/long-running
Relevant facts: WorkManager can run long-running workers with foreground service, but Android 16 job quotas can be exhausted; direct foreground service may be needed.
Use: scheduled cleanup/indexing only; not primary live agent execution.
Decision: use foreground service for active agent runs; WorkManager for maintenance/index tasks.

### Android scoped storage/app-specific files
URLs:
- https://source.android.com/docs/core/storage/scoped
- https://developer.android.com/training/data-storage/app-specific
Relevant facts: scoped storage limits external storage access; app-specific files are preferred.
Use: workspace storage model.
Decision: default project root in app/Termux-owned workspace; external storage access via explicit user approval/document picker.

### Jetpack Compose architecture/ViewModel
URLs:
- https://developer.android.com/develop/ui/compose/architecture
- https://developer.android.com/topic/libraries/architecture/viewmodel
Relevant facts: Compose fits unidirectional data flow; ViewModel holds state and survives configuration changes.
Use: Android UI architecture.
Decision: use Compose + ViewModel + coroutine/Flow state streams.

## E. Code intelligence

### Tree-sitter
URL: https://github.com/tree-sitter/tree-sitter
Relevant facts: incremental parser; builds concrete syntax trees and updates efficiently as source changes.
Use: repo map, symbol indexing, context selection.
Decision: begin with file/ripgrep map; add tree-sitter indexer as production-grade context layer.

## How to use this registry

For every subsystem, Antigravity should write/maintain an implementation note:

- Source(s) reviewed
- Decision made
- Library/API chosen
- License impact
- Failure modes
- Test proving the choice works


## V3 Added Prior Art: Reasoning, Subagents, Design/Copy Quality

- Claude Code public docs: subagents, skills, hooks, permission modes, and plan/act patterns establish that a modern agent needs extensibility and specialist contexts.
- ReAct: use interleaved plan/action/observation loops for tool-using agents.
- Self-Refine: use critique/refine loops for copy, design, patches, and explanations.
- Tree of Thoughts: use branching/alternative evaluation for major architecture/product decisions only.
- SWE-agent ACI: agent performance depends on LM-native computer interfaces, not just model quality.
- Aider repo map: repository mapping is required for coding context.
- OpenHands SDK/platform: production agents need event logs, workspace abstractions, safe execution, and composable agent architecture.
- Recent CoT faithfulness literature: visible chain-of-thought is not guaranteed faithful; Crypseal should expose structured summaries and evidence instead.
