# Antigravity Issue Backlog

Use these as implementation tickets. Each ticket requires tests and BUILD_LOG entry.

## Epic A — Foundation

### A01 Create Android project and package structure
Acceptance: app builds, empty Compose shell opens, package tree matches spec.

### A02 Implement event model and JSONL writer
Acceptance: events append durably and can be replayed.

### A03 Implement Room indexes
Acceptance: projects/sessions/events can be listed without parsing JSONL every time.

### A04 Implement CrypsealGateway shell
Acceptance: gateway can create project/session and emit events.

### A05 Implement SessionLane
Acceptance: concurrent requests serialize; interrupt flag stops next tool boundary.

## Epic B — Termux

### B01 TermuxSetupChecker
Acceptance: detects installed/missing Termux, permission state, allow-external-apps guidance.

### B02 RUN_COMMAND foreground execution
Acceptance: runs `echo crypseal_OK` and captures output.

### B03 Command output streaming
Acceptance: command emitting multiple lines streams to UI/log.

### B04 Background process monitor
Acceptance: local server starts, output polls, stop works.

## Epic C — Policy

### C01 PathSandbox
Acceptance: traversal and protected paths rejected.

### C02 CommandClassifier
Acceptance: allow/ask/deny tests pass for policy examples.

### C03 Approval cards and resolver
Acceptance: approve/deny once and always rules persist.

### C04 Approval binding/drift
Acceptance: changed script after approval cannot run.

## Epic D — Tools

### D01 ToolRegistry
Acceptance: tools register by name/group and validate schemas.

### D02 File read/list/glob/grep
Acceptance: line-numbered excerpts and truncation work.

### D03 Patch parser and diff renderer
Acceptance: multi-hunk patch previews correctly.

### D04 Checkpoint/revert
Acceptance: revert restores pre-edit state.

### D05 Git status/diff/add/commit
Acceptance: git operations require proper policy and show actual diff.

## Epic E — Models

### E01 ModelRuntime interface and MockModelRuntime
Acceptance: deterministic tests can drive tool loop without real model.

### E02 LiteRT-LM integration spike-to-production
Acceptance: documented SDK integration and working chat/tool-call path or explicit blocker.

### E03 Termux llama-server adapter
Acceptance: app connects to localhost model endpoint started from Termux.

### E04 Model output repair
Acceptance: malformed JSON cannot execute; repair path tested.

## Epic F — Agent Loop

### F01 ContextBuilder
Acceptance: constructs budgeted context from AGENT.md, repo map, session state.

### F02 Plan mode
Acceptance: mutating tools blocked; plan card generated.

### F03 Act loop
Acceptance: model tool call → policy → tool result → next model step.

### F04 Failure loop detection
Acceptance: repeated identical failures stop and summarize.

### F05 Context compaction
Acceptance: long output summarized and full logs saved.

## Epic G — UI

### G01 Project/thread screens
Acceptance: user can create/open/resume projects.

### G02 Tool cards
Acceptance: file reads, commands, patches, approvals render distinctly.

### G03 Diff viewer
Acceptance: apply/reject/edit patch works.

### G04 Runtime/policy settings
Acceptance: user can inspect active runtime and permission profile.

### G05 Notifications
Acceptance: active run and approval needed notifications work.

## Epic H — Skills/hooks/subagents

### H01 Bundled skills loader
Acceptance: default skills list loads and is selectable.

### H02 Project skill override
Acceptance: project `.crypseal/skills` overrides bundled skill.

### H03 Hook engine
Acceptance: PreCommand hook can block execution.

### H04 Subagent runner
Acceptance: ExploreAgent can search and return summary without edit permission.

## Epic I — Release

### I01 Security test suite
Acceptance: all attack scenarios pass.

### I02 Export/import
Acceptance: project/session/patch export works.

### I03 Full golden path demos
Acceptance: Python, Node/web, repo repair, denial, drift tests pass on target device.
