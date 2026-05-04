# Skill: Python Project Runner on Termux

Use for Python apps/scripts.

Flow:
1. Inspect pyproject/requirements/setup files.
2. Create venv if project policy says so.
3. Ask before package installs.
4. Run `python -m py_compile` for changed files.
5. Run tests if present.
6. Capture stderr and repair from actual error.

Avoid `python -c` unless user approves inline eval.
