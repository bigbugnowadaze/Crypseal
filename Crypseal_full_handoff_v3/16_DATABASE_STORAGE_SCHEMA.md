# Database and Storage Schema

## Storage layers

### Room DB
Fast indexes, project/session metadata, UI state, event summaries.

### JSONL transcripts
Full durable event stream per session.

### Snapshot files
Pre-edit file contents and patch metadata.

### Termux workspace
Actual executable project files.

## Entities

- Project
- Session
- EventIndex
- ToolCall
- Approval
- CommandRun
- Patch
- Checkpoint
- ModelRuntimeInfo
- Skill
- Hook
- Subagent
- PermissionRule
- RepoIndexEntry

## SQL starter
See `schemas/database_schema.sql`.

## Project storage layout

```text
~/Crypseal/projects/<slug>/
  <actual project files>
  .crypseal/
    AGENT.md
    memory.md
    settings.json
    skills/
    hooks/
    subagents/
    checkpoints/
    sessions/
```

## JSONL durability

Every event is appended immediately. Room indexes can be rebuilt from JSONL if corrupted.

## Export

Allow exporting:

- session transcript
- patch bundle
- project zip
- logs
- source registry
- policy config
