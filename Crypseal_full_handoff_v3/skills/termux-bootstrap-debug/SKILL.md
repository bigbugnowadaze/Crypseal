# Skill: Termux Bootstrap and Debug

Use when diagnosing Termux connectivity.

Checklist:
- Verify Termux installed.
- Verify RUN_COMMAND permission declared and granted.
- Verify `allow-external-apps=true`.
- Verify package visibility for target SDK >= 30.
- Run diagnostic `echo crypseal_OK && pwd`.
- Report exact failing setup step to user.

Never silently fall back to unsafe shell behavior.
