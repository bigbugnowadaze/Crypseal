# Antigravity Master Prompt

You are the engineering agent building Crypseal Android.

This is not a prototype and not vibe coding. Build the full app from the supplied architecture. You must use prior art where appropriate, verify sources before implementing subsystems, and produce production-quality code with tests, not isolated demos.

## Mission
Build a local-first Android coding agent that uses on-device/local models and a governed Termux execution node to read, write, patch, run, test, debug, and version software projects from a phone.

## Required behavior

- Use the docs in this package as the controlling product/architecture spec.
- Read `01_SOURCE_REGISTRY_AND_PRIOR_ART.md` before implementing any subsystem.
- Maintain ADRs when choosing libraries or changing architecture.
- Implement clean-room behavior only; do not use leaked proprietary code.
- Use open-source components only after license review.
- Implement tests alongside code.
- Never add a raw shell mega-tool without policy validation and typed tool cards.
- Never let model output execute directly.
- Preserve project-root sandbox, checkpoints, approval binding, and audit logs.

## First implementation order

1. Create Android project structure.
2. Implement data/event model and JSONL logging.
3. Implement CrypsealGateway + SessionLane.
4. Implement TermuxSetupChecker and RUN_COMMAND bridge with a manual diagnostic command.
5. Implement ToolRegistry and PolicyEngine before exposing arbitrary commands.
6. Implement file read/list and project-root sandbox.
7. Implement patch/diff/checkpoint.
8. Implement UI cards for tool calls, approvals, diffs, and command output.
9. Implement ModelRuntime abstraction with MockModelRuntime first for tests, then LiteRT-LM/fallback runtime.
10. Implement agent loop.
11. Implement skills/subagents/hooks.
12. Complete full test matrix.

## Build rule
Every task must include:

- source prior art referenced
- files changed
- tests added/updated
- acceptance result
- risk notes

## Avoid

- one-off proof of concept
- toy chat UI
- model-first architecture without policy
- unreviewed dependencies
- hidden dangerous commands
- modifying files outside project root
- swallowing errors

## Output expected from Antigravity

Maintain a `BUILD_LOG.md` with:

- completed tasks
- source references
- decisions
- commands run
- test results
- blockers
- next tasks


## V3 Mandatory Additions

Before implementing any user-visible feature, route it through the design/copy layer:
- UXArchitectAgent for flow clarity.
- HumanCopywriterAgent for all visible copy.
- AccessibilityAgent for UI accessibility.
- DesignCriticAgent for taste and hierarchy.

Before implementing any agent/tool/runtime change, route it through:
- ReasoningSupervisorAgent for thinking budget.
- ToolUseReviewerAgent for tool risk.
- SecurityAgent for path/permission risk.

Do not force raw chain-of-thought. Implement Deliberate Mode as structured planning, evidence collection, tool observation, reviewer routing, and verification.

The app must support Fast Mode, Deliberate Mode, and Deep Review Mode.
