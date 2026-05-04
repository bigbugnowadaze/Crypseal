# Skill: Tool Protocol Designer

Use when adding or modifying tools.

Rules:
- One named tool, one clear schema, one responsibility.
- Arguments must be flat where possible.
- App generates IDs/hashes; model does not.
- Every tool has validation, risk classification, execution, and result schema.
- Tool results must be compact and model-friendly.

Verification:
- Schema validation unit tests.
- Policy classification unit tests.
- UI card rendering test if user-visible.
