# Crypseal — Build Status

> **Last verified:** 2026-05-04 18:29 CDT
> **Milestone:** M3 — Approval Request Spine ✅ COMPLETE
> **`./gradlew assembleDebug`:** ✅ PASS (exit 0, 157 tasks)
> **`./gradlew testDebugUnitTest`:** ✅ PASS (exit 0, 43 passed, 0 skipped, 0 failed)

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

## M3 — Approval Request Spine (this pass)

### What changed

| # | Task | Status |
|---|------|--------|
| 1 | Approval domain models | ✅ `ApprovalRequest`, `ApprovalResponse`, `ApprovalDecision`, `ApprovalScope`, `ApprovalState` |
| 2 | `ApprovalCallback` interface | ✅ `AutoApproveCallback`, `AutoDenyCallback`, `RecordingApprovalCallback` |
| 3 | Wire into `AgentOrchestrator` | ✅ ASK → callback → approve/deny → execute/block |
| 4 | Wire into `SessionLane` state | ✅ `WAITING_FOR_APPROVAL` ↔ `EXECUTING` transitions |
| 5 | Approval drift checks | ✅ Command drift + file hash drift |
| 6 | Comprehensive tests | ✅ 9 new tests passing |
| 7 | Update `BUILD_STATUS.md` | ✅ This file |

### Files changed in M3

| File | Change |
|------|--------|
| `crypseal-runtime/.../gateway/ApprovalModels.kt` | **New.** `ApprovalRequest` with binding fields (command, file path, file hash), `ApprovalResponse`, `ApprovalDecision`, `ApprovalScope`, `ApprovalState`. |
| `crypseal-runtime/.../gateway/ApprovalCallback.kt` | **New.** `ApprovalCallback` interface + `AutoApproveCallback`, `AutoDenyCallback`, `RecordingApprovalCallback`. |
| `crypseal-runtime/.../gateway/ApprovalDriftChecker.kt` | **New.** Binds approval to command string and file SHA-256 hash. Detects command drift and file modification drift before execution. |
| `crypseal-runtime/.../gateway/AgentOrchestrator.kt` | Added `approvalCallback`, `sessionLane`, `projectRoot` params. ASK path: builds bound request → sets lane state → calls callback → checks drift → executes or blocks. |
| `crypseal-runtime/.../gateway/SessionLane.kt` | Added `setWaitingForApproval()` and `setExecuting()` for orchestrator-driven state transitions. |
| `crypseal-runtime/.../gateway/ApprovalFlowTest.kt` | **New.** 9 tests covering full approval lifecycle. |

### Key design decisions

1. **`ApprovalCallback` is a suspend interface.** This allows the real UI implementation to `suspendCancellableCoroutine` on a user tap without blocking the main thread. Tests use synchronous auto-approve/deny implementations.

2. **Drift binding is done at request creation time.** `ApprovalDriftChecker.bindRequest()` captures the command string and file hash at the moment of the approval request. The checker then compares against current state at execution time, after the callback returns.

3. **DENY never calls the callback.** The policy gate's DENY verdict is final — no approval is requested. Only ASK verdicts reach the callback. ALLOW verdicts skip the callback entirely.

4. **`ApprovalScope` is declared but only `ONCE` is implemented.** `SESSION` and `ALWAYS_FOR_PATTERN` scopes are enum values for future milestones but have no behavioral difference in M3.

5. **`policyGate` and `approvalCallback` remain optional.** M1 tests (no policy gate) and M2 tests (no callback) continue to work without changes.

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
| `:crypseal-runtime` | 40 | 40 | 0 | 0 | +9 new ApprovalFlowTest |
| `:crypseal-shell-bridge` | 0 | — | — | — | No test sources |
| `:ui` | 0 | — | — | — | No test sources |
| `:app` | 0 | — | — | — | No test sources |
| **Total** | **43** | **43** | **0** | **0** | |

### New M3 tests

