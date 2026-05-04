# Production Roadmap — Full App, Not Prototype

This roadmap is staged for engineering reality, but every milestone builds the final architecture. Do not ship a dead-end demo.

## Track 0 — Source verification and project foundation

Deliverables:
- Android project scaffold
- package structure
- license registry
- source review notes
- ADRs initialized
- CI/unit test skeleton

Acceptance:
- repo builds on Android Studio
- source registry mirrored in `/docs/sources`
- license risks documented

## Track 1 — Crypseal Gateway core

Deliverables:
- event bus
- session manager
- project manager
- session lanes
- JSONL logging
- Room indexes

Acceptance:
- can create/open project and append session events
- lane serialization tested
- session resume/fork basic

## Track 2 — Termux execution node

Deliverables:
- setup checker
- RUN_COMMAND bridge
- command result receiver
- foreground/background command support
- stdout/stderr capture
- bootstrap script

Acceptance:
- `python --version` runs from app
- command output streams to UI
- missing permission states diagnosed

## Track 3 — Tool registry and policy engine

Deliverables:
- typed tool protocol
- tool parser/validator
- path sandbox
- command classifier
- approval engine
- protected paths
- approval binding/drift checks

Acceptance:
- safe commands classify correctly
- dangerous commands blocked
- approvals bind and detect drift

## Track 4 — File tools, patch/diff/checkpoints

Deliverables:
- file read/list/glob/grep
- patch parser/apply/check
- diff UI
- snapshots
- revert

Acceptance:
- multi-file patch applies with checkpoint
- user can reject/edit/apply patch
- revert restores files

## Track 5 — Local model runtime

Deliverables:
- ModelRuntime interface
- LiteRT-LM integration research/build
- mock runtime for testing
- Termux llama-server fallback adapter
- model health UI
- tool-call JSON repair/retry

Acceptance:
- local model or fallback runtime can propose valid tool call
- malformed model output does not execute
- runtime can be swapped

## Track 6 — Agent orchestrator

Deliverables:
- plan mode
- act loop
- tool observation loop
- failure loop detector
- max step limits
- interrupt/steer
- compaction integration

Acceptance:
- agent can create/run/fix small Python and web projects with approvals
- repeated failure loop detected and summarized

## Track 7 — Repo context/indexing

Deliverables:
- project scanner
- ignore rules
- repo map
- search tools
- language detectors
- tree-sitter/LSP plan

Acceptance:
- app can summarize unknown repo structure
- model receives compact repo map and relevant excerpts

## Track 8 — Skills/subagents/hooks/slash commands

Deliverables:
- bundled skill library
- project skills override
- subagent definitions
- hook engine
- command palette

Acceptance:
- skills load by task
- PreCommand hook blocks dangerous command
- ExploreAgent summarizes repo without editing

## Track 9 — Full UI polish

Deliverables:
- project/thread UI
- tool cards
- approval cards
- diff cards
- command logs
- git UI
- policy settings
- runtime settings
- notifications

Acceptance:
- phone workflow can be used without manually reading Termux
- user can approve/deny/intervene at every critical point

## Track 10 — Git/release/project lifecycle

Deliverables:
- git status/diff/add/commit tools
- project templates
- export session/project/patch
- local server preview
- release build workflows

Acceptance:
- agent can create commit after successful run
- user can export complete project/session

## Track 11 — Security, QA, performance

Deliverables:
- test matrix complete
- security scenario tests
- thermal/memory monitoring
- log redaction
- crash recovery
- data export/import

Acceptance:
- all release gates in `18_TEST_QA_EVAL_SECURITY_MATRIX.md` pass.

## Track 12 — Optional advanced adapters

Deliverables:
- MNN/MLC/ExecuTorch adapters as warranted
- MCP bridge
- embedded userland sandbox
- remote node support

Acceptance:
- adapters can be added without changing agent core.
