# Crypseal — Build Status

> **Last verified:** 2026-05-04 17:58 CDT
> **Milestone:** M1 — Tool Dispatch Spine ✅ COMPLETE
> **`./gradlew assembleDebug`:** ✅ PASS (exit 0, 157 tasks)
> **`./gradlew testDebugUnitTest`:** ✅ PASS (exit 0, 16 passed, 1 skipped, 0 failed)

---

## Environment

| Key | Value |
|-----|-------|
| Java | OpenJDK 21.0.9 (Android Studio JBR) |
| Gradle | 8.9 (via wrapper) |
| AGP | 8.4.0 |
| Kotlin | 1.9.22 |
| KSP | 1.9.22-1.0.17 |
| compileSdk | 34 |
| minSdk | 26 |
| Host OS | Windows |

---

## M1 — Tool Dispatch Spine (this pass)

### What changed

| # | Task | Status |
|---|------|--------|
| 1 | Fix `AgentOrchestrator.runActLoop` to use structured `ModelResponse` directly | ✅ Done |
| 2 | Add JSON argument parser (`ToolArgParser`) | ✅ Done |
| 3 | Fix tool result flow (emit `TOOL_CALL` + `TOOL_RESULT` events) | ✅ Done |
| 4 | Fix Plan Mode to block mutating tools cleanly | ✅ Done |
| 5 | Un-quarantine `EpicFTest.testAgentOrchestratorPlanMode` | ✅ Passing |
| 6 | Un-quarantine `GoldenPathDemoTest.testPythonEndToEndGoldenPath` | ✅ Passing |
| 7 | Fix `PathSandbox` for platform-agnostic operation | ✅ Done |
| 8 | Un-quarantine `EpicCTest.testPathSandbox` | ✅ Passing |

### Files changed in M1

| File | Change |
|------|--------|
| `crypseal-runtime/.../gateway/AgentOrchestrator.kt` | Rewritten: uses `ModelResponse` directly, falls back to repair only when structured fields missing. Parses args via `ToolArgParser`. Emits `TOOL_CALL` + `TOOL_RESULT` events. Proper plan-mode gate. |
| `crypseal-runtime/.../tools/ToolArgParser.kt` | **New.** JSON→`Map<String, Any>` parser using Android's built-in `org.json`. Supports strings, numbers, booleans, nested objects, arrays. Returns safe error on malformed input. |
| `crypseal-runtime/build.gradle.kts` | Added `testImplementation("org.json:json:20231013")` for JVM unit test classpath. |
| `crypseal-guard/.../PathSandbox.kt` | Rewritten: uses `canonicalFile` instead of `normalize()` for cross-platform path resolution. Normalizes separators to `/` before regex matching. |
| `crypseal-guard/.../EpicCTest.kt` | Un-quarantined `testPathSandbox`: now uses temp directories instead of hardcoded Linux paths. |
| `crypseal-runtime/.../gateway/EpicFTest.kt` | Un-quarantined `testAgentOrchestratorPlanMode`. |
| `crypseal-runtime/.../release/GoldenPathDemoTest.kt` | Rewritten as realistic golden-path test: model calls `read_file` with parsed args `{"path":"main.py"}`, file content appears in `TOOL_RESULT` event, model finishes cleanly. |

### Key design decisions

1. **`resolveResponse()` fallback strategy:** The orchestrator uses structured `ModelResponse.toolCallName` / `toolCallArgsJson` when present. Only falls back to `ModelOutputRepair.repairToolCall(text)` when structured fields are null AND the text contains JSON-like markers (`"tool"`, `"name"`, `{`). This ensures mock runtimes work deterministically while still supporting sloppy real model output.

2. **`ToolArgParser` uses `org.json`:** Android ships `org.json` as part of the framework, so no new runtime dependency. We only add it as `testImplementation` for JVM unit tests. No kotlinx.serialization needed.

3. **Event flow per tool call:** Each tool dispatch now emits two events in sequence:
   - `TOOL_CALL` with `{"tool":"name","args":{...}}`
   - `TOOL_RESULT` with success output or prefixed `Error: ...`

