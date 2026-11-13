# Technology Stack: Language Learner

## Core Language & Platform
- **Language:** Kotlin `2.2.10` (Java 11 bytecode target via `compileOptions`)
- **JDK Runtime:** JDK 25 (`25.0.5`, pinned via `org.gradle.java.home` in `gradle.properties`)
- **Platform:** Android (`minSdk 24`, `targetSdk 36`, `compileSdk 36`)
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`) with Version Catalog (`gradle/libs.versions.toml`), AGP `9.1.1`, and KSP `2.3.5`
- **Unit Test JVM Args:** `--enable-native-access=ALL-UNNAMED` configured in `app/build.gradle.kts` (`testOptions.unitTests`) for clean Robolectric execution on JDK 25

## UI & Presentation
- **UI Toolkit:** Jetpack Compose (Compose BOM `2024.09.00`) with Material 3 (`material3`, `material-icons-core`, `material-icons-extended`)
- **Lifecycle & State:** `androidx.activity:activity-compose` (`1.10.1`), `androidx.lifecycle:lifecycle-viewmodel-compose` & `lifecycle-runtime-compose` (`2.8.7`)

## Architecture
- **Pattern:** Single-module Android app (`:app`) structured around MVVM (`LanguageLearnerViewModel`), Repository (`CurriculumRepository`), Room DAOs, and Jetpack Compose screens (`ui/screens/`).
- **Domain Modules:** Spaced repetition (`srs/SpacedRepetitionAlgorithm.kt`), dual-audio playback (`audio/AudioController.kt`), and curriculum import (`data/importer/CurriculumImporter.kt`).

## Local Persistence & Serialization
- **Database:** AndroidX Room `2.7.0` (`room-runtime`, `room-ktx`, `room-compiler` via KSP)
- **Serialization:** Moshi `1.15.2` (`moshi-kotlin`, `moshi-kotlin-codegen` via KSP)

## Networking & Services
- **HTTP Client:** Retrofit `2.12.0` (`converter-moshi`) and OkHttp `4.10.0` (`logging-interceptor`)
- **Firebase & AI:** Firebase BOM `34.17.0` (`firebase-ai`, `firebase-appcheck-recaptcha`, `firebase-appcheck-debug`), Secrets Gradle Plugin `2.0.1`
- **Async:** Kotlin Coroutines `1.10.2` (`kotlinx-coroutines-core`, `kotlinx-coroutines-android`)

## Testing
- **Unit & Integration Tests:** JUnit 4 (`4.13.2`), Robolectric (`4.16.1`), `kotlinx-coroutines-test` (`1.10.2`)
- **UI & Instrumented Tests:** Compose UI Test (`ui-test-junit4`), AndroidX Test (`core`, `junit`, `runner`), Espresso (`3.7.0`)
