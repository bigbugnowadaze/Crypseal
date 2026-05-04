# Full UI/UX Specification

## Main screens

### Home / Projects
- Project list
- Recent sessions
- Runtime health
- Termux setup status
- Create/import project
- Search projects

### Project Workspace
- Chat/thread pane
- Files tab
- Git tab
- Runs tab
- Checkpoints tab
- Settings tab

### Chat / Agent Thread
- User messages
- Assistant messages
- Tool cards
- Approval cards
- Diff cards
- Streaming command output cards
- Plan cards
- Error cards
- Status chips: model, permission mode, project root, active run

### Diff Viewer
- File list
- Unified diff default
- Side-by-side optional for tablets/foldables
- Edit patch
- Apply / reject / apply+test

### Command Approval
Must show:
- exact command
- parsed argv
- cwd
- reason
- risk
- protected paths touched
- package/network/external flags
- allow once / deny once / always allow / always deny / edit

### Runtime Settings
- Model runtime selector
- Model download/import
- Context budget
- Temperature/reasoning settings if exposed
- Local/cloud privacy labels

### Policy Settings
- permission mode
- allow/ask/deny rules
- protected paths
- package install policy
- network policy
- external storage grants

### Skills/Subagents/Hooks
- list bundled skills
- project overrides
- enable/disable
- inspect source
- run validation

## UI principles

- Mobile-first, not terminal-first.
- Every action visible.
- No wall of logs unless expanded.
- Show enough command detail to be trusted.
- Approvals must be one-tap but not blind.
- Long-running runs must show notifications.
- User can interrupt at any time.
- User can steer without corrupting current run.

## Core cards

- PlanCard
- ToolCallCard
- ApprovalCard
- CommandOutputCard
- DiffCard
- CheckpointCard
- ErrorRepairCard
- GitStatusCard
- RuntimeHealthCard

## Tablet/foldable mode

Use multi-pane layout:

- left: project/thread list
- center: chat
- right: files/diff/logs

## Accessibility

- readable monospace output
- copy buttons
- large approval buttons
- high contrast diff mode
- no color-only risk signaling
