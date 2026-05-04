# Model Thinking Budgets

## Objective

Use reasoning deliberately. Deep thinking costs latency, battery, heat, and context. On mobile, thinking must be allocated, not left permanently maxed out.

## Budget levels

### none
For UI acknowledgements, button labels, simple summaries.

### low
For short explanations, command output summaries, simple copy edits.

### medium
For small patches, basic debugging, local model routing, onboarding copy variants.

### high
For architecture changes, security-sensitive commands, UX flows, release decisions, multi-file changes.

### deep
For prior-art-driven roadmap changes, major system rewrites, threat model changes, and model/tool protocol changes.

## Required behavior by budget

| Budget | Required extra behavior |
|---|---|
| low | one-sentence rationale |
| medium | facts, assumptions, next action |
| high | alternatives considered, risks, verification plan |
| deep | prior art references, reviewer subagents, rollback plan |

## Anti-patterns

- Do not use deep mode for every message.
- Do not expose raw chain-of-thought as a UX feature.
- Do not let reasoning summaries replace tests.
- Do not let a local small model pretend certainty without evidence.

## Runtime note

For models with native thinking controls, map budget to runtime setting. For models without native controls, map budget to orchestration: more context gathering, reviewer subagents, test runs, and self-refine passes.
