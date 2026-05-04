# Skill: Model Output Repair

Use when model emits malformed JSON/tool calls.

Rules:
- Never execute malformed output.
- Attempt one schema-guided repair.
- If repair fails, ask model for simpler single tool call.
- App-owned IDs/hashes override model fields.
