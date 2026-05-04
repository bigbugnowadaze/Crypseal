# Crypseal Android — Full-App Antigravity Handoff Package

Compiled: 2026-05-04

This package replaces the earlier prototype-style handoff. It is written as a **full application blueprint**, not a vibe-code starter and not a toy MVP. The goal is a production-grade Android coding agent that uses local/on-device models when possible, uses Termux as a governed execution node, and meets the modern coding-agent minimum standard established by public prior art such as OpenClaw, Codex CLI, Cline, Aider, SWE-agent, OpenHands, MCP, and Android/Google AI Edge tooling.

## Product thesis

Crypseal is a phone-native coding agent shell:

- Android app owns UI, permissions, state, policy, local model runtime, project management, sessions, diffs, approvals, and logs.
- Termux acts as the execution node for real tools: Python, Node, Git, Rust, package managers, local servers, tests, formatters, and repo workflows.
- Local/on-device models act as the default planner/operator, with runtime abstraction for LiteRT-LM/Gemma-class models first and optional fallback/adapters for Termux-hosted llama.cpp/MNN/MLC/ExecuTorch/API providers later.
- The model never owns the phone. The model proposes typed tool calls. The app validates policy. Termux executes only approved, bounded work.

## Read order for Antigravity

1. `00_DIRECTIVE_NO_PROTOTYPE.md`
2. `01_SOURCE_REGISTRY_AND_PRIOR_ART.md`
3. `02_FULL_APP_PRODUCT_SPEC.md`
4. `03_ARCHITECTURE_DECISION_RECORD.md`
5. `04_SYSTEM_ARCHITECTURE.md`
6. `05_ANDROID_APP_ARCHITECTURE.md`
7. `06_LOCAL_MODEL_RUNTIME_STRATEGY.md`
8. `07_TERMUX_EXECUTION_NODE_SPEC.md`
9. `08_AGENT_LOOP_ORCHESTRATION.md`
10. `09_TOOL_PROTOCOL.md`
11. `10_PERMISSION_SANDBOX_SECURITY.md`
12. `11_PATCH_DIFF_CHECKPOINTS.md`
13. `12_REPO_CONTEXT_INDEXING.md`
14. `13_SESSIONS_MEMORY_COMPACTION.md`
15. `14_SKILLS_SUBAGENTS_HOOKS.md`
16. `15_UI_UX_FULL_APP_SPEC.md`
17. `16_DATABASE_STORAGE_SCHEMA.md`
18. `17_MCP_AND_PLUGIN_ARCHITECTURE.md`
19. `18_TEST_QA_EVAL_SECURITY_MATRIX.md`
20. `19_PRODUCTION_ROADMAP.md`
21. `20_ANTIGRAVITY_MASTER_PROMPT.md`

## Build stance

This is a clean-room implementation. Public docs and open-source repos are used as prior art. Do not use leaked proprietary code. Do not clone contaminated repos into the project. Use open-source components only after license review.

## Deliverable expectation

Antigravity should build toward the **full app**, not stop at a demo. The roadmap is staged because serious software is built in layers, but every stage is tied to the production architecture and acceptance matrix.


## V3 Expansion: Design, Copy, Reasoning, and Full Agent Topology

This package now includes a dedicated product-design/copywriting layer, expanded subagents, expanded skills, and a reasoning-mode policy. The project is not only a coding agent. It must compete with premium AI assistants that feel good to use, write human copy, explain changes clearly, and design tastefully on mobile.

Added focus areas:
- Design and copy parity target, including onboarding, microcopy, brand voice, empty states, approval-card copy, error copy, App Store copy, and documentation.
- Full subagent taxonomy: product, UX, visual design, interaction, copy, research, QA, security, release, local-model/runtime, and reasoning supervisors.
- A safe reasoning architecture: private deliberation where supported, visible structured planning, evidence-backed decisions, self-refine loops, and no requirement to expose raw chain-of-thought.
- Prior-art-backed orchestration using ReAct, Self-Refine, Tree of Thoughts, SWE-agent ACI, Aider repo maps, OpenHands SDK/platform architecture, Cline Plan/Act, and Claude Code-style skills/subagents/hooks from public docs.
