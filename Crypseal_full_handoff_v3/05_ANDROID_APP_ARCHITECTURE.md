# Android App Architecture

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- Coroutines + Flow
- ViewModel per screen/feature
- Room for indexed state
- JSONL files for full transcripts/event logs
- DataStore for settings
- Foreground service for active agent runs
- WorkManager only for maintenance tasks such as indexing or cleanup

## Package structure

```text
crypseal-android/
  app/
    src/main/java/com/harrowhaus/crypseal/app/
      CrypsealApplication.kt
      MainActivity.kt
      CrypsealForegroundService.kt
  crypseal-runtime/
    src/main/java/com/harrowhaus/crypseal/runtime/
      gateway/
        CrypsealGateway.kt
        AgentOrchestrator.kt
        EventBus.kt
        SessionLane.kt
      models/
        ModelRuntime.kt
        ModelRouter.kt
        LiteRtLmRuntime.kt
        TermuxLlamaServerRuntime.kt
        MockModelRuntime.kt
      tools/
        Tool.kt
        ToolRegistry.kt
        FileTools.kt
        PatchTools.kt
        GitTools.kt
        TermuxTools.kt
        ProjectTools.kt
        SessionTools.kt
      context/
        ContextBuilder.kt
        RepoIndexer.kt
        RepoMap.kt
        SkillsLoader.kt
        MemoryManager.kt
        Compactor.kt
      storage/
        db/
        jsonl/
        snapshots/
  crypseal-shell-bridge/
    src/main/java/com/harrowhaus/crypseal/shellbridge/
      TermuxIntentRunner.kt
      TermuxSetupChecker.kt
      TermuxResultReceiver.kt
      TermuxProcessMonitor.kt
  crypseal-guard/
    src/main/java/com/harrowhaus/crypseal/guard/
      ApprovalEngine.kt
      CommandClassifier.kt
      PathSandbox.kt
      ProtectedPaths.kt
      ApprovalBinding.kt
      RiskLevel.kt
  ui/
    src/main/java/com/harrowhaus/crypseal/ui/
      screens/
      components/
      navigation/
  util/
    src/main/java/com/harrowhaus/crypseal/util/
```

## UI architecture

Use unidirectional data flow:

```text
Gateway events → Repository/ViewModel state → Compose UI
Compose user events → ViewModel intent → Gateway command
```

No tool should be executed directly from a composable.

## Foreground service

Use foreground service for active agent runs that stream output, keep a local server monitor alive, or wait for approvals. Android 14+ service type declarations must be handled explicitly. WorkManager is for scheduled maintenance, not live interactive agent loops.

## Storage approach

- App-private internal: session metadata, DB, settings, secrets if any.
- App-specific external: user-exportable logs/snapshots if enabled.
- Termux home: execution workspace by default.
- Shared storage: only after explicit user grant and policy label.

## Permissions

Required:
- `com.termux.permission.RUN_COMMAND`
- foreground service permission(s)
- notification permission as required by Android version

Optional:
- storage/document access via SAF
- network if cloud/runtime/model download enabled

## Android-specific failure cases

- Termux missing
- RUN_COMMAND permission missing
- `allow-external-apps` not set
- target SDK package visibility prevents Termux intent resolution
- Termux command starts but UI cannot foreground session
- storage path not accessible due scoped storage
- process killed by battery optimization
- model runtime OOM/thermal throttling
- foreground service type misdeclared

Each must have diagnostics in `TermuxSetupChecker` or `RuntimeHealthChecker`.
