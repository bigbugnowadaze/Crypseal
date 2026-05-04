# Feature Requirements Matrix

| ID | Feature | Priority | Source prior art | Production requirement | Acceptance |
|---|---|---:|---|---|---|
| F001 | Project root workspace | P0 | Codex sandbox, OpenClaw gateway | Canonical root per project; no default external writes | PathSandbox tests pass |
| F002 | Session lanes | P0 | OpenClaw agent loop | One active run per session; queue/interrupt/steer | Race test cannot corrupt files |
| F003 | Termux bridge | P0 | Termux RUN_COMMAND | Executes approved commands and streams results | Diagnostic command works |
| F004 | Typed tools | P0 | MCP/OpenClaw/SWE-agent | All actions are schema-validated tool calls | Unknown/malformed tool rejected |
| F005 | Approval engine | P0 | OpenClaw/Codex/Cline | Allow/ask/deny, risk classes, UI cards | Dangerous commands blocked |
| F006 | Command binding | P0 | OpenClaw exec approvals | Bind cwd/argv/executable/hash before execution | Drift test re-asks |
| F007 | Patch/diff/checkpoint | P0 | Aider/Codex/Claude public behavior | Visible diffs and snapshots for edits | Revert last patch works |
| F008 | Local model runtime abstraction | P0 | LiteRT-LM/MNN/MLC/ExecuTorch | Runtime swappable behind interface | Mock + one real/fallback runtime works |
| F009 | Plan mode | P0 | Cline Plan/Act, Claude public docs | No edits/mutations in plan mode | Mutating tools blocked |
| F010 | Repo map | P0 | Aider repo map/SWE-agent ACI | Compact file/symbol/context overview | Unknown repo summarized |
| F011 | Tool output streaming | P0 | Codex/Cline/OpenClaw | stdout/stderr visible incrementally | Long command streams chunks |
| F012 | Background monitors | P1 | OpenClaw exec/process, Claude Monitor | Start/stop/watch local servers/logs | npm dev server can be stopped |
| F013 | Skills | P1 | Claude/OpenClaw skills | Bundled and project override skills | Skill selection visible |
| F014 | Hooks | P1 | Claude/OpenClaw hooks | Deterministic lifecycle policies | PreCommand block works |
| F015 | Subagents | P1 | Claude/OpenHands/SWE-agent | Separate contexts/tool limits | ExploreAgent cannot edit |
| F016 | Git operations | P1 | Codex/Aider | status/diff/add/commit only under policy | Commit after review |
| F017 | Context compaction | P1 | Claude/SWE-agent summarizers | Compact long sessions without losing current task | Resume after compaction |
| F018 | Runtime health | P1 | AI Edge Gallery benchmark UX | Show model/Termux/battery/thermal readiness | Health cards accurate |
| F019 | Prompt injection defense | P0 | Agent security prior art | File/log content cannot override policy | Injection test blocked |
| F020 | Export/import | P2 | Developer workflows | Export session/project/patch bundle | Zip exports replayable |
