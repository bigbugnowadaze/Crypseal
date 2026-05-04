# Directive: This Is Not a Prototype Handoff

The goal is not to make a chat demo, a proof of concept, or a one-off Termux script.

The goal is to build the full Android app: a mobile coding agent that can create, inspect, modify, run, test, debug, package, and version projects from a phone using a governed Termux execution layer and local/on-device model runtime.

## Hard rules

1. No vibe coding.
2. No undocumented architecture leaps.
3. No single `run shell command` mega-tool as the permanent architecture.
4. No file overwrites without diff/checkpoint behavior.
5. No dangerous shell behavior without policy classification and approval.
6. No assuming model output is valid JSON or safe commands.
7. No relying on cloud models as the core identity of the product.
8. No leaking project secrets into prompts, logs, or model context.
9. No unlicensed or contaminated proprietary source code.
10. No dead-end MVPs; every implementation task must connect to the full architecture.

## The actual target

Crypseal should eventually feel like:

- Claude Code / Codex-style agent ergonomics
- OpenClaw-style gateway/tool/session architecture
- Aider-style repo awareness and edit discipline
- Cline-style Plan/Act UX and permission visibility
- Termux-native execution power
- Android-native local/offline control surface
- Google AI Edge / LiteRT-LM model runtime where possible

## Definition of done for the full app

The app is done when a user can:

1. Create or open a project on Android.
2. Give a natural-language build request.
3. See a plan before changes.
4. Approve, reject, or edit proposed actions.
5. Let the agent read/search/map the project.
6. Let the agent patch files with visible diffs.
7. Run tests/builds/scripts through Termux.
8. Watch command output stream back into chat.
9. Have the model inspect failures and repair code.
10. Use persistent skills, memory, hooks, slash commands, and subagents.
11. Resume/fork/rollback sessions.
12. Keep the entire process bounded by explicit policy and project sandbox.
13. Export logs, patches, project state, and release artifacts.
