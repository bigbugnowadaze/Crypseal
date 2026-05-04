# Skill: Command Output Streaming

Use for stdout/stderr streaming.

Rules:
- Stream chunks to UI and JSONL.
- Save full logs for large output.
- Summarize after command finishes.
- Preserve exit code and timeout/killed flags.
- Avoid blocking UI thread.
