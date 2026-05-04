# Hook: AfterFileEdit Format/Test

Event: AfterFileEdit

Rules:
- If Python file changed: propose `python -m py_compile <file>`.
- If JS/TS project and package scripts exist: propose relevant `npm test`/`npm run build`.
- If Rust file changed: propose `cargo check`.
- Never auto-run package install.
