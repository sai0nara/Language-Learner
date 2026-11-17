# Unit and UI Tests

> JVM, Robolectric, in-memory Room, and Compose UI test suites under `app/src/test`.

Last updated: 2026-11-16

## Test Stack

All automated unit, persistence, and Robolectric Compose UI tests run offline on the JVM via `gradle testDebugUnitTest` (with `--enable-native-access=ALL-UNNAMED` configured in `app/build.gradle.kts` for JDK 25), including `com.example.qa.*` feature suites and `PolyglotFsrsAndMatrixMasteryTest`.

## Cross-references

- [[data-layer]] — tested via `RoomPersistenceAndRepositoryTest` and `CurriculumImporterTest`.
- [[srs-and-audio]] — tested via `SpacedRepetitionAlgorithmTest`.
- [[ui-layer]] — tested via Compose UI tests using `createComposeRule()`.

## Sources

- `app/build.gradle.kts` — test dependencies and JDK 25 `testOptions.unitTests` configuration.
- `app/src/test/java/com/example/srs/SpacedRepetitionAlgorithmTest.kt` — SM-2 unit tests.
- `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` — JSON, CSV, and XML importer tests.
- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — in-memory Room DAO and repository tests.
- `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` — Robolectric Compose UI tests for `CurriculumScreen` and `ImportExportScreen`.

Back to [[overview]]
