# Sessions, Memory, and Context Compaction

## Session storage

Each project has:

```text
.crypseal/
  AGENT.md
  memory.md
  settings.json
  skills/
  hooks/
  subagents/
  checkpoints/
  sessions/
    <session-id>.jsonl
```

The Android app also stores indexes in Room.

## Session JSONL event types

- user_message
- assistant_message
- model_request_summary
- tool_call
- tool_result
- approval_request
- approval_result
- patch_proposed
- patch_applied
- command_started
- command_output
- command_finished
- checkpoint_created
- compaction
- interruption
- error

## Memory levels

### Global app memory
User preferences, allowed workflows, common build style. Sensitive data not stored unless explicit.

### Project memory
Conventions, commands, architecture decisions, known pitfalls.

### Session memory
Current task, files touched, failures, decisions.

## AGENT.md
Project instruction file. Equivalent in spirit to CLAUDE.md, but clean-room and app-specific.

## Context compaction

Trigger when:

- context budget > threshold
- command output huge
- repeated logs
- session length high

Keep:

- goal
- plan
- current state
- files changed
- command outcomes
- failed attempts
- decisions
- pending TODO

Drop/summarize:

- raw long logs
- duplicate outputs
- obsolete searches

## Resume

On resume:

- load project AGENT.md
- load memory.md
- load last compacted session state
- read git status/diff
- read recent files touched if needed
- display status card

## Fork

A fork creates new session ID with same project/checkpoint base and compacted context. Useful when exploring alternatives.
