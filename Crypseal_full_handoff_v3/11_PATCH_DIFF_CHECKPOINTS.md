# Patch, Diff, and Checkpoint System

## Goal
Editing must feel professional and safe: visible diffs, reversible changes, conflict detection, and no accidental overwrite of user edits.

## File operation hierarchy

Preferred:
1. `file.patch` with structured hunks.
2. `file.edit` with exact old/new text and hash guard.
3. `file.write` only for new files or explicit full overwrite.

## Patch workflow

```text
model proposes patch
→ validate paths
→ check file hashes
→ create checkpoint
→ render diff card
→ approval decision
→ apply patch
→ verify file hash after apply
→ optionally run formatter/test
→ persist patch event
```

## Checkpoint contents

For every edit:

- original file content or compressed snapshot
- new file content hash
- patch text
- model reason
- approval mode
- timestamp
- git state before edit

## Conflict detection

If a file changed after it was read or after patch generation, do not apply blindly. Ask model to reread or perform three-way merge.

## Diff UI

Phone-friendly cards:

- changed file list
- expandable file diff
- line numbers
- syntax highlighting where possible
- apply / reject / edit patch / apply + test

## Revert

Minimum:

- revert last patch
- revert selected file to checkpoint
- revert session to checkpoint

Advanced:

- fork from checkpoint
- generate reverse patch
- export patch bundle

## Implementation options

- Kotlin patch parser for unified diffs.
- Use `git apply --check` through Termux as a verifier when inside git repo.
- Keep app-owned snapshot regardless of git.
- Do not require git for checkpoints.
