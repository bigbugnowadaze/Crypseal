# Design and Human Copy System

Crypseal must not feel like a Termux wrapper. It must feel like a premium mobile AI workbench: calm, confident, legible, and human.

## Why this is required

The user explicitly values Claude because it is good at design judgment and human copy. Therefore Crypseal must include product taste as a first-class subsystem, not a late polish pass.

## User-facing language goals

Crypseal copy must be:
- concrete, not vague
- calm under failure
- short on cards, richer in expandable explanations
- honest about risk and uncertainty
- human without being cutesy
- mobile-readable
- action-oriented

## Required copy surfaces

1. First-run onboarding
2. Termux setup instructions
3. Permission explanations
4. Command approval cards
5. File diff summaries
6. Error explanations
7. Test result summaries
8. Session resume cards
9. Git commit summaries
10. Release notes
11. App Store listing
12. Empty states
13. Warning states
14. Privacy/trust screens
15. Skill descriptions
16. Subagent descriptions
17. Model capability disclaimers

## Required design surfaces

1. Chat screen
2. Project switcher
3. Thread list
4. Tool/event stream
5. Approval card
6. Diff viewer
7. Command output viewer
8. Local server preview launcher
9. Model/runtime settings
10. Termux setup wizard
11. Permissions screen
12. Agent/team screen
13. Skills library screen
14. Logs/snapshots/revert screen
15. Context/memory screen

## Design system principles

- Mobile-first: thumb-safe primary actions, readable logs, collapsible details.
- Transparent agency: every tool action appears as a visible card.
- Calm danger: risky commands are clear but not panic-inducing.
- Reversible work: diffs/checkpoints are prominent.
- Human explanation: after every run, show what happened and why it matters.
- Trust by default: no hidden shell execution.

## Required UI copy pattern

Every tool card has:

```text
Title: short action phrase
Intent: one sentence reason
Risk: low/medium/high
Scope: project path or resource touched
Primary action: Run / Apply / Approve / Open
Secondary actions: Edit / Deny / Explain / Always allow
Expandable details: raw command, stdout/stderr, policy decision
```

## Example approval copy

Bad:

```text
Execute command?
```

Good:

```text
Run the generated Python script?

Crypseal wants to run:
python main.py

Why: check whether the generated script works before making more changes.
Scope: current project only.
Risk: low — runs local project code.
```

## Required copy review loop

Every user-visible string must pass:
- clarity check
- risk/trust check
- mobile length check
- human tone check
- localization readiness check
