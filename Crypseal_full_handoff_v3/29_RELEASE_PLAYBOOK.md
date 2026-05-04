# Release Playbook

## Pre-release checklist

- All P0 acceptance tests pass.
- Golden paths A-G pass on target device.
- Security tests pass.
- Termux setup flow tested from clean device.
- Local model runtime documented.
- Logs redacted.
- Export/import tested.
- Dependencies/license registry current.
- No contaminated/leaked code.
- No secret values in repo.

## Build artifacts

- APK/AAB
- source zip
- docs zip
- test report
- threat model
- license report
- release notes

## Release channels

- internal APK sideload
- private testing track
- later public distribution if desired

## Versioning

Use semantic versioning:

- 0.x internal full-app development builds
- 1.0 when full-app definition of done is met

## Crash/log policy

If adding crash analytics later, cloud reporting must be opt-in and redact command/file contents by default.
