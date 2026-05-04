# Architecture Decision Record

## ADR-001: Build a local Crypseal Gateway, not a single chat activity

Decision: Crypseal must have a local gateway/runtime layer that owns sessions, project state, tools, approvals, event stream, model routing, and Termux nodes.

Why: OpenClaw prior art shows that serious agents need a control plane. A chat activity alone cannot manage long-running runs, approvals, session lanes, skills, plugins, and tool policy.

Implementation: `core/gateway/CrypsealGateway.kt` with `SessionManager`, `ProjectManager`, `ToolRegistry`, `ApprovalEngine`, `EventBus`, `ModelRouter`, `TermuxNodeRegistry`.

## ADR-002: Use Termux as first execution node

Decision: The first production execution backend is user-installed Termux via RUN_COMMAND.

Why: Termux already supplies Linux packages and avoids bundling a fragile distro inside APK. RUN_COMMAND is official prior art.

Implementation: `TermuxIntentRunner` and `TermuxBridgeService`.

Future: optional embedded userland is v2+ only.

## ADR-003: Use runtime abstraction for local models

Decision: Do not hardcode model runtime. Define `ModelRuntime` interface.

Primary: LiteRT-LM/Gemma-class runtime.
Fallbacks: Termux llama.cpp server, MNN, MLC, ExecuTorch, remote API provider.

Reason: On-device model tooling is evolving quickly; the app architecture must survive runtime swaps.

## ADR-004: Use typed tools, not raw shell as the API

Decision: All agent actions are typed tool calls. Raw shell exists only under `termux.exec`, governed by policy.

Reason: This creates safety, UI cards, logging, schema validation, replay, and future model/runtime compatibility.

## ADR-005: App edits files; Termux runs code

Decision: Prefer app-owned file read/write/patch/diff/checkpoint tools. Termux should execute code and run utilities, not be the only file mutation path.

Reason: App-owned edits are easier to sandbox, diff, snapshot, and audit. Termux remains powerful but governed.

## ADR-006: Sandbox defaults to project root

Decision: Every project has a canonical root. Default file actions and commands are limited to that root.

Reason: Android phone storage contains personal data. Project-root sandbox prevents accidental traversal and exfiltration.

## ADR-007: Use session lanes

Decision: Each session/project has a single serialized run lane. New user input can queue, interrupt, or steer.

Reason: Prevents race conditions, corrupted patches, duplicated commands, and mixed tool results.

## ADR-008: Strict approvals with drift binding

Decision: Command approvals bind cwd, argv, resolved executable path, env overrides, and script file hash where applicable.

Reason: OpenClaw exec approvals prior art shows approval drift is a real issue.

## ADR-009: Repo map required for real codebase work

Decision: Add a repo context indexer and repo map as a first-class component, not a nice-to-have.

Reason: Aider and SWE-agent show context shaping is as important as model quality.

## ADR-010: Hooks, skills, and subagents are core features

Decision: Implement hooks, skills, and subagent definitions as full app concepts.

Reason: Serious coding agents need persistent project behavior, deterministic guardrails, and separate contexts for exploration/testing.
