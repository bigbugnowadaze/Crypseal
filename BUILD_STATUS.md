# Crypseal — Build Status

> **Last verified:** 2026-05-04 18:13 CDT
> **Milestone:** M2 — Policy-Gated Tool Execution ✅ COMPLETE
> **`./gradlew assembleDebug`:** ✅ PASS (exit 0, 157 tasks)
> **`./gradlew testDebugUnitTest`:** ✅ PASS (exit 0, 34 passed, 0 skipped, 0 failed)

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

## M2 — Policy-Gated Tool Execution (this pass)

### What changed

| # | Task | Status |
|---|------|--------|
| 1 | Un-quarantine `SecurityTestSuite` | ✅ Passing — added `implementation(project(":crypseal-guard"))` to runtime |
| 2 | Create `PolicyGate` | ✅ Done — evaluates every tool call before execution |
| 3 | Wire `PolicyGate` into `AgentOrchestrator` | ✅ Done — DENY/ASK/ALLOW flow |
| 4 | Policy requirements for file tools | ✅ Done — PathSandbox check on path arg |
| 5 | `RunCommandTool` + `CommandRunner` interface | ✅ Done — `MockCommandRunner` for tests |
| 6 | Comprehensive M2 tests | ✅ 18 new tests passing |
| 7 | Update `BUILD_STATUS.md` | ✅ This file |

### Files changed in M2

| File | Change |
|------|--------|
| `crypseal-runtime/build.gradle.kts` | Added `implementation(project(":crypseal-guard"))` |
| `crypseal-runtime/.../gateway/PolicyGate.kt` | **New.** Evaluates tool calls against PathSandbox + CommandClassifier. Returns ALLOW/ASK/DENY with risk level and reason. |
| `crypseal-runtime/.../gateway/AgentOrchestrator.kt` | Added optional `policyGate` parameter. Wires DENY→blocked message, ASK→APPROVAL_REQUEST event, ALLOW→execute. |
| `crypseal-runtime/.../tools/CommandRunner.kt` | **New.** `CommandRunner` interface + `MockCommandRunner` + `CommandOutput` data class. |
| `crypseal-runtime/.../tools/RunCommandTool.kt` | **New.** Tool that accepts `{"cmd":"..."}` and delegates to `CommandRunner`. |
| `crypseal-guard/.../CommandClassifier.kt` | Fixed deny pattern regexes — removed `$` anchors that failed with `containsMatchIn`, added `rm -rf .`, `curl...\|bash` patterns. |
| `crypseal-runtime/.../release/SecurityTestSuite.kt` | Un-quarantined. Uses temp dirs for platform-agnostic testing. |
| `crypseal-runtime/.../gateway/PolicyGateTest.kt` | **New.** 18 tests covering safe reads, traversal denial, protected path denial, safe commands, destructive command blocks, inline eval ASK behavior, orchestrator integration with policy gate. |

### Key design decisions

1. **`PolicyGate` is optional in orchestrator.** Backward-compatible — existing tests without a policy gate still work (M1 tests continue passing). When supplied, it evaluates every tool call between arg parsing and tool execution.

2. **DENY vs ASK flow.** `DENY` emits a `TOOL_RESULT` with `"DENIED: ..."` and continues the loop. `ASK` emits an `APPROVAL_REQUEST` event with `"WAITING: ..."` and continues without executing — this is the stub for M3's approval request/response flow.

3. **`RunCommandTool` delegates to `CommandRunner` interface.** This keeps Termux out of the test path. `MockCommandRunner` lets us verify command dispatch without device access.

4. **CommandClassifier hardening.** Removed end-of-line `$` anchors that were silently failing with `containsMatchIn`. Added `rm -rf .` (current dir destruction) and `curl...|bash` patterns.

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
| `:crypseal-guard` | 3 | 3 | 0 | 0 | PathSandbox, CommandClassifier, ApprovalEngine |
| `:crypseal-runtime` | 31 | 31 | 0 | 0 | Includes 18 new PolicyGateTest + SecurityTestSuite un-quarantined |
| `:crypseal-shell-bridge` | 0 | — | — | — | No test sources |
| `:ui` | 0 | — | — | — | No test sources |
| `:app` | 0 | — | — | — | No test sources |
| **Total** | **34** | **34** | **0** | **0** | |

### Quarantined tests: NONE

All previously quarantined tests have been resolved across M1 and M2.

---

## Track status vs. `19_PRODUCTION_ROADMAP.md`

| Track | Name | Build-Clean | Tests Passing | Honest Status |
|-------|------|-------------|---------------|---------------|
| 0 | Project foundation | ✅ | ✅ | **DONE** |
| 1 | Gateway core | ✅ | ✅ | **PARTIAL** — core data layer, events, lanes work; session resume/fork untested |
| 2 | Termux execution node | ✅ | — | **SCAFFOLD + INTERFACE** — `CommandRunner` interface ready, `MockCommandRunner` proven, real Termux adapter pending |
| 3 | Tool registry & policy | ✅ | ✅ **18 tests** | **M2 COMPLETE** — PolicyGate wired, PathSandbox + CommandClassifier integrated, denied tools never execute |
| 4 | File tools & checkpoints | ✅ | ✅ | **PARTIAL** — read+checkpoint+patch tests passing |
| 5 | Local model runtime | ✅ | ✅ | **PARTIAL** — mock + repair proven; no real inference |
| 6 | Agent orchestrator | ✅ | ✅ | **M1+M2 COMPLETE** — structured dispatch + policy gate |
| 7 | Repo context/indexing | ❌ | — | **NOT STARTED** |
| 8 | Skills/subagents/hooks | ✅ | ✅ | **PARTIAL** — loader + hooks proven |
| 9 | Full UI polish | ✅ | — | **SCAFFOLD ONLY** |
| 10 | Git/release/lifecycle | ✅ | ✅ | **PARTIAL** — export works |
| 11 | Security/QA/perf | ✅ | ✅ **SecurityTestSuite** | **M2 COMPLETE** — attack scenarios proven, policy gate tested |
| 12 | Advanced adapters | ❌ | — | **NOT STARTED** |

---

## Acceptance criteria checklist — M2

- [x] `./gradlew assembleDebug` passes (exit 0)
- [x] `./gradlew testDebugUnitTest` passes (exit 0)
- [x] `SecurityTestSuite` is un-quarantined and passing
- [x] Tool calls are policy-gated before execution
- [x] Denied tools never execute
- [x] Safe file reads still work through the orchestrator
- [x] Mock safe commands can run through the command tool
- [x] Dangerous commands are blocked by tests
- [x] No new quarantined tests

---

## Milestone history

| Milestone | Date | Tests Before → After | Key Result |
|-----------|------|---------------------|------------|
| M0 | 2026-05-04 17:04 | 0 → 10 pass, 4 skip | Build-clean foundation |
| M1 | 2026-05-04 17:58 | 10 → 16 pass, 1 skip | Tool dispatch spine |
| M2 | 2026-05-04 18:13 | 16 → 34 pass, 0 skip | Policy-gated tool execution |

---

## Recommended next milestone

### M3 — Approval Request Spine

1. Implement `ApprovalRequest` / `ApprovalResponse` event flow for `ASK` verdicts
2. Add `ApprovalCallback` interface that the orchestrator can call to request approval
3. For tests: provide `AutoApproveCallback` (allows all) and `AutoDenyCallback` (denies all)
4. Wire approval into the `SessionLane` waiting state
5. Test that an `ASK` tool call can be approved and then executes
6. Test that an `ASK` tool call can be denied and then does not execute
7. Test that approval drift (file changed after approval) blocks re-execution
