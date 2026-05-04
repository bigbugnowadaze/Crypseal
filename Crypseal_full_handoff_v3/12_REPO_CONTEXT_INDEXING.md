# Repository Context and Indexing

## Why this matters
A local phone model will fail if handed random giant files. The app needs a structured codebase map inspired by Aider repo maps and SWE-agent ACI feedback.

## Context layers

### Layer 0: Project metadata
- root path
- language hints
- package files
- git status
- recent files
- AGENT.md

### Layer 1: Fast file map
- directory tree
- ignored files
- file sizes
- modified times
- binary/text detection

### Layer 2: Search index
- ripgrep/fd-backed content search
- filename search
- symbol-like regex fallback

### Layer 3: Repo map
- important files
- key classes/functions/types/signatures
- dependencies/imports
- entrypoints

### Layer 4: Tree-sitter/LSP
- AST symbol extraction
- references
- call graph fragments
- syntax errors

## Initial implementation

1. Build `ProjectScanner` for file tree and ignore rules.
2. Use Termux `rg` and `fd` if available for fast search.
3. Build simple language detectors from files: package.json, pyproject.toml, Cargo.toml, build.gradle, AndroidManifest.xml.
4. Implement `RepoMapBuilder` with regex symbol extraction first.
5. Add tree-sitter-backed parser after base app works.

## Context budget policy

For each model request include:

- current user request
- active task state
- project instructions
- small repo map
- relevant file excerpts only
- recent tool observations summarized
- current git status/diff summary

Never dump entire repo.

## Search subagent

Use `ExploreAgent` to search project and return compact findings, not raw full output.

## Output format for file reads

Include:

- path
- line range
- sha256
- excerpt with line numbers
- truncated flag

This makes model edits safer.
