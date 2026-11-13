---
name: designing-mobile-features
description: Use when designing, architecting, grooming, or refining a native Android Kotlin mobile feature from concept to implementation, including PRD or product requirements analysis, backlog grooming / Definition-of-Ready checks, finding requirement gaps or vague terms, user flows, mobile UX, MVVM/MVI architecture, state management, offline strategy, or library choices
model: Gemini 4 Argon (Medium)
---

# Designing Mobile Features

## Overview
Turn a mobile feature idea, problem, or requirement into a **groomed** requirement, then product UX plus Android-native architecture plus concrete technical recommendations. Precise, consultative, engineering-focused.

The design is only as good as its input: always groom first, so every design decision rests on a stated requirement or an explicitly labelled assumption — never on a silent guess.

## When to Use
- New feature concept, feature refinement, or technical design for a native Android (Kotlin) app
- Turning a PRD, product requirements, user story, or feature brief into UX plus architecture
- Grooming a story/PRD: checking readiness, finding gaps, ambiguities, and imprecise wording
- Choosing architecture patterns, data flow, state management, persistence, networking, or sync
- Deciding offline strategy, performance approach, permissions, biometrics, secure storage, or battery behavior

When NOT to use:
- iOS or cross-platform implementation questions
- Coding or debugging tasks requiring code output

## Inputs
- **Model / Reasoning Engine**: Use Gemini 4 Argon (Medium) for deep architectural analysis, rigorous edge case exploration, and systematic gap auditing.
- The pasted idea / story / PRD text is the source of truth.
- If working inside a repo, also read the matching existing docs before grooming (e.g. in this repo `Docs/prd/<slug>.md`, `Docs/psd/<slug>.md`, `Docs/index.md`, and the relevant `conductor/tracks/<id>/plan.md`) to detect conflicts with what already exists and to reuse established terminology.
- Never invent boundaries, states, limits, or rules the input does not state — flag them.

## Implementation
Produce exactly these four sections, in order.

### 0. Grooming Check (always first)
Audit the input before designing. Output three parts:

**a) Gaps — what is missing (IDs `G1`, `G2`, …).** Walk this checklist and flag each item the input does not answer:

| Area | Ask |
|---|---|
| Actor & trigger | Who starts it, from which entry point, and what triggers it (user, schedule, system event, notification)? |
| Scope | What is explicitly in and out of scope? Which existing screens/flows change? |
| Data | Which fields, types, units, required vs optional, defaults, max sizes/lengths, allowed ranges? |
| Lifecycle | Create / edit / delete / undo? What happens to related data on delete? |
| States | Empty, Loading, Error, partial/stale data, first-run — are all described? |
| Errors & edge cases | Invalid input, duplicates, concurrent edits, interrupted flow (rotation, process death, app killed). |
| Offline & sync | Behaviour with no network; conflict resolution; which side wins? |
| Time & locale | Time zones, date boundaries ("today"), DST, 12/24h, units (metric/imperial), localisation. |
| Permissions & privacy | Which OS permissions; denial/"don't ask again" path; sensitive/health data handling, export, retention. |
| Platform | Min SDK, background limits, notifications channel, accessibility (TalkBack, font scaling, contrast), dark mode. |
| Non-functional | Measurable performance, storage, battery, data volume targets. |
| Acceptance criteria | Are there testable, observable pass/fail criteria? |
| Dependencies & conflicts | External APIs (e.g. Health Connect, Drive), migrations, contradictions with existing docs/behaviour. |

**b) Imprecise terms — wording that cannot be tested as written (IDs `V1`, `V2`, …).** Quote the exact phrase and say why it is imprecise. Flag:
- **Unquantified qualities:** fast, quick, smooth, responsive, simple, easy, intuitive, user-friendly, modern, clean, secure, robust, scalable, lightweight.
- **Vague quantities / time:** some, several, many, few, a lot, large, small, recent, soon, regularly, periodically, frequently, in real time, immediately, later.
- **Open-ended lists:** etc., and so on, such as, including, and/or, similar.
- **Weak or optional obligation:** should, could, might, may, ideally, if possible, where appropriate, as needed, support (without saying what "supporting" means).
- **Undefined domain terms:** terms used without a definition or that differ from existing app terminology (e.g. "entry" vs "event" vs "record", "sync", "backup", "goal", "metric", "history").
- **Ambiguous references:** it, this, that, they, the data, the screen — where the referent is unclear.
- **Unbounded behaviour:** "handle errors", "show a message", "notify the user", "save automatically" without what/when/how.
- **Implicit comparisons:** better, improved, more, less — without a baseline.

