# Skill: Android Compose Architecture

Use when implementing Android UI or navigation.

Rules:
- Use unidirectional data flow.
- Compose functions render state and emit events only.
- ViewModels hold screen state and call repositories/gateway.
- Do not execute tools from UI components.
- Every long-running run must surface foreground-service notification state.

Verification:
- Rotation/config changes preserve active session UI.
- Tool output continues streaming after UI recreation.
