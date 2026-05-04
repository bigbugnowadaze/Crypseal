# Gradle Dependencies and License Notes

Antigravity must verify current versions before adding. This is a planning list, not pinned truth.

## Likely Android dependencies

- AndroidX Core KTX
- Jetpack Compose BOM
- Material 3
- Navigation Compose
- Lifecycle ViewModel Compose
- Coroutines Android
- Room Runtime/KTX/Compiler
- DataStore Preferences
- Kotlin serialization or Moshi
- OkHttp for localhost runtime adapters

## Potential utilities

- Diff library or custom unified diff parser
- JGit only if CLI git is insufficient
- Tree-sitter binding later; otherwise Termux CLI/parser path

## License policy

Preferred:
- Apache-2.0
- MIT
- BSD

Requires explicit review:
- GPL
- LGPL
- AGPL
- unknown license

Never import:
- leaked proprietary code
- repos claiming leaked derivation
- code without clear license unless rewritten clean-room
