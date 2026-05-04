# Open Source Component Matrix

| Component | Candidate | License posture | Use | Notes |
|---|---|---:|---|---|
| Android local model showcase | Google AI Edge Gallery | check repo; public OSS | reference/integration patterns | Do not turn into product UI. |
| Native model runtime | LiteRT-LM | check SDK license | primary runtime | Function calling and Android support. |
| Termux bridge | Termux RUN_COMMAND | Termux OSS | execution node | Requires user permission + allow-external-apps. |
| Agent architecture | OpenClaw | OSS | architecture reference | Use concepts: gateway/tools/approvals. |
| Terminal agent security | Codex CLI docs | public docs/OSS repo | sandbox/approval reference | Workspace boundary logic. |
| Plan/Act UX | Cline | OSS/public docs | permission UX | Plan before act. |
| Repo map | Aider | OSS/public docs | context strategy | Symbol map and concise repo overview. |
| ACI | SWE-agent | OSS/research | tool feedback design | LM-centric commands/outputs. |
| Agent platform | OpenHands | MIT/open source | sandbox/runtime inspiration | Do not overbuild cloud platform. |
| Parser | Tree-sitter | MIT | repo indexing | Use later for symbol map. |
| Git library | JGit or Termux git | EDL/BSD or CLI | git operations | CLI first; app parse output. |
