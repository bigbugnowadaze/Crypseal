# No-Guesswork Research Protocol

Before implementing any subsystem, Antigravity must run this mini-protocol:

1. Identify subsystem.
2. Open relevant source files/docs from `01_SOURCE_REGISTRY_AND_PRIOR_ART.md`.
3. Confirm current API names, version constraints, and license.
4. Record decision in `BUILD_LOG.md` and ADR if architectural.
5. Implement smallest production-connected slice.
6. Add tests proving behavior.
7. Add failure modes and diagnostics.

## What counts as guesswork

- Implementing a Termux Intent without checking RUN_COMMAND extras/permissions.
- Adding a model runtime without confirming Android SDK shape.
- Letting shell commands run before policy exists.
- Adding dependencies without license review.
- Assuming Android background execution works without foreground service constraints.
- Copying UX patterns without understanding approval/security implications.

## Allowed exploratory work

Exploration is allowed only as a named research task with output:

- source consulted
- result
- decision
- blocker if any
- production impact

Exploration must not become the app architecture.
