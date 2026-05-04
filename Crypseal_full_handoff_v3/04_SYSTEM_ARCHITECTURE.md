# System Architecture

## High-level diagram

```text
Android App Process
  ├─ Compose UI
  ├─ Crypseal Gateway
  │   ├─ Session Manager
  │   ├─ Project Manager
  │   ├─ Agent Orchestrator
  │   ├─ Tool Registry
  │   ├─ Approval Engine
  │   ├─ Event Bus
  │   ├─ Model Router
  │   ├─ Context Builder
  │   ├─ Repo Indexer
  │   ├─ Memory/Skills Loader
  │   └─ Persistence Layer
  │
  ├─ Model Runtime(s)
  │   ├─ LiteRT-LM Runtime
  │   ├─ Termux llama-server Runtime
  │   ├─ MNN/MLC/ExecuTorch Adapters later
  │   └─ Optional API Runtime later
  │
  └─ Termux Bridge
      ├─ RUN_COMMAND Intent Runner
      ├─ Result Receiver
      ├─ Process Monitor
      └─ Bootstrap Checker

Termux App Context
  ├─ ~/.termux/termux.properties
  ├─ ~/Crypseal/projects/<project>
  ├─ Python / Node / Git / Rust / package managers
  ├─ local servers
  └─ command stdout/stderr/results
```

## Main runtime flow

```text
User prompt
  → Session lane acquire
  → Context builder loads project state, repo map, memory, skills, recent observations
  → Model runtime generates message or tool call
  → Tool call parser validates schema
  → Approval engine classifies action
  → UI asks user if required
  → Tool executor runs action
  → Event bus streams result
  → Persistence layer records event
  → Model receives observation
  → Loop continues until done/interrupted/needs approval
```

## Core modules

### `gateway`
Owns the app's agent runtime. No UI logic. Emits events.

### `models`
Local and remote model runtime adapters. Produces structured `AgentAction` objects.

### `tools`
Typed tools. Tool implementations must never skip policy.

### `termux`
All Android-to-Termux details. No model logic.

### `policy`
Approvals, sandboxing, risk classification, command binding.

### `context`
Repo map, prompt context, compaction, memory, skills, subagent context.

### `storage`
Room DB, JSONL transcript, patch snapshots, project metadata.

### `ui`
Compose screens and components. UI observes gateway events.

## Event-driven contract

All major changes emit events:

- `SessionStarted`
- `ModelToken`
- `ToolCallProposed`
- `ApprovalRequested`
- `ApprovalResolved`
- `ToolStarted`
- `ToolOutputChunk`
- `ToolFinished`
- `PatchProposed`
- `PatchApplied`
- `CheckpointCreated`
- `CommandBound`
- `CommandDriftDetected`
- `ContextCompacted`
- `SessionInterrupted`
- `SessionFinished`

Events are persisted to JSONL and summarized into Room indexes.
