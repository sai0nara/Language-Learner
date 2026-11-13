# Specification: Update Project to be Compatible with JDK 25

## Overview
Update the **Language Learner** Android project's build configuration, toolchain settings, and Gradle/AGP/Kotlin/KSP plugin versions as needed so that the project builds cleanly and runs unit tests reliably on **JDK 25**.

## Functional Requirements
- **FR1 — JDK 25 Build & Plugin Compatibility:** Audit and upgrade Gradle, Android Gradle Plugin (AGP), Kotlin, KSP, and related build plugins in `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`, and `gradle.properties` as needed to support JDK 25 without `jlink` / `JdkImageTransform` or bytecode/compiler errors.
- **FR2 — Gradle Toolchain Configuration:** Configure Gradle Java toolchain / daemon settings so builds target and run cleanly with JDK 25.
- **FR3 — Documentation Sync:** Document the JDK 25 compatibility and any upgraded plugin/toolchain versions in `conductor/tech-stack.md`.

## Non-Functional Requirements
- **NFR1 — Android Compatibility:** Preserve `minSdk = 24` and `targetSdk = 36` Android runtime compatibility.
- **NFR2 — Build Reliability:** Ensure both `assembleDebug` and `testDebugUnitTest` succeed deterministically under JDK 25.

## Acceptance Criteria
- Running `assembleDebug` with JDK 25 completes with `BUILD SUCCESSFUL`.
- Running `testDebugUnitTest` with JDK 25 completes with all unit and Robolectric tests passing.
- `conductor/tech-stack.md` reflects the updated JDK 25 compatibility and plugin versions.

## Out of Scope
- Functional changes to application screens, SRS logic, or curriculum import behavior.
