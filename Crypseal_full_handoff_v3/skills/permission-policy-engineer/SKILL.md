# Skill: Permission Policy Engineer

Use when editing approvals, risk classes, or sandbox rules.

Rules:
- Deny beats ask, ask beats allow.
- Protected paths never auto-approve.
- Inline eval always asks.
- Package/network commands ask unless explicit policy.
- Approval binds cwd, argv, executable path, env, and script hash when possible.

Verification:
- Dangerous patterns blocked.
- Drift test denies changed script.
- Permission UI accurately reflects policy.
