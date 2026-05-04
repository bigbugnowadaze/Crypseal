# Skills, Subagents, Hooks, and Slash Commands

## Skills are not sample prompts
Skills are operational playbooks the agent can load when a task matches a domain. They include constraints, allowed tools, diagnostics, verification steps, and failure handling.

## Skill format

```yaml
name: debug-python-project
version: 1
summary: Diagnose and repair Python project failures on Android/Termux.
allowed_tools:
  - file.read
  - file.patch
  - file.grep
  - termux.exec
  - git.diff
risk_notes:
  - package installs require approval
  - never read .env without explicit approval
```

Then markdown instructions.

## Required skill library

The package includes full starter skill files under `skills/`. Antigravity must install them in the app as bundled defaults and allow per-project override.

Core skill categories:

- Android app architecture
- Termux bootstrap/debug
- local model runtime
- tool protocol design
- permission policy
- patch/diff discipline
- repo mapping
- Python project build/debug
- Node/webapp build/debug
- Rust/Cargo in Termux
- Git workflow
- local server preview
- session compaction
- testing/QA/security
- release engineering
- performance/thermal optimization
- dependency/license vetting
- MCP/plugin bridge

## Subagents

Subagents run separate context windows/modes with limited tools.

Required built-ins:

- ExploreAgent: read/search only.
- PlanAgent: plan only, no edits.
- ImplementAgent: edits + safe tests.
- TestAgent: runs diagnostics and summarizes failures.
- GitAgent: status/diff/commit summaries.
- SecurityAgent: reviews commands, paths, secrets, dependencies.
- ReleaseAgent: build/export/release checklist.

## Hooks

Hooks are deterministic rules triggered by lifecycle events. They are not optional because LLM instructions alone are unreliable.

Required hook events:

- SessionStart
- UserPromptSubmit
- PreToolUse
- PostToolUse
- PermissionRequest
- PermissionDenied
- BeforeFileEdit
- AfterFileEdit
- BeforeCommand
- AfterCommand
- OnError
- BeforeCompact
- AfterCompact
- SessionEnd

Example policies:

- BeforeCommand: block `rm -rf`, `curl | sh`, secret file reads.
- AfterFileEdit: create snapshot, run formatter if configured.
- AfterCommand: summarize large output and store full log.
- PermissionRequest: show Android notification if app backgrounded.

## Slash commands

Required:

```text
/help
/plan
/act
/status
/diff
/test
/run
/stop
/permissions
/memory
/compact
/skills
/hooks
/subagents
/snapshot
/revert
/fork
/bootstrap-termux
/open-server
/commit
/export-session
```
