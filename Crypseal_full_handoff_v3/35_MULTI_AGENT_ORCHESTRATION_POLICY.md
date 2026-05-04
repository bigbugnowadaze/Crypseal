# Multi-Agent Orchestration Policy

## Purpose

Crypseal must route work to specialist agents without creating chaos, duplicated effort, or context bloat.

## Orchestration model

Use a main OrchestratorAgent with session ownership. Specialists receive scoped tasks and return compact artifacts.

```text
User request
  -> Orchestrator
  -> select primary agent
  -> optional research/design/security/copy reviewers
  -> merge results
  -> propose plan/tool calls
  -> execute with approvals
  -> summarize verified outcome
```

## Subagent output contract

Every subagent returns:

```json
{
  "agent": "DesignCriticAgent",
  "task": "Review command approval card UX",
  "findings": [],
  "recommendations": [],
  "blocking_issues": [],
  "files_or_surfaces": [],
  "confidence": "low|medium|high"
}
```

## When to spawn subagents

Spawn when:
- task spans multiple domains
- main context would bloat
- design/copy quality matters
- security/permissions are involved
- prior art must be checked
- release quality matters

Do not spawn when:
- trivial command output explanation
- one-line file edit
- simple user clarification

## Maximum parallelism

Phone constraints matter. Default: one primary + two reviewers. Deep Review Mode may use up to five reviewers.

## Required reviewer pairings

- New UI surface: UXArchitect + HumanCopywriter + AccessibilityAgent.
- New tool/permission: SecurityAgent + ToolUseReviewer + ErrorUXCopyAgent.
- New local model/runtime feature: LocalModelRuntimeAgent + EvalHarnessAgent + PerformanceThermalAgent.
- New roadmap/architecture decision: PriorArtLibrarian + ProductDirector + RoadmapStrategist.
