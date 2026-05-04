# MCP and Plugin Architecture

## Principle
Do not start by exposing arbitrary MCP tools. Build an internal MCP-inspired protocol first, with strict policy and Android UI affordances.

## Internal plugin concept

A plugin can register:

- tools
- skills
- hooks
- subagents
- model runtimes
- context providers
- UI panels

## Plugin manifest

```json
{
  "id": "termux-plugin",
  "name": "Termux Execution Node",
  "tools": ["termux.exec", "termux.process.output", "termux.process.stop"],
  "permissions": ["run_command_intent"],
  "risk": "high"
}
```

## Required built-in plugins

- CoreFSPlugin
- TermuxPlugin
- GitPlugin
- ProjectPlugin
- SessionPlugin
- ModelRuntimePlugin
- RepoIndexPlugin
- SkillsPlugin
- HooksPlugin
- BrowserPreviewPlugin

## MCP bridge later

When adding MCP:

- default disabled
- per-server approval
- per-tool policy
- no untrusted stdio command execution without explicit allowlist
- tool descriptions are untrusted
- MCP output is untrusted observation
- network/server lifecycle visible in UI

## Plugin security

- signed/bundled plugins preferred
- no dynamic untrusted code loading in early versions
- external plugin marketplace explicitly out of scope until security review
