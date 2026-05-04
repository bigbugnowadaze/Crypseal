# Premium Assistant Quality Parity Targets

Crypseal is not trying to copy Claude internals. It is trying to meet the observable quality bar users expect from premium AI tools.

## Quality dimensions

### 1. Design taste
- UI feels intentional, not generated.
- Every risky action has clear visual hierarchy.
- Complex logs collapse into human summaries.
- Empty states explain what to do next.

### 2. Human copy
- No robotic filler.
- No fake certainty.
- Good microcopy for warnings, errors, setup, and onboarding.
- Explanations are conversational but precise.

### 3. Tool confidence
- Agent explains why a command is needed before running it.
- Agent reads output before guessing.
- Agent verifies changes with tests/linters where possible.

### 4. Recovery
- Every edit can be inspected and reverted.
- Failed commands produce next-step cards.
- Setup problems are diagnosed, not blamed on the user.

### 5. Product continuity
- Sessions resume cleanly.
- Projects remember context.
- Skills and instructions persist.
- Compaction preserves decisions and TODOs.

## Required review gates

A feature is not shippable until:
- ImplementAgent says it works.
- TestAgent verifies it.
- UXArchitect says the flow is understandable.
- HumanCopywriter approves user-facing copy.
- SecurityAgent approves permissions/risk if tools are involved.
