# Skill: Room + JSONL Storage

Use for persistence.

Rules:
- JSONL is source of truth for event transcript.
- Room stores indexes and UI query state.
- Room can be rebuilt from JSONL.
- Redact secrets before both.
