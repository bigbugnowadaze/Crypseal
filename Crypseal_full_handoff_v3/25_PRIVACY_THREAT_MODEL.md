# Privacy and Threat Model

## Assets to protect

- User project files
- Android shared storage
- Termux home
- SSH keys and tokens
- `.env` files
- conversation logs
- model prompts/context
- generated code/artifacts

## Attack surfaces

- malicious repo files
- prompt injection in README/docs/logs
- package manager scripts
- shell command injection
- model hallucinations
- Termux permissions
- local HTTP servers
- exported logs/zips
- future MCP servers/plugins

## Security boundaries

- Android app sandbox
- Termux app sandbox
- project-root sandbox
- approval policy
- protected paths
- log redaction
- foreground service visibility

## Risk decisions

- Default no read of secret files.
- Default no external storage writes.
- Default no network commands unless user approves.
- Default no destructive commands.
- Default no arbitrary MCP.
- Default no cloud provider unless user enables and understands data flow.

## Logging policy

Logs should preserve enough to debug but redact:

- API keys
- SSH private keys
- tokens
- `.env` values
- authorization headers

Redaction must happen before writing JSONL.