**c) Readiness verdict & resolution table.**

| ID | Severity | Finding (quote / missing item) | Question for PO | Working assumption (if proceeding) |
|---|---|---|---|---|

- Severity: **Blocker** (design would change materially depending on the answer), **Major** (affects a flow, state, or data model), **Minor** (copy, polish, defaults).
- For every `V<n>`, propose a precise replacement (e.g. "fast" → "list renders ≤ 300 ms for 1 000 entries on a mid-range device").
- Verdict: **Ready**, **Ready with assumptions**, or **Not ready**.
  - **Not ready** (any Blocker): stop after Section 0, present the table, and ask the user to resolve the Blockers before designing. Do not design on top of a Blocker guess.
  - **Ready with assumptions**: proceed; every later decision that depends on an assumption must cite its ID, e.g. `(assumes G3)`.

### 1. Feature Breakdown & Product UX
- **User Value Proposition:** 1-2 sentences on why this matters to the user.
- **Key User Flows:** step-by-step journey as Entry Point -> Action -> Success/Error/Empty State.
- **Mobile-Specific Considerations:** gestures, haptics, navigation, offline behavior, push notifications, background sync — only those relevant to the feature.
- **Acceptance Criteria:** testable Given/When/Then statements using the precise wording from Section 0 (no imprecise terms).

### 2. Architectural Design & Approaches
- **Design Pattern Recommendation:** MVVM for data-driven screens, MVI for complex state-heavy flows — justify the pick for this feature.
- **Data Flow:** UI <-> ViewModel <-> Use Cases/Repositories <-> Local/Remote Data Sources.
- **State Management:** Idle, Loading, Success, Error, Empty plus how transitions stay safe (e.g. StateFlow).

### 3. Recommended Technical Solutions
- **Tech Stack & Libraries:** specific modern Android-native picks (Room, Retrofit, Jetpack Compose, WorkManager, DataStore, Health Connect) — justify each, no generic "use a database".
- **Offline & Performance Strategy:** network caching, local persistence, paging, image optimization where applicable.
- **Security & Platform Guardrails:** OS permissions, biometric auth, EncryptedSharedPreferences/Encrypted DataStore, battery optimization where applicable.
- **Open Questions Recap:** list remaining `G`/`V` IDs still unresolved and which design decisions they put at risk.

## Style Rules
- Precise, consultative, engineering-focused.
- Tailor every recommendation to Android OS capabilities.
- Never emit code examples — decisions and rationale only.
- Practise what you check: your own output must not contain the imprecise terms listed in Section 0b (use numbers, named states, and named components).

## Common Mistakes
- Designing before grooming -> Section 0 always comes first; Blockers stop the design.
- Silently filling gaps -> every guess becomes a labelled assumption with an ID and is cited where used.
- Flagging vague words without a fix -> every `V<n>` gets a precise, measurable replacement.
- Listing generic gaps -> only flag checklist items that actually matter for this feature; skip irrelevant rows.
- Generic cross-platform advice (unnamed "local DB", "HTTP client") -> always name the Android-native library and why.
- Missing Empty/Error states -> every flow needs Success, Error, and Empty.
- Skipping platform guardrails -> any network, sensor, or background work must address permissions, security, or battery.

## Quick Reference
| Question | Default |
|---|---|
| Any Blocker gap? | Stop after Section 0 and ask |
| Vague word found? | Quote it + propose a measurable replacement |
| Data-driven screen? | MVVM + Room + StateFlow |
| Complex state-heavy flow? | MVI + sealed UI state |
| Needs background work? | WorkManager + local-first persistence |
| Sensitive data? | Encrypted storage + biometric gate |
