# Skill: Subagent Director

Use when a side task would flood main context.

Rules:
- ExploreAgent is read-only.
- SecurityAgent can inspect policy but not execute.
- TestAgent can run tests under policy.
- Subagents return compact summaries with citations to files/logs.
