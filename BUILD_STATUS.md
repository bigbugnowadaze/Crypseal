# Crypseal — Build Status

> **Last verified:** 2026-05-04 18:44 CDT
> **Milestone:** M4 — Real Termux Diagnostic Spine ✅ IN PROGRESS (Code complete, testing needed)
> **`./gradlew assembleDebug`:** ✅ PASS (exit 0)
> **`./gradlew testDebugUnitTest`:** ✅ PASS (exit 0)

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

## M4 — Real Termux Diagnostic Spine (this pass)

### What changed

| # | Task | Status |
|---|------|--------|
| 1 | Manifest & Permission | ✅ `RUN_COMMAND` permission verified |
| 2 | `TermuxCommandRunner` | ✅ Implemented using Intents + ResultReceiver |
| 3 | Diagnostic Commands | ✅ `pwd`, `ls`, `python --version` integrated |
| 4 | Lifecycle Events | ✅ `COMMAND_START`, `COMMAND_END` emitted with metadata |
| 5 | Diagnostic UI | ✅ `DiagnosticScreen` implemented in Compose |
| 6 | Unit Tests | ✅ Intent/Command structure tests added |
| 7 | Manual Checklist | ✅ Added below |
| 8 | Policy Integration | ✅ Commands are run through the `TermuxCommandRunner` abstraction |

### Files changed in M4

| File | Change |
|------|--------|
| `app/src/main/java/.../MainActivity.kt` | Wired `DiagnosticScreen` as the main content. Initializes real Termux runners. |
| `crypseal-shell-bridge/build.gradle.kts` | Added `crypseal-runtime` and `junit` dependencies. |
| `crypseal-shell-bridge/.../TermuxCommandRunner.kt` | **New.** Implements `CommandRunner`. Handles Intent orchestration and broadcast receiver lifecycle. |
| `ui/.../screens/DiagnosticScreen.kt` | **New.** Compose UI for Termux status and command execution. |
| `crypseal-shell-bridge/src/test/.../TermuxBridgeTest.kt` | **New.** Unit tests for bridge structures. |

---

## M4 Manual Device Test Checklist

- [ ] Install debug APK on physical Android device
- [ ] Install Termux from F-Droid
- [ ] Enable `allow-external-apps=true` in `~/.termux/termux.properties` (restart Termux)
- [ ] Grant `RUN_COMMAND` permission to Crypseal in Android Settings
- [ ] Open Crypseal diagnostics
- [ ] Verify **Termux installed = YES**
- [ ] Verify **RUN_COMMAND permission = YES**
- [ ] Click **[pwd]** → Verify output contains `/data/data/com.termux/files/home`
- [ ] Click **[ls]** → Verify file listing appears
- [ ] Click **[python --version]** → Verify Python version (if installed) or error message
- [ ] Check Logcat → Verify `CrypsealEvent` logs for `COMMAND_START` and `COMMAND_END`

---

## Milestone history

| Milestone | Date | Tests Before → After | Key Result |
|-----------|------|---------------------|------------|
| M0 | 2026-05-04 17:04 | 0 → 10 pass, 4 skip | Build-clean foundation |
| M1 | 2026-05-04 17:58 | 10 → 16 pass, 1 skip | Tool dispatch spine |
| M2 | 2026-05-04 18:13 | 16 → 34 pass, 0 skip | Policy-gated tool execution |
| M3 | 2026-05-04 18:29 | 34 → 43 pass, 0 skip | Approval request spine |
| **M4** | **2026-05-04 18:44** | **43 → 45 pass** | **Real Termux Diagnostic Spine** |

---

## Recommended next milestone

### M5 — First Usable Agent Screen

- Project selector UI
- Chat/event timeline (Event visualization)
- Tool cards (Show what the agent is doing)
- Approval cards (Interactive ASK flow)
- Guided flow: "Run Python diagnostic"
- Session persistence / Resume

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
