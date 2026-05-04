# Agent Loop and Orchestration

## Loop design

Crypseal uses a controlled ReAct-style observe/think/act loop, but hidden reasoning is not shown as raw chain-of-thought. The model receives structured observations and returns either a message, a plan, or a tool call.

```text
Prompt
→ assemble context
→ model step
→ validate tool call/message
→ policy classify
→ approval if needed
→ execute tool
→ persist observation
→ update context
→ continue or stop
```

## Session lanes

Each project/thread has one active lane. Tool actions are serialized per lane.

User input during active run can:

- queue
- interrupt
- steer current run
- deny pending approval
- approve pending approval

## Run states

```text
Idle
Planning
WaitingForApproval
ExecutingTool
StreamingOutput
CompactingContext
Interrupted
Failed
Completed
```

## Planning

Plan mode may use read/search/status tools only. It produces a plan card with:

- goal
- assumptions
- files likely touched
- tools required
- commands likely run
- risks
- verification strategy
- rollback strategy

User choices:

- approve auto-safe
- approve with manual edits
- keep planning
- edit plan
- cancel

## Tool loop policy

- One tool call per model step initially.
- Parallel tool calls allowed only for read-only tools after v1.
- Every tool call must include a reason.
- Tool observations must be compact and line-numbered when relevant.
- After command failure, agent must inspect output before proposing fix.
- After user edits a diff manually, agent must reread file.

## Stop conditions

- task done
- needs user info
- approval denied
- max autonomous steps reached
- repeated failure loop detected
- command risk escalated
- context too degraded; compact then ask/continue

## Failure-loop detector

Track repeated:

- same command failing with same digest
- same patch rejected
- same package install failure
- same missing dependency after install
- same file conflict

On loop detection, agent must summarize and propose alternatives.
