# Full-App Product Specification

## Name
Crypseal Android

## One-line description
A local-first Android coding agent that uses on-device models and a governed Termux execution node to build, run, debug, and manage software projects directly from a phone.

## Target user
A builder who uses mobile AI chat heavily, creates many projects, and needs the phone to execute code, run packages, test projects, modify files, and iterate without manually living inside Termux.

## Core use cases

1. **Create a new runnable project** from a chat request.
2. **Open an existing repo/folder** and ask the agent to inspect, improve, fix, or extend it.
3. **Run Python/Node/Rust/web projects** through Termux.
4. **Debug errors** by capturing stdout/stderr and feeding observations back to the model.
5. **Patch files safely** with reviewable diffs and snapshots.
6. **Use git**: status, diff, commit, branch, rollback.
7. **Serve web previews** over localhost and open Android browser/WebView.
8. **Persist project memory and skills** across sessions.
9. **Resume/fork/rollback** agent sessions.
10. **Operate offline/private** where local models and installed packages allow.

## Non-negotiable full-app capabilities

### Agent operation
- Plan mode before high-impact edits.
- Typed tool calls only.
- Tool result feedback loop.
- Session lane serialization.
- Interrupt/steer current run.
- Context compaction and memory reload.
- Subagents for explore/plan/implement/test/git/security.

### Execution
- Termux bridge using RUN_COMMAND.
- Foreground and background commands.
- PTY support where needed.
- Process output streaming.
- Long-running monitor tools.
- Process stop/kill.
- Command timeout.
- Working-directory binding.
- Approval-drift detection.

### Editing
- Read/list/glob/grep tools.
- Structured patch tool.
- Multi-file patches.
- Diff preview.
- Editable patch before apply.
- Snapshots before every edit.
- Revert last patch/session checkpoint.
- Conflict detection if file changed since read.

### Safety
- Project-root sandbox.
- Protected paths.
- Allow/ask/deny policy.
- Risk classifier.
- Strict inline eval approval.
- Package install approval.
- Network command approval.
- External storage approval.
- No destructive commands without high-friction confirmation.
- Full audit log.

### Context
- AGENT.md project instructions.
- Skills folder.
- Memory file.
- Repo map.
- Recent files touched.
- Git state.
- Summarized command history.
- Model-output validation/retry.

### UI
- Project list.
- Thread list grouped by project.
- Chat with tool cards.
- Streaming command output.
- Approval cards.
- Diff cards.
- File viewer/editor light mode.
- Git status/diff UI.
- Logs and checkpoints.
- Model/runtime selector.
- Permission profile editor.
- Slash command palette.

## Success standard

A user can paste a complex app idea, let Crypseal plan it, create the project, install dependencies, run tests, iterate on failures, preview the app, commit changes, and resume later — all from Android without manually operating Termux except for initial setup/permissions.
