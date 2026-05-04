# Skill: Hooks Automation

Use when implementing deterministic lifecycle actions.

Hook design:
- PreToolUse can block.
- PostToolUse can validate/log but not undo.
- BeforeCommand performs policy checks.
- AfterFileEdit can format/test.
- SessionStart loads memory and skills.
