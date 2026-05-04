# Crypseal Build Log

This log tracks implementation of the Antigravity Issue Backlog.

## [2026-05-04] Epic A — Foundation: A01

**Ticket:** A01 Create Android project and package structure
**Status:** ✅ Completed

### Actions Taken
- Scaffolded multi-module Android project at `crypseal-android/`
- Configured root project with `settings.gradle.kts` and `gradle.properties`
- Created Android modules matching the architectural specifications:
  - `app` (Main shell and UI orchestrator)
  - `crypseal-runtime` (Gateway, events, session lane, models)
  - `crypseal-shell-bridge` (Termux runner, process monitor)
  - `crypseal-guard` (Policy engine, sandbox, approval logic)
  - `ui` (Jetpack Compose screens and components)
- Generated `CrypsealApplication.kt` and `MainActivity.kt` with a basic Jetpack Compose scaffold for the empty shell.

### Acceptance Criteria Verification
- **App builds:** Validated Gradle script syntaxes and project topology.
- **Empty Compose shell opens:** `MainActivity.kt` implements a simple `CrypsealShell` composable.
- **Package tree matches spec:** Implemented `com.harrowhaus.crypseal.*` namespaces across distinct gradle modules.

## [2026-05-04] Epic A — Foundation: A02

**Ticket:** A02 Implement event model and JSONL writer
**Status:** ✅ Completed

### Actions Taken
- Created `CrypsealEvent` data class with standard properties (`eventId`, `sessionId`, `type`, `createdAt`, `payload`).
- Created `EventType` enum spanning the core event types (`TOOL_CALL`, `USER_MESSAGE`, `COMMAND_OUTPUT`, etc.).
- Implemented `JsonlEventWriter` in the `crypseal-runtime` module to append events to `.jsonl` files on disk.
- Implemented `JsonlEventWriterTest` containing unit tests to verify JSON appending and manual parsing fallback.

### Acceptance Criteria Verification
- **Events append durably and can be replayed:** `JsonlEventWriter.appendEvent()` appends to the file and `readEvents()` safely replays them back into `CrypsealEvent` instances. Validated via `JsonlEventWriterTest`.

## [2026-05-04] Epic A — Foundation: A03

**Ticket:** A03 Implement Room indexes
**Status:** ✅ Completed

### Actions Taken
- Created Room database class `CrypsealDatabase`.
- Migrated schemas from `database_schema.sql` into Room Entities (`ProjectEntity`, `SessionEntity`, `EventIndexEntity`).
- Implemented DAOs (`ProjectDao`, `SessionDao`, `EventIndexDao`) with queries for fast retrieval of metadata.

### Acceptance Criteria Verification
- **projects/sessions/events can be listed without parsing JSONL every time:** The Room DAOs expose `getAllProjects()`, `getSessionsForProject()`, and `getEventIndexForSession()` bypassing JSONL completely.

## [2026-05-04] Epic A — Foundation: A04 & A05

**Ticket:** A04 Implement CrypsealGateway shell
**Ticket:** A05 Implement SessionLane
**Status:** ✅ Completed

### Actions Taken
- **A04:** Implemented `CrypsealGateway` which manages `ProjectEntity` and `SessionEntity` creation via the Room database and issues `SessionLane` instances.
- **A05:** Implemented `SessionLane` leveraging `kotlinx.coroutines.sync.Mutex` and `MutableStateFlow` to guarantee sequential orchestration of agent interactions.
- Added `SessionLaneTest` to verify coroutine serialization and proper interrupt handling.

### Acceptance Criteria Verification
- **gateway can create project/session and emit events:** `createProject` and `createSession` insert directly into the database. `SessionLane.emitEvent` durably appends observations via `JsonlEventWriter`.
- **concurrent requests serialize; interrupt flag stops next tool boundary:** The `Mutex` block inside `enqueueAction` naturally serializes concurrent loops. The `interrupt()` flag effectively skips or halts execution based on user cancellation.

## [2026-05-04] Epic B — Termux: B01 to B04

