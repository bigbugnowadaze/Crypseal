# Test, QA, Evaluation, and Security Matrix

## Unit tests

- PathSandbox rejects traversal/outside root.
- CommandClassifier labels dangerous patterns.
- ApprovalBinding detects cwd/argv/file hash drift.
- Patch parser applies and reverts hunks.
- JSON tool parser rejects invalid/malformed fields.
- Context compactor keeps required state.
- Repo scanner respects ignore rules.

## Integration tests

- Termux setup checker detects missing Termux.
- RUN_COMMAND dry-run works.
- Python project create/run/fix loop.
- Node webapp create/run/preview loop.
- Git status/diff/commit loop.
- Long-running local server monitor.
- Permission denial stops run cleanly.
- Interrupt active run.
- Resume/fork session.

## Device tests

- Samsung S21 FE class phone.
- Low RAM device.
- Recent Pixel with NPU/GPU acceleration.
- Android 13/14/15/16 target coverage where possible.
- App backgrounded during active run.
- Battery optimization on/off.
- Termux with/without storage permission.

## Security tests

- Prompt injection inside README tries to override policy.
- Repo script tries to read `.ssh`.
- Package script tries external network.
- `curl | sh` blocked.
- `rm -rf` blocked.
- Inline eval requires approval.
- `.env` redacted.
- Approval drift detected after file changes.
- Malformed tool call cannot run.

## Agent quality evals

- Can inspect unfamiliar repo and produce file map.
- Can fix syntax error after test output.
- Can install missing dependency with approval.
- Can avoid repeating same failed command.
- Can summarize session after compaction.
- Can use skill when task matches.
- Can ask user only when needed.

## Release gates

No release until:

- all high-risk command tests pass
- Termux setup flow reliable
- file snapshot/revert works
- no known secret leakage in logs
- permissions screen accurately reflects policy
- crash reporting/log export works without leaking secrets
