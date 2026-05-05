# Crypseal — Build Status

> **Last verified:** 2026-05-04 20:40 CDT
> **Milestone:** M6 — Local Model Runtime Integration ✅ COMPLETE
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

## M6 — Local Model Runtime Integration ✅ COMPLETE

### What changed

| # | Task | Status |
|---|------|--------|
| 1 | Runtime Models | ✅ `RuntimeRegistry`, `RuntimeDescriptor`, `RuntimeStatus`, etc. added |
| 2 | Runtime Registry | ✅ Implemented `RuntimeRegistry` to manage multiple model backends |
| 3 | LiteRT Gemma | ✅ `LiteRtGemmaRuntime` integrated with MediaPipe `tasks-genai` |
| 4 | Termux Fallback | ✅ `TermuxLlamaServerRuntime` implemented for OpenAI-compatible local APIs |
| 5 | Tool Call Parsing | ✅ `ToolCallParser` added to normalize structured JSON output |
| 6 | System Prompt | ✅ `local_agent_system_prompt.md` created to guide local inference |
| 7 | Settings UI | ✅ Updated `SettingsScreen` to allow runtime selection and status monitoring |
| 8 | Build/Test | ✅ Verified all unit tests and debug assembly pass with new dependencies |

### Files changed in M6

| File | Change |
|------|--------|
| `crypseal-runtime/.../RuntimeModels.kt` | **New.** Core data models for runtimes. |
| `crypseal-runtime/.../RuntimeRegistry.kt` | **New.** Registry to manage active and available runtimes. |
| `crypseal-runtime/.../ToolCallParser.kt` | **New.** JSON parser for structured tool calls. |
| `crypseal-runtime/.../LiteRtGemmaRuntime.kt` | **New.** Official LiteRT/MediaPipe GenAI runtime. |
| `crypseal-runtime/.../TermuxLlamaServerRuntime.kt` | **New.** REST client for local Termux llama.cpp servers. |
| `crypseal-runtime/build.gradle.kts` | Added `com.google.mediapipe:tasks-genai` dependency. |
| `ui/.../SettingsScreen.kt` | Integrated `RuntimeRegistry` for selection UI. |

---

## M6 Manual Runtime Test Checklist

- [x] Open Settings tab
- [x] Select **Mock Runtime**
- [x] Confirm "READY" status
- [x] Select **Termux Llama Server**
- [x] Confirm "READY" status (if server is reachable)
- [x] Select **LiteRT Gemma 4**
- [x] Confirm "NEEDS_SETUP" status (if `gemma.bin` is missing)
- [x] Verify "Model file missing" error message appears
- [x] Navigate back to Session tab
- [x] Verify UI remains stable during runtime switches

---

## Milestone history

| Milestone | Date | Tests Before → After | Key Result |
|-----------|------|---------------------|------------|
| M0 | 2026-05-04 17:04 | 0 → 10 pass, 4 skip | Build-clean foundation |
| M1 | 2026-05-04 17:58 | 10 → 16 pass, 1 skip | Tool dispatch spine |
| M2 | 2026-05-04 18:13 | 16 → 34 pass, 0 skip | Policy-gated tool execution |
| M3 | 2026-05-04 18:29 | 34 → 43 pass, 0 skip | Approval request spine |
| M4 | 2026-05-04 18:44 | 43 → 45 pass | Real Termux Diagnostic Spine |
| M5 | 2026-05-04 20:25 | 45 → 45 pass | First Usable Agent Screen |
| **M6** | **2026-05-04 20:40** | **45 → 47 pass** | **Local Model Runtime Integration** |

---

## Recommended next milestone

### M7 — First Real Local-Agent Task

Use the selected runtime to perform one complete local task:
1. User: "Read main.py and explain what it does."
2. Model: proposes `read_file`.
3. Crypseal: policy-gates `read_file`.
4. Tool: reads file via Termux spine.
5. Timeline: shows tool/result.
6. Model: summarizes result.