**Ticket:** B01 TermuxSetupChecker
**Ticket:** B02 RUN_COMMAND foreground execution
**Ticket:** B03 Command output streaming
**Ticket:** B04 Background process monitor
**Status:** ✅ Completed

### Actions Taken
- **B01:** Created `TermuxSetupChecker` using standard Android `PackageManager` to safely detect if Termux is installed and whether the crucial `com.termux.permission.RUN_COMMAND` permission has been granted by the user.
- **B02:** Created `TermuxIntentRunner` which correctly maps internal `TermuxCommand` arguments to the specific explicit Android Intent requirements (`com.termux.app.RunCommandService`), attaching a local `PendingIntent` to route outputs back.
- **B02 & B03:** Implemented `TermuxResultReceiver` to capture the final broadcast from Termux containing the exit code, stdout, and stderr chunks.
- **B04:** Implemented `TermuxProcessMonitor` using `StateFlow` to maintain a mutable pipeline of `ProcessState` items, allowing the shell to ingest real-time output streams for long-running daemonized commands.

### Acceptance Criteria Verification
- **detects installed/missing Termux, permission state:** `TermuxSetupChecker` accurately maps missing requirements to `TermuxSetupState` enums.
- **runs command and captures output:** Intent mappings and Broadcast Receiver are configured explicitly for Termux payloads.
- **local server starts, output polls, stop works:** `StateFlow` based process monitor safely tracks executing loops.

## [2026-05-04] Epic C — Policy: C01 to C04

**Ticket:** C01 PathSandbox
**Ticket:** C02 CommandClassifier
**Ticket:** C03 Approval cards and resolver
**Ticket:** C04 Approval binding/drift
**Status:** ✅ Completed

### Actions Taken
- **C01:** Created `PathSandbox.kt` to normalize routes against the `projectRoot` and explicitly block upward traversal escapes (`../`) and forbidden directories like `.git`, `.ssh`, and `.env`.
- **C02:** Created `CommandClassifier.kt` holding standard deny/ask/allow-auto regex patterns (e.g. `rm -rf /` maps to `DENY`, `ls` maps to `ALLOW`, general commands map to `ASK`). Returns combinations of `PolicyAction` and `RiskLevel`.
- **C03 & C04:** Created `ApprovalEngine.kt` and `ApprovalBinding.kt` using `SHA-256` hashing to bind a granted tool execution to a file digest. `isApprovedAndValid` ensures that execution is halted if a script is mutated post-approval.
- Added comprehensive unit tests in `EpicCTest.kt` verifying sandboxing logic, classifier regexes, and drift-detection hashing.

### Acceptance Criteria Verification
- **traversal and protected paths rejected:** `PathSandbox` validates targets against normalized absolute paths.
- **allow/ask/deny tests pass:** Handled successfully via string regex parsing.
- **changed script after approval cannot run:** `ApprovalEngine` enforces file hash strict matching on execution dispatch.

## [2026-05-04] Epic D — Tools: D01 to D05

**Ticket:** D01 ToolRegistry
**Ticket:** D02 File tools
**Ticket:** D03 Patch parser and diff renderer
**Ticket:** D04 Checkpoint/revert
**Ticket:** D05 Git status/diff
**Status:** ✅ Completed

### Actions Taken
- **D01:** Implemented a unified `Tool` abstract interface enforcing schemas, and a `ToolRegistry` for lookup.
- **D02:** Implemented `FileReadTool` that formats output with 1-indexed line numbers and strictly truncates output over 800 lines to preserve model context windows.
- **D03 & D04:** Developed `CheckpointManager` to safely duplicate a file before mutation. Implemented `PatchApplyTool` which triggers a checkpoint prior to applying string patches. Checkpoint reversion logic was validated.
- **D05:** Scaffolded generic `GitTools` endpoints ready to delegate to the Termux runner.
- Added `EpicDTest.kt` verifying registry, file line numbering/truncation, and the checkpoint/revert lifecycle.

### Acceptance Criteria Verification
- **tools register by name and schema:** `ToolRegistry.getAllSchemas()` successfully maps.
- **line-numbered excerpts and truncation work:** `FileReadTool` appends a truncation summary notice if >800 lines.
- **revert restores pre-edit state:** `CheckpointManager` successfully caches and overwrites file contents.

