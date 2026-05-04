# Termux Execution Node Spec

## Purpose
Termux is the execution node: it runs real commands in a Linux-like Android environment. The Android app remains the policy/UI/control plane.

## Setup requirements

1. Termux installed.
2. App declares `com.termux.permission.RUN_COMMAND`.
3. User grants that permission in Android settings.
4. Termux `~/.termux/termux.properties` contains:

```properties
allow-external-apps=true
```

5. Target SDK package visibility is configured so the app can resolve Termux.

## Termux bootstrap script

See `scripts/termux_bootstrap_full.sh`.

Installs:
- python
- nodejs-lts
- git
- ripgrep
- fd
- jq
- tree
- diffutils
- patch
- openssh
- clang/make/cmake optional
- rust optional

## Command execution schema

```json
{
  "tool": "termux.exec",
  "args": {
    "command": "python main.py",
    "workdir": "~/Crypseal/projects/demo",
    "timeoutSeconds": 120,
    "pty": false,
    "background": false,
    "stdin": null,
    "env": {},
    "reason": "Run the generated script and capture errors."
  }
}
```

## Required execution modes

### Foreground
Runs command, streams output, returns exit code.

### Background process
Starts long-running command, returns process ID, allows output polling and stop.

### PTY
Needed for commands that behave differently under terminal.

### Monitor
Long-running process whose output is watched and fed back into the event stream.

## Command binding

Before executing an approved command, bind:

- canonical workdir
- parsed argv
- resolved executable path
- environment overrides
- script/interpreter file hash if applicable
- project root
- approval mode
- timestamp

If command, workdir, executable, or bound file changes after approval, deny and re-ask.

## Strict inline eval

Always ask for:

- `python -c`
- `node -e`
- `ruby -e`
- `perl -e`
- `php -r`
- `lua -e`
- `sh -c`
- `bash -c`
- curl/wget piped to shell

Prefer writing a visible script file, diffing it, then running it.

## Path rules

Default allowed:
- project root
- app-created temp directory
- Termux workspace under `~/Crypseal`

Default protected:
- `.git/`
- `.Crypseal/`
- `.termux/`
- `.ssh/`
- `.env*`
- shell profiles
- `/sdcard`, `/storage/emulated/0` unless explicitly granted

## Output handling

The result object must include:

- command ID
- startedAt/finishedAt
- cwd
- canonical argv
- stdout chunks
- stderr chunks
- exit code
- timeout flag
- killed flag
- truncated flag
- output digest

Large outputs must be saved to log file and summarized.
