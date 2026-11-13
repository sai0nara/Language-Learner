# Product Guidelines: Language Learner

## Visual Design & Aesthetic
- **Clean Material 3:** Use Jetpack Compose Material 3 components with calm, study-focused color palettes and high-contrast, legible typography across light and dark themes.
- **Clarity First:** Keep layouts uncluttered so target-language text, grammar matrices, and audio controls remain the visual focal point.

## UX & Study Flow Principles
- **Zero-Friction Study Sessions:** Minimize taps required to start and complete reviews, drills, and sentence practice.
- **Clear Progress & Minimal Distractions:** Show straightforward progress indicators during active sessions while avoiding unnecessary modals or visual noise.

## Tone & Voice
- **Warm, Encouraging, and Supportive:** Use friendly, motivating microcopy that celebrates learning milestones, streaks, and completed sessions without being overly verbose.

## Error Handling & Offline Resilience
- **Silent Automatic Fallbacks:** Prefer graceful, automatic fallbacks whenever possible (e.g., offline-ready defaults, safe parsing recovery) to keep the learner's flow uninterrupted.
- **Non-Blocking Offline Experience:** Core study, SRS, grammar, and import/export workflows must never block or fail due to lack of network connectivity.
