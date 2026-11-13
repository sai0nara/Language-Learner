# Implementation Plan: Port UI Testing, Docs/LLMWiki, README/AGENTS/GEMINI, and Skills/Commands

## Phase 1: Port Skills, Commands, and Root Agent/Project Docs
- [x] Task: Audit and port `.gemini/commands/`, `.agents/skills/`, and `.agents/plugins/` from `health-journal`
    - [x] Inspect `.gemini/commands/`, `.agents/skills/`, and `.agents/plugins/` in `/Users/sai/VS_CODE_PROJECTS/health-journal`
    - [x] Copy and adapt project-specific references for Language Learner
- [x] Task: Port and adapt `README.md`, `AGENTS.md`, and `GEMINI.md`
    - [x] Read `README.md`, `AGENTS.md`, and `GEMINI.md` from `health-journal`
    - [x] Write Language Learner-adapted `README.md`, `AGENTS.md`, and `GEMINI.md`
- [x] Task: Conductor - User Manual Verification 'Phase 1: Port Skills, Commands, and Root Agent/Project Docs' (Protocol in workflow.md)

## Phase 2: Port `Docs/` and `wiki/` (LLM Wiki) Maintenance Approach
- [x] Task: Port `Docs/` structure and templates, and seed initial Language Learner docs
    - [x] Inspect `Docs/` (`_templates/`, `index.md`, `Defects.md`, `Review.md`, `prd/`, `psd/`, `tests/`) in `health-journal`
    - [x] Create `Docs/` with templates, `index.md`, `Defects.md`, `Review.md`, and initial Language Learner PRD/PSD/test docs
- [x] Task: Port `wiki/` tooling, hooks, and seed initial Language Learner wiki pages
    - [x] Port `wiki/lint.py`, `wiki/lint.sh`, `wiki/test_lint.py`, `wiki/hooks/`, `wiki/meta/`, and folder structure
    - [x] Seed initial Language Learner wiki pages (`index.md`, `modules/`, `service/`, `tests/`, etc.)
    - [x] Run `wiki/lint.sh` and `wiki/test_lint.py` and confirm all checks pass
- [x] Task: Conductor - User Manual Verification 'Phase 2: Port `Docs/` and `wiki/` (LLM Wiki) Maintenance Approach' (Protocol in workflow.md)

## Phase 3: Port UI Testing Approach and Add Initial Compose UI Tests
- [x] Task: Audit UI testing approach in `health-journal`
    - [x] Inspect UI test files, test rules, Robolectric/Compose setup, and helpers in `health-journal/app/src/`
- [x] Task: Port UI testing pattern and write Compose UI tests for Language Learner screens
    - [x] Add UI test helper/harness if applicable and write failing/verification Compose UI tests for key Language Learner screens
    - [x] Run `gradle testDebugUnitTest` and confirm all unit and Compose UI tests pass
- [x] Task: Conductor - User Manual Verification 'Phase 3: Port UI Testing Approach and Add Initial Compose UI Tests' (Protocol in workflow.md)