| Test | Proves |
|------|--------|
| `testAskCommandAutoApproveExecutes` | ASK + approved → tool executes with real output |
| `testAskCommandAutoDenyDoesNotExecute` | ASK + denied → tool does not execute |
| `testDenyCommandNeverCallsApproval` | DENY → RecordingCallback receives zero requests |
| `testAllowCommandNoApprovalCall` | ALLOW → RecordingCallback receives zero requests |
| `testApprovalEventsEmittedInOrder` | APPROVAL_REQUEST emitted before APPROVAL_RESPONSE |
| `testSessionLaneWaitingState` | Lane state is WAITING_FOR_APPROVAL during callback |
| `testApprovalDriftBlocksExecution` | File modified after approval → DRIFT_BLOCKED |
| `testCommandDriftDetection` | Command string changed → drift detected |
| `cleanUp` | Temp dir cleanup |

### Quarantined tests: NONE

---

## Track status vs. `19_PRODUCTION_ROADMAP.md`

| Track | Name | Build-Clean | Tests Passing | Honest Status |
|-------|------|-------------|---------------|---------------|
| 0 | Project foundation | ✅ | ✅ | **DONE** |
| 1 | Gateway core | ✅ | ✅ | **PARTIAL** — events, lanes, JSONL work; session resume/fork untested |
| 2 | Termux execution node | ✅ | — | **SCAFFOLD + INTERFACE** — `CommandRunner` interface ready, mock proven |
| 3 | Tool registry & policy | ✅ | ✅ 18 tests | **M2 COMPLETE** — PolicyGate, PathSandbox, CommandClassifier integrated |
| 4 | File tools & checkpoints | ✅ | ✅ | **PARTIAL** — read+checkpoint+patch passing |
| 5 | Local model runtime | ✅ | ✅ | **PARTIAL** — mock + repair proven |
| 6 | Agent orchestrator | ✅ | ✅ | **M1+M2+M3 COMPLETE** — structured dispatch + policy gate + approval spine |
| 7 | Repo context/indexing | ❌ | — | **NOT STARTED** |
| 8 | Skills/subagents/hooks | ✅ | ✅ | **PARTIAL** — loader + hooks proven |
| 9 | Full UI polish | ✅ | — | **SCAFFOLD ONLY** |
| 10 | Git/release/lifecycle | ✅ | ✅ | **PARTIAL** — export works |
| 11 | Security/QA/perf | ✅ | ✅ | **M2+M3** — attack scenarios + approval drift proven |
| 12 | Advanced adapters | ❌ | — | **NOT STARTED** |

---

## Acceptance criteria checklist — M3

- [x] `./gradlew assembleDebug` passes (exit 0)
- [x] `./gradlew testDebugUnitTest` passes (exit 0)
- [x] No quarantined tests
- [x] ASK tools can be approved and then execute
- [x] ASK tools can be denied and then do not execute
- [x] DENY tools never execute and never ask for approval
- [x] ALLOW tools execute without approval
- [x] Approval events are persisted into session history
- [x] Approval drift is tested
- [x] BUILD_STATUS.md is updated honestly

---

## Milestone history

| Milestone | Date | Tests Before → After | Key Result |
|-----------|------|---------------------|------------|
| M0 | 2026-05-04 17:04 | 0 → 10 pass, 4 skip | Build-clean foundation |
| M1 | 2026-05-04 17:58 | 10 → 16 pass, 1 skip | Tool dispatch spine |
| M2 | 2026-05-04 18:13 | 16 → 34 pass, 0 skip | Policy-gated tool execution |
| M3 | 2026-05-04 18:29 | 34 → 43 pass, 0 skip | Approval request spine |

---

## Recommended next milestone

### M4 — Real Termux Diagnostic Spine

1. Detect whether Termux is installed on the device
2. Detect/check `com.termux.permission.RUN_COMMAND` permission status
3. Implement `TermuxCommandRunner` (real `CommandRunner` using `RUN_COMMAND` intent)
4. Run `python --version`, `pwd`, `ls` as smoke tests
5. Capture stdout/stderr/exit code through `ResultReceiver`
6. Log `COMMAND_START`, `COMMAND_OUTPUT`, `COMMAND_END` events
7. Surface diagnostic result in a minimal UI screen
8. Add mock instrumented tests for intent construction
