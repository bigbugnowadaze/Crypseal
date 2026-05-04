# Target Codebase Layout

```text
crypseal-android/
  app/
    build.gradle.kts
    src/main/
      AndroidManifest.xml
      java/com/harrowhaus/crypseal/app/
      res/
  crypseal-runtime/
    src/main/java/com/harrowhaus/crypseal/runtime/
  crypseal-shell-bridge/
    src/main/java/com/harrowhaus/crypseal/shellbridge/
  crypseal-guard/
    src/main/java/com/harrowhaus/crypseal/guard/
  ui/
    src/main/java/com/harrowhaus/crypseal/ui/
  docs/
    adr/
    sources/
    security/
  scripts/
    termux_bootstrap_full.sh
  tests/
    golden_paths/
  .crypseal-dev/
    skills/
    hooks/
    subagents/
  BUILD_LOG.md
  LICENSES.md
  README.md
```

## Rule
Production code should mirror architecture. If a module does not fit, update ADR before creating random folders.
