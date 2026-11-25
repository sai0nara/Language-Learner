# Code Review Register

## RV-3 — Track `revise_lessons_content_20261117` (2026-11-17)

- **Scope:** Revise Lessons Content to Suit Requirements — commits `9fa6c52..59c934e` (7 files, +977/−480).
- **Plan:** `conductor/tracks/revise_lessons_content_20261117/plan.md` · **Spec:** `conductor/tracks/revise_lessons_content_20261117/spec.md`
- **Verdict:** Resolved (`RV-3.1` and `RV-3.2` fixed and verified).

### Verification

| Check | Result |
|---|---|
| Plan compliance | Partial — Phase 1 & Phase 2 curriculum content (`app/src/main/java/com/example/data/initial/InitialCurriculumData.kt`, `app/src/main/java/com/example/data/importer/CurriculumImporter.kt`, `app/src/main/java/com/example/data/local/AppDatabase.kt`, `app/src/test/java/com/example/ExampleUnitTest.kt`) and `Docs/Defects.md` are implemented; the Phase 3 sub-task to update `Docs/psd/` and `Docs/tests/` was marked done without modifying those files |
| Style compliance | Pass — compact helper builders (`v()`, `cell()`, `grammarGuide()`, `s()`) keep the 16-lesson seed dataset readable and maintainable |
| New tests | Yes — 3 curriculum validation tests in `app/src/test/java/com/example/ExampleUnitTest.kt` |
| Test coverage | Partial — tests verify volume, Russian distractors, 9-cell matrices, and 9-coordinate sentence coverage, but do not assert that sentence `distractorTokensJson` tokens are disjoint from `tokensJson` |
| Test results | `./gradlew testDebugUnitTest`: 214 tests, 0 failures. `python3 wiki/lint.py`: exit 0 |

### Findings

| ID | Severity | Status | Finding |
|---|---|---|---|
| RV-3.1 | Low | Fixed | Four sentences include a distractor token that duplicates a token already in the target sentence |
| RV-3.2 | Low | Fixed | Phase 3 documentation & wiki freshness dates were not updated for touched source files |

#### RV-3.1 — Low — Four sentences include a distractor token that duplicates a token already in the target sentence
- **File:** `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` (Lines L883, L938, L945, L1025)
- **Context:** In 4 of the 144 sentences, one of the distractors in `distractorTokensJson` is identical to a word already present in `targetSentenceLatin` (`smete`, `bih`, `smo`, `bih`). In `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt`, `poolTokens` combines `correctTokens + distractors`, so the learner sees two identical tiles in the word bank where only one is needed, instead of two distinct morphological distractors.
- **Suggestion:**
```diff
-        s(3, "PRESENT", "NEGATIVE", "1SG", "Здесь вы не смеете парковать машину.", "Ovde ne smete da parkirate auto.", listOf("smete", "parkirati"), "Prezent (-): ne smete da..."),
+        s(3, "PRESENT", "NEGATIVE", "1SG", "Здесь вы не смеете парковать машину.", "Ovde ne smete da parkirate auto.", listOf("smemo", "parkirati"), "Prezent (-): ne smete da..."),
...
-        s(8, "PRESENT", "NEGATIVE", "1SG", "Я не стал бы предлагать это решение.", "Ne bih predložio to rešenje.", listOf("bih", "nisam"), "Potencijal (-): ne bih predložio"),
+        s(8, "PRESENT", "NEGATIVE", "1SG", "Я не стал бы предлагать это решение.", "Ne bih predložio to rešenje.", listOf("bismo", "nisam"), "Potencijal (-): ne bih predložio"),
...
-        s(9, "PAST", "AFFIRMATIVE", "1SG", "Мы вышли из здания на свежий воздух.", "Izašli smo iz zgrade na svež vazduh.", listOf("ušli", "smo"), "Perfekat: izašli smo iz..."),
+        s(9, "PAST", "AFFIRMATIVE", "1SG", "Мы вышли из здания на свежий воздух.", "Izašli smo iz zgrade na svež vazduh.", listOf("ušli", "ste"), "Perfekat: izašli smo iz..."),
...
-        s(16, "PRESENT", "AFFIRMATIVE", "1SG", "Я тренируюсь каждый день, чтобы говорить свободно.", "Vežbam svaki dan da bih govorio tečno.", listOf("bih", "govorim"), "Синтез (наст.): vežbam da bih govorio"),
+        s(16, "PRESENT", "AFFIRMATIVE", "1SG", "Я тренируюсь каждый день, чтобы говорить свободно.", "Vežbam svaki dan da bih govorio tečno.", listOf("bismo", "govorim"), "Синтез (наст.): vežbam da bih govorio"),
```
  Also add an assertion in `testInitialSentencesCoverAll9CoordinatesPerLessonWithLessonSpecificContent` in `app/src/test/java/com/example/ExampleUnitTest.kt` verifying `(tokens.toSet() intersect distractors.toSet()).isEmpty()`.

#### RV-3.2 — Low — Phase 3 documentation & wiki freshness dates were not updated for touched source files
- **File:** `conductor/tracks/revise_lessons_content_20261117/plan.md` (Lines L25–L29)
- **Context:** Commit `51ebfd9` marked Phase 3 (`Update Docs/Defects.md, Docs/psd/, and Docs/tests/ for revised 16-lesson curriculum counts and Russian vocab distractors`) complete, but only `Docs/Defects.md` was modified. Touching `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt`, `app/src/main/java/com/example/data/importer/CurriculumImporter.kt`, and `app/src/main/java/com/example/data/local/AppDatabase.kt` leaves `REVIEW` staleness warnings in `python3 wiki/lint.py` for `wiki/modules/data-layer.md`, `Docs/prd/curriculum-progression.md`, `Docs/prd/curriculum-import.md`, and `Docs/psd/curriculum-import.md`.
- **Suggestion:** Refresh the relevant `Docs/` and `wiki/modules/data-layer.md` descriptions/dates so the vault stays in sync with the 16-lesson (10 vocab + 9 sentences per lesson) seed dataset.