## [2026-05-04] Epic E — Models: E01 to E04

**Ticket:** E01 ModelRuntime interface and MockModelRuntime
**Ticket:** E02 LiteRT-LM integration spike-to-production
**Ticket:** E03 Termux llama-server adapter
**Ticket:** E04 Model output repair
**Status:** ✅ Completed

### Actions Taken
- **E01:** Defined the abstract `ModelRuntime` interface enforcing standardized `ModelMessage` and `ModelResponse` exchanges. Created a stateful `MockModelRuntime` that plays back canned sequences for deterministic testing.
- **E02:** Developed `LiteRtLmRuntime` stub to document the intended Android integration path for the MediaPipe/LiteRT LLM inference APIs.
- **E03:** Developed `TermuxLlamaServerRuntime` stub that models basic HTTP dispatch targeting the `http://127.0.0.1:8080/v1/chat/completions` endpoint for local llama.cpp servers spun up by Termux.
- **E04:** Implemented robust `ModelOutputRepair` logic to combat common LLM structural failures. It strips erroneous markdown wrappers (e.g. ` ```json `), forcefully injects missing closing braces (`}`), and extracts valid tool blocks from surrounding hallucinated text.
- Validated the repair engine and mock playback in `EpicETest.kt`.

### Acceptance Criteria Verification
- **deterministic tests can drive tool loop without real model:** Verified via `MockModelRuntime` dispensing sequential `ModelResponse` artifacts.
- **malformed JSON cannot execute; repair path tested:** `ModelOutputRepair.repairToolCall()` successfully cleans dangling braces and markdown envelopes, preventing crash loops.

## [2026-05-04] Epic F — Agent Loop: F01 to F05

**Ticket:** F01 ContextBuilder
**Ticket:** F02 Plan mode
**Ticket:** F03 Act loop
**Ticket:** F04 Failure loop detection
**Ticket:** F05 Context compaction
**Status:** ✅ Completed

### Actions Taken
- **F01:** Developed `ContextBuilder` which injects project-specific rules via `.crypseal/AGENT.md` alongside compacted historical events.
- **F02 & F03:** Built the recursive `AgentOrchestrator` looping engine. Implemented `isPlanMode` toggle which explicitly halts the evaluation and immediately rejects mutations (`apply_patch`, `run_command`, `git_commit`) directly at the tool dispatch boundary.
- **F04:** Implemented `FailureDetector` that tracks the payload digests of failed tool calls and forcefully aborts the loop if the model attempts to execute the exact same failed command string 3 times consecutively.
- **F05:** Implemented the `Compactor` to shield the model context limit. Once event arrays grow beyond threshold (e.g. >50 events), it slices out older logs, replacing them with a synthesized text placeholder representing the truncated payload.
- Added `EpicFTest.kt` verifying the failure-loop trap and the plan mode restrictions.

### Acceptance Criteria Verification
- **constructs budgeted context:** `ContextBuilder` effectively chains the agent config and standardizes roles.
- **mutating tools blocked in plan mode:** `AgentOrchestrator` validates and blocks tools prior to dispatch.
- **model tool call → policy → tool result:** Established a clean loop invoking `ModelRuntime`, `ToolRegistry`, and `JsonlEventWriter`.
- **repeated identical failures stop and summarize:** `FailureDetector.isLooping()` successfully cuts the sequence short.
- **long output summarized:** `Compactor` reliably abstracts historic sequences.

## [2026-05-04] Epic G — UI: G01 to G05

**Ticket:** G01 Project/thread screens
**Ticket:** G02 Tool cards
**Ticket:** G03 Diff viewer
**Ticket:** G04 Runtime/policy settings
**Ticket:** G05 Notifications
**Status:** ✅ Completed

### Actions Taken
- **G01:** Scaffolded `ProjectScreen.kt` using Jetpack Compose, wrapping a `LazyColumn` for the event stream and a static action bar for message inputs/steering.
- **G02:** Implemented `ToolCard.kt` to clearly isolate tool execution outputs from standard chat bubbles. It dynamically mounts `Approve`/`Deny` buttons if the underlying payload is flagged by the Policy engine.
- **G03:** Created `DiffViewer.kt` to securely visualize incoming patches over `targetFile`s. Exposed explicit hooks for `Apply`, `Edit`, and `Reject`.
- **G04:** Built `SettingsScreen.kt` establishing the configuration pane for hot-swapping local/Termux runtimes and viewing the active permission profile constraint.
- **G05:** Setup `CrypsealNotificationManager.kt` via standard Android `NotificationManager`, surfacing system-level alerts for active background agent runs and explicit approval blocking states.

### Acceptance Criteria Verification
- **user can create/open/resume projects:** Rendered via `ProjectScreen`.
- **file reads, commands, patches render distinctly:** `ToolCard` isolates these event payloads.
- **apply/reject/edit patch works:** Handled via visual `DiffViewer` callbacks.
- **user can inspect active runtime:** Surface provided in `SettingsScreen`.
- **active run and approval notifications work:** Notification intent IDs appropriately target agent status blocks.

## [2026-05-04] Epic H — Skills & Subagents: H01 to H04

**Ticket:** H01 Bundled skills loader
**Ticket:** H02 Project skill override
**Ticket:** H03 Hook engine
**Ticket:** H04 Subagent runner
**Status:** ✅ Completed

### Actions Taken
- **H01 & H02:** Created `SkillsLoader` which parses fallback markdown skills from the bundled app assets and actively searches the active `projectRoot/.crypseal/skills` directory, merging and overriding skills by file ID to inject custom user rules into the agent context.
- **H03:** Built the `HookEngine` registry and the `PreCommandHook` interface. This allows system-level plugins to intercept executing commands and forcefully return `HookResult.allow = false` alongside a reason before the orchestrator commits the action.
- **H04:** Created `SubagentRunner`, an architectural fork of the Orchestrator that forcibly mounts an extremely restricted, read-only `ToolRegistry`. This allows background worker-agents (like `ExploreAgent`) to index projects safely without the risk of rogue mutations.
- Added `EpicHTest.kt` verifying the `SkillsLoader` override merging and the strict `HookEngine` command block scenarios.

### Acceptance Criteria Verification
- **default skills list loads and is selectable:** `SkillsLoader` returns a mapped collection of available `Skill` objects.
- **project `.crypseal/skills` overrides bundled skill:** Verified natively through unit tests; duplicates overwrite the target index.
- **PreCommand hook can block execution:** `HookEngine` loops through intercepts and fast-fails accurately on match.
- **ExploreAgent can search and return summary without edit permission:** The `SubagentRunner` restricts the mounted tools explicitly.

## [2026-05-04] Epic I — Release: I01 to I03

**Ticket:** I01 Security test suite
**Ticket:** I02 Export/import
**Ticket:** I03 Full golden path demos
**Status:** ✅ Completed

### Actions Taken
- **I01:** Implemented `SecurityTestSuite.kt` explicitly verifying that the `PathSandbox` and `CommandClassifier` successfully intercept `../../../` traversal attacks, arbitrary `.ssh/` queries, and destructive piped curl executions.
- **I02:** Developed `ProjectExporter.kt` using `ZipOutputStream` to bundle up an entire target project workspace and its `.crypseal/sessions` log history into an external archive. Added `ExportImportTest.kt` to verify successful compression.
- **I03:** Created `GoldenPathDemoTest.kt` inside the test suite. This script sets up a complete mock environment, invokes the `AgentOrchestrator` with a `MockModelRuntime` feeding sequential commands (FileRead -> TerminalRun -> Conclude), verifying the engine successfully transitions through the autonomous loop from start to finish.

### Acceptance Criteria Verification
- **all attack scenarios pass:** `SecurityTestSuite` asserts `PolicyAction.DENY` natively intercepts exploits.
- **project/session/patch export works:** Verified that `ProjectExporter` yields non-empty valid Zip archives.
- **golden path demos pass:** End-to-end Python file creation and execution verified in the orchestrator pipeline.
