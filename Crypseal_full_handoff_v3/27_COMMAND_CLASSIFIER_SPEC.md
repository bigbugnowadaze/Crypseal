# Command Classifier Spec

## Pipeline

1. Normalize command.
2. Parse shell structure if possible.
3. Identify executable(s).
4. Resolve executable path when possible.
5. Detect chaining/pipes/redirection.
6. Detect inline eval.
7. Detect package/network/destructive/secret/external path behavior.
8. Apply deny/ask/allow rules.
9. Build approval binding if permitted.

## Categories

- read/status
- test/build
- package install
- network fetch
- local server
- file mutation
- git mutation
- destructive
- secret access
- external storage
- inline eval
- unknown

## Heuristics

Ask for:
- package managers
- network fetchers
- local server starts
- git commits
- inline eval
- commands with `&&`, `||`, `;`, pipes, or redirections unless all segments safe

Block:
- known destructive patterns
- protected path reads/writes
- unexplained external storage writes
- hidden shell execution after network fetch

## Output

```json
{
  "risk": "MEDIUM_PACKAGE_INSTALL",
  "decision": "ASK",
  "reasons": ["npm install may run package scripts and network"],
  "bindingRequired": true
}
```