4. **PathSandbox uses `canonicalFile`:** This resolves symlinks and `..` sequences correctly on both Windows (`C:\`) and Unix (`/`) without path separator assumptions.

---

## Commands run

```
.\gradlew.bat assembleDebug            → PASS (exit 0)
.\gradlew.bat testDebugUnitTest        → PASS (exit 0)
```

---

## Test results

| Module | Tests Run | Passed | Failed | Skipped | Notes |
|--------|-----------|--------|--------|---------|-------|
| `:crypseal-guard` | 3 | 3 | 0 | 0 | All passing — `testPathSandbox` un-quarantined ✅ |
| `:crypseal-runtime` | 14 | 13 | 0 | 1 | `SecurityTestSuite` remains quarantined (cross-module import) |
| `:crypseal-shell-bridge` | 0 | — | — | — | No test sources |
| `:ui` | 0 | — | — | — | No test sources |
| `:app` | 0 | — | — | — | No test sources |
| **Total** | **17** | **16** | **0** | **1** | |

### Un-quarantined this pass

| Test | Was | Now | Proof |
|------|-----|-----|-------|
| `EpicCTest.testPathSandbox` | `@Ignore` (Linux paths on Windows) | ✅ Passing | Uses temp dirs, platform-agnostic |
| `EpicFTest.testAgentOrchestratorPlanMode` | `@Ignore` (orchestrator discarded tool calls) | ✅ Passing | Orchestrator uses `ModelResponse` directly |
| `GoldenPathDemoTest.testPythonEndToEndGoldenPath` | `@Ignore` (orchestrator didn't parse args) | ✅ Passing | Args parsed via `ToolArgParser`, `FileReadTool` receives `{"path":"main.py"}` |

### Remaining quarantined test

| Test | Reason | Promote When |
|------|--------|-------------|
| `SecurityTestSuite.testAttackScenarios` | Imports `:crypseal-guard` classes from `:crypseal-runtime` test sources (cross-module dependency not declared) | Move test to an integration-test source set, or add `testImplementation(project(":crypseal-guard"))` to `:crypseal-runtime` |

---

## Track status vs. `19_PRODUCTION_ROADMAP.md`

| Track | Name | Build-Clean | Tests Passing | Honest Status |
|-------|------|-------------|---------------|---------------|
| 0 | Project foundation | ✅ | ✅ | **DONE** |
| 1 | Gateway core | ✅ | ✅ SessionLane, JSONL writer, event model | **PARTIAL** — core data layer works; session resume/fork untested |
| 2 | Termux execution node | ✅ | — | **SCAFFOLD ONLY** — stubs compile, no device integration |
| 3 | Tool registry & policy | ✅ | ✅ PathSandbox, CommandClassifier, ApprovalEngine | **PARTIAL** — all 3 tests passing. SecurityTestSuite needs cross-module wiring |
| 4 | File tools & checkpoints | ✅ | ✅ FileReadTool, CheckpointManager, PatchApplyTool | **PARTIAL** — read+checkpoint+patch tests passing; patch is overwrite, not real diff |
| 5 | Local model runtime | ✅ | ✅ MockModelRuntime, ModelOutputRepair | **PARTIAL** — mock + repair proven; no real inference |
| 6 | Agent orchestrator | ✅ | ✅ **FailureDetector, PlanMode, GoldenPath** | **M1 COMPLETE** — structured dispatch working, args parsed, tool results captured |
| 7 | Repo context/indexing | ❌ | — | **NOT STARTED** |
| 8 | Skills/subagents/hooks | ✅ | ✅ SkillsLoader, HookEngine | **PARTIAL** — loader + hooks proven; subagent is stub |
| 9 | Full UI polish | ✅ | — | **SCAFFOLD ONLY** |
| 10 | Git/release/lifecycle | ✅ | ✅ ProjectExporter | **PARTIAL** — export works; git tools are stubs |
| 11 | Security/QA/perf | ✅ | ⚠️ | **SCAFFOLD ONLY** — SecurityTestSuite quarantined |
| 12 | Advanced adapters | ❌ | — | **NOT STARTED** |

---

## Acceptance criteria checklist — M1

- [x] `./gradlew assembleDebug` passes (exit 0)
- [x] `./gradlew testDebugUnitTest` passes (exit 0)
- [x] No newly introduced quarantines
- [x] `EpicCTest.testPathSandbox` — un-quarantined, passing
- [x] `EpicFTest.testAgentOrchestratorPlanMode` — un-quarantined, passing
- [x] `GoldenPathDemoTest.testPythonEndToEndGoldenPath` — un-quarantined, passing
- [x] `AgentOrchestrator` drives `MockModelRuntime` → `FileReadTool` with parsed `{"path":"main.py"}` → captures `TOOL_RESULT` with file content

---

## Recommended next milestone

### M2 — Cross-Module Integration & SecurityTestSuite

1. Add `testImplementation(project(":crypseal-guard"))` to `:crypseal-runtime` and un-quarantine `SecurityTestSuite`
2. Wire `CommandClassifier` + `PathSandbox` into `AgentOrchestrator` so tool execution is policy-gated
3. Add a `RunCommandTool` that delegates to `TermuxIntentRunner` (mock for tests, real on device)
4. Write an integration test where the orchestrator runs `read_file`, then tries `run_command` and gets policy-checked
5. All tests green, no new quarantines
