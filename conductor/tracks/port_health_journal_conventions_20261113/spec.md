# Specification: Port UI Testing, Docs/LLMWiki Maintenance, README/AGENTS/GEMINI, and Skills/Commands from health-journal

## Overview
Adopt the engineering conventions, documentation/LLM-wiki maintenance workflow, UI testing approach, agent instructions, and local skills/commands from `/Users/sai/VS_CODE_PROJECTS/health-journal` and adapt them for **Language Learner**.

## Functional Requirements
- **FR1 — Root Documentation & Agent Instructions:** Port and adapt `README.md`, `AGENTS.md`, and `GEMINI.md` from `health-journal` so they accurately reflect Language Learner's architecture (`app/`, `srs/`, `audio/`, `data/`), Gradle commands (with JDK 25), and offline-first principles.
- **FR2 — `Docs/` & `wiki/` (LLM Wiki) Maintenance Approach:** Port `Docs/` structure (`_templates/`, `index.md`, `prd/`, `psd/`, `tests/`, `Defects.md`, `Review.md`) and `wiki/` structure (`index.md`, `lint.py`, `lint.sh`, `test_lint.py`, `hooks/`, `meta/`, `modules/`, `service/`, `sources/`, `tests/`, `raw/`), adapted for Language Learner and seeded with initial Language Learner documentation and wiki pages.
- **FR3 — UI Testing Approach:** Port the Compose/Robolectric UI testing approach and patterns from `health-journal` into `app/src/test` (or `androidTest` matching `health-journal`'s setup) and add initial Compose UI tests covering key Language Learner screens.
- **FR4 — Skills & Commands:** Port `.gemini/commands/`, `.agents/skills/`, and `.agents/plugins/` from `health-journal` into Language Learner, updating any project-specific references where needed.

## Non-Functional Requirements
- **NFR1 — Verification:** All ported lint/test scripts (`wiki/lint.sh`, `wiki/test_lint.py` / `pytest` or `unittest`) and Gradle unit/UI tests (`gradle testDebugUnitTest`) must pass cleanly.
- **NFR2 — Zero Health-Journal Leakage:** Adapted docs, wiki pages, `AGENTS.md`, `GEMINI.md`, and `README.md` must reference Language Learner rather than health-journal domain entities.

## Acceptance Criteria
- `README.md`, `AGENTS.md`, and `GEMINI.md` exist at the repository root and accurately describe Language Learner.
- `Docs/` and `wiki/` are in place with initial Language Learner pages, and `wiki/lint.py` + `wiki/test_lint.py` pass without errors.
- Compose UI tests following the `health-journal` pattern are added and pass under `gradle testDebugUnitTest`.
- `.gemini/commands/`, `.agents/skills/`, and `.agents/plugins/` are ported into the workspace.

## Out of Scope
- Implementing new app features beyond the ported UI tests, docs/wiki, agent config, and skills/commands.
