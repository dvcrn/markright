# Repository Guidelines

## Project Structure & Module Organization
- ClojureScript sources live in `src/cljs` (e.g., `src/cljs/markright/main.cljs` for the Electron main process and `markright/ui.cljs` for the renderer). Shared build hooks are in `src/clj/shadow_hooks.clj`.
- Compiled output goes into `node/app.js` and `node/ui/js/` via `shadow-cljs`; the packaged UI (HTML/CSS/fonts) sits in `node/ui/`.
- Assets such as screenshots and the banner are under `resources/`. Packaging helpers (e.g., macOS post-build) live in `scripts/`.

## Build, Test, and Development Commands
- `bun install` — install dependencies (required before any build).
- `bun run release` — compile both Electron main (`:main`) and renderer (`:front`) targets via `shadow-cljs`; refreshes `node/app.js` and `node/ui/js/`.
- `bun start` — launch Electron using the compiled output; run `bun run release` once beforehand or keep `watch` running.
- `bun run watch` — continuous `shadow-cljs watch main front` for live recompilation during development; use alongside `bun start`.
- `bun run clean` — remove build artifacts (`dist`, `node/ui/js/`, `.shadow-cljs`, `target`) to unstick stale builds.

## Coding Style & Naming Conventions
- Favor idiomatic ClojureScript: 2-space indentation, `:require` with `:as` aliases, avoid `:use`, and use dot syntax for JS interop (`(.method js/object ...)`).
- Namespaces follow `markright.<area>` (e.g., `markright.components.markdown`). Keep React/Om components colocated under `src/cljs/markright/components/`.
- Keep functions small and pure where possible; isolate Electron/FS side effects in dedicated helpers (see `main.cljs`).

## Testing Guidelines
- No automated test suite is present; rely on manual smoke tests.
- During development, run `bun run watch` in one terminal and `bun start` in another; verify file open/save flows, markdown rendering, and dialogs (unsaved-changes, save-as, error) work end to end.
- Treat `shadow-cljs` warnings as blockers before sending changes.

## Commit & Pull Request Guidelines
- Branch from `develop` and target PRs there (PRs to `master` are closed per CONTRIBUTING.md). Rebase frequently to keep the branch merge-ready.
- Use concise, imperative commit messages (`Add spellcheck default`, `Fix save dialog error`); reference issue/PR numbers when applicable.
- PRs should include: a short summary of behavior change, reproduction steps or manual test notes, and screenshots/GIFs for UI changes. Mention any build commands run.

## Security & Configuration Notes
- Do not commit built artifacts or OS-specific paths; run `bun run clean` before packaging releases.
- Electron loads local files; keep external links explicit via `shell.openExternal` helpers and avoid introducing remote code execution surfaces.
