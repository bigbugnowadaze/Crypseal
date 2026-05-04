# Reasoning Mode Assessment — Do Not Force Raw Chain-of-Thought Everywhere

## Decision

Crypseal should not force every model to expose raw chain-of-thought. It should force **deliberate structured reasoning behavior** through plans, checklists, evidence, tool observations, self-review, and verification gates.

## Why

Research and production practice increasingly distinguish true latent reasoning from surface chain-of-thought text. Recent work argues that LLM reasoning should be studied as latent-state trajectory formation rather than assuming visible CoT is a faithful representation of reasoning. Other work on reasoning models reports that chain-of-thought can be unfaithful or post-hoc. Therefore, visible CoT should not be treated as a reliable audit log.

## Required alternative

Use a four-layer reasoning architecture:

### 1. Private deliberation layer
Where a model/runtime supports internal reasoning or thinking budgets, allow it. Do not require exposing raw hidden thoughts to the user.

### 2. Structured visible plan layer
Expose concise, auditable plans:

```json
{
  "goal": "Run the app and fix failing startup errors",
  "known_facts": ["package.json exists", "npm install has not run"],
  "assumptions": ["Node project uses package scripts"],
  "next_actions": ["read package.json", "run npm install if dependencies are missing"],
  "stop_conditions": ["tests pass", "blocked by missing permission"]
}
```

### 3. Tool-observation layer
All actual work is grounded in tool results: file reads, grep results, command output, tests, compiler diagnostics, git diffs.

### 4. Verification/refinement layer
Use Self-Refine-style critique loops, ReAct-style reason/action/observation loops, and Tree-of-Thoughts-style branching only for high-impact decisions.

## Thinking budget policy

Do not use the same reasoning level for every action.

| Task type | Thinking mode |
|---|---|
| Greeting / simple question | none / low |
| Explain command output | low |
| Write small patch | medium |
| Multi-file refactor | high |
| Security-sensitive command | high + ToolUseReviewer |
| Product/design decision | high + DesignCritic/HumanCopywriter |
| Roadmap/architecture decision | high + PriorArtLibrarian |
| Release decision | high + QA/Security/Release reviewers |

## User-visible explanation policy

Show:
- plan summary
- evidence used
- command/file changes
- tradeoffs
- what was verified
- what remains uncertain

Do not show:
- raw hidden chain-of-thought
- long speculative internal monologues
- unverifiable reasoning traces as if they were proof

## Implementation requirement

The model output schema must include:

```json
{
  "mode": "plan|act|review|explain",
  "reasoning_budget": "none|low|medium|high|deep",
  "visible_reasoning_summary": "short explanation for user",
  "evidence": [],
  "assumptions": [],
  "tool_calls": []
}
```

## Product wording

Call this **Deliberate Mode**, not “show chain of thought.”

Modes:
- Fast Mode: quick chat and simple actions.
- Deliberate Mode: plans, checks evidence, may ask reviewers.
- Deep Review Mode: multi-agent review before major changes.

## Non-negotiable

If the app claims it is thinking deeply, it must produce better behavior: more evidence, better plans, more verification, and fewer reckless commands. It must not merely print longer reasoning.
