# Onboarding and Termux Setup Flow

## First-run wizard

1. Explain app purpose: agent UI + Termux hands.
2. Check Termux installed.
3. Check RUN_COMMAND permission.
4. Guide user to grant permission.
5. Check `allow-external-apps=true`.
6. Offer bootstrap script.
7. Run diagnostic command.
8. Create default workspace.
9. Import/download local model or configure fallback.
10. Create first project.

## Setup states

- READY
- TERMUX_MISSING
- RUN_COMMAND_PERMISSION_MISSING
- ALLOW_EXTERNAL_APPS_MISSING
- TERMUX_STORAGE_RESTRICTED
- BOOTSTRAP_REQUIRED
- DIAGNOSTIC_FAILED
- MODEL_RUNTIME_MISSING

## User-facing diagnostics

Each state must show:

- what is wrong
- why it matters
- exact fix
- retry button
- copy command button where useful

## Never do

- silently fail
- tell user to manually debug Termux without exact reason
- run bootstrap without showing command/risk
