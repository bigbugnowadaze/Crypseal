# Prior Art: Reasoning and Agent Patterns

This document records the agent/reasoning prior art that should inform Crypseal.

## ReAct

ReAct interleaves reasoning traces and actions so an agent can update plans based on observations. For Crypseal, the implementation should be: visible concise plan + tool call + observation + updated plan. Do not make the raw reasoning trace the product.

## Self-Refine

Self-Refine shows that LLM outputs can improve through generate -> feedback -> refine loops without extra training. Crypseal should use self-refine for copy, design, patches, and explanations before presenting final results.

## Tree of Thoughts

Tree of Thoughts supports exploring multiple possible paths for tasks requiring planning/search. Crypseal should use this only in Deep Review Mode or high-stakes architecture/design decisions, not every turn.

## SWE-agent ACI

SWE-agent demonstrates that custom Agent-Computer Interfaces significantly affect software-agent performance. Crypseal must therefore design LM-native commands and feedback formats instead of dumping raw terminal output only.

## Aider repo map

Aider's repo map injects key files/symbols into context to help coding changes. Crypseal should implement repo mapping with tree-sitter/LSP where practical and mobile-safe token budgets.

## OpenHands

OpenHands emphasizes event-driven architecture, sandboxed execution, user-visible files/commands/browser state, and composable agent infrastructure. Crypseal should use an append-only event log and clear Workspace/Action/Observation separation.

## Cline Plan/Act

Cline popularized visible Plan/Act UX with file edits, terminal commands, browser use, and user approval. Crypseal should make Plan/Act native to Android approval cards.

## Claude Code public docs

Claude Code public docs establish skills, subagents, hooks, permission modes, protected paths, plan mode, and tool approval patterns as a modern coding-agent minimum. Crypseal should implement clean-room equivalents.

## Reasoning faithfulness caution

Recent papers caution that visible chain-of-thought is not guaranteed to be a faithful explanation of model reasoning. Therefore Crypseal should audit actions through evidence, tool outputs, tests, and structured summaries rather than raw CoT.
