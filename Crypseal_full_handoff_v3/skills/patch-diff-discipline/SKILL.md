# Skill: Patch/Diff Discipline

Use when changing files.

Rules:
- Prefer patch over full write.
- Read file before editing.
- Check file hash before applying.
- Create checkpoint before edit.
- Show diff before applying unless mode explicitly auto-allows.
- Reread after user manually edits patch.

Verification:
- Revert works.
- Conflict detection works.
- Multi-file patch tested.
