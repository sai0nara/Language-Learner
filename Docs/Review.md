# Code Review Register

## RV-2 — Track `latin_only_remove_script_switch_20261117` (2026-11-17)

- **Scope:** Remove Script Switch Button and Keep Latin Alphabet Only — commits `deffb4b..9bc8698` (22 files, +252/−267), including the post-completion Latin data fix `9bc8698`.
- **Plan:** `conductor/tracks/latin_only_remove_script_switch_20261117/plan.md` · **Spec:** `conductor/tracks/latin_only_remove_script_switch_20261117/spec.md`
- **Verdict:** Resolved. `RV-2.1` through `RV-2.7` fixed and verified (`./gradlew --no-daemon testDebugUnitTest` — 211 tests, 0 failures; `python3 wiki/lint.py` exit 0).

### Verification

| Check | Result |
|---|---|
| Plan compliance | Partial — ViewModel, `CurriculumScreen`, four screens, seed tokens and PSDs are done; the Phase 3 task to update `qa-artifacts/` is marked done but three artifact sets still reference the toggle, and `Docs/tests/*` were not refreshed |
| Style compliance | Pass — consistent `ifBlank {}` fallback idiom |
| New tests | Yes — Cyrillic-token guard in `app/src/test/java/com/example/ExampleUnitTest.kt`; QA suites updated |
| Test coverage | Partial — no realistically shaped `SENTENCE` SRS card test; no import → sentence-practice test in Latin-only mode |
| Test results | `./gradlew testDebugUnitTest`: 209 tests, 0 failures. Wiki lint exit 0 with staleness `REVIEW` warnings |

### Findings

| ID | Severity | Status | Finding |
|---|---|---|---|
| RV-2.1 | High | Fixed | Sentence SRS cards show the answer on the front |
| RV-2.2 | High | Fixed | Imported sentences that follow the sample templates can't be solved |
| RV-2.3 | Medium | Fixed | Mixed-script typo `nećете` in seed data |
| RV-2.4 | Medium | Fixed | Serbian text the learner sees is still in Cyrillic |
| RV-2.5 | Medium | Fixed | DB version bump wipes learner progress |
| RV-2.6 | Low | Fixed | Docs and QA artifacts still mention the toggle |
| RV-2.7 | Low | Fixed | Leftover `isLatin` parameter in SRS pairwise tests |

#### RV-2.1 — High — Sentence SRS cards show the answer on the front
- **File:** `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` (prompt, answer and audio selection)
- **Context:** For `SENTENCE` cards (built in `recordImplicitSentenceAttempt`, `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt`), `prompt` is the Russian source, `answer` is the Cyrillic target and `secondaryText` is the Latin target. `secondaryText.ifBlank { prompt }` therefore puts the Serbian answer on the front, and the Russian prompt never appears. Reveal then shows the same answer in Cyrillic. Before this change the bug only happened in the opt-in Latin mode; now every sentence card has it. `VOCAB` cards are unaffected.
- **Suggestion:**
```diff
-                    val promptText = currentItem.secondaryText.ifBlank { currentItem.prompt }
+                    val isSentence = currentItem.itemType == "SENTENCE"
+                    // Sentence cards: prompt = source language, secondaryText = Latin target (the answer).
+                    val promptText = if (isSentence) currentItem.prompt else currentItem.secondaryText.ifBlank { currentItem.prompt }
+                    val answerText = if (isSentence) currentItem.secondaryText.ifBlank { currentItem.answer } else currentItem.answer
+                    val targetSpeech = if (isSentence) answerText else promptText
 ...
-                                viewModel.playAudio(promptText, isTargetLanguage = true)
+                                viewModel.playAudio(targetSpeech, isTargetLanguage = true)
 ...
-                                text = currentItem.answer,
+                                text = answerText,
 ...
-                            val word = currentItem.secondaryText.ifBlank { currentItem.prompt }
-                            viewModel.playAudio(word, isTargetLanguage = true)
+                            viewModel.playAudio(targetSpeech, isTargetLanguage = true)
```
  Add a test that seeds a sentence-shaped card (`prompt="Я работаю."`, `answer="Ја радим."`, `secondaryText="Ja radim."`). It should assert the prompt is Russian and the revealed answer is Latin.

#### RV-2.2 — High — Imported sentences that follow the sample templates can't be solved
- **File:** `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` (JSON token default, XML token default, `getSampleJsonTemplate`, `getSampleXmlTemplate`)
- **Context:** `SentencePracticeScreen` now checks answers against `targetSentenceLatin`, but the tiles come from `tokensJson`. The importer builds tokens from the Cyrillic target by default, and both sample templates ship Cyrillic tokens next to a Latin field. A user who imports the official template gets Cyrillic tiles for a Latin target, so the sentence can never be answered correctly. The XML fallback also passes the whole target string as a single token.
- **Suggestion:**
```diff
-                        val tokens = (sMap["tokens"] as? List<String>) ?: tgt.split(" ")
+                        val tokens = (sMap["tokens"] as? List<String>) ?: tgtLatin.ifBlank { tgt }.split(" ")
 ...
-                                    val tokensRaw = parser.getAttributeValue(null, "tokens") ?: tgt
+                                    val tokensRaw = parser.getAttributeValue(null, "tokens") ?: latin.ifBlank { tgt }.replace(" ", ",")
 ...
-          "tokens": ["Ја", "хоћу", "да", "говорим", "српски."],
+          "tokens": ["ja", "hoću", "da", "govorim", "srpski."],
 ...
-    ... tokens="Ми,ћемо,сутра,путовати." distractors="бићемо,јесте" ...
+    ... tokens="mi,ćemo,sutra,putovati." distractors="bićemo,jeste" ...
```

#### RV-2.3 — Medium — Mixed-script typo in seed data
- **File:** `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` (future-negative matrix cell `notes`)
- **Context:** In `nećете` the ending `ете` is Cyrillic. It displays in the matrix notes and confuses learners.
- **Suggestion:**
```diff
-                    notes = "Формы: neću, nećeš, neće, nećemo, nećете, neće + инфинитив."
+                    notes = "Формы: neću, nećeš, neće, nećemo, nećete, neće + инфинитив."
```

#### RV-2.4 — Medium — Serbian text the learner sees is still in Cyrillic
- **Files:** `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` (instruction label), `app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` (`FallbackMatrixTable`), `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` (`grammarHint` values and `nineMatrixTemplates` hints)
- **Context:** These strings are Serbian, not Russian UI text, so the Latin-only goal covers them: the instruction "Преведите на српски", the fallback-table labels Презент, Перфекат and Футур I, and grammar-hint examples such as "ја сам радио", "да ли радиш", «ћу» and 'се' shown in the help dialog.
- **Suggestion:**
```diff
-                        text = "Преведите на српски",
+                        text = "Prevedite na srpski",
-                text = "• Презент: radim, ...
+                text = "• Prezent: radim, ...
-        ... "Перфекат (+): ја сам радио")
+        ... "Perfekat (+): ja sam radio")
```

#### RV-2.5 — Medium — DB version bump wipes learner progress
- **File:** `app/src/main/java/com/example/data/local/AppDatabase.kt` (`version = 4` with `fallbackToDestructiveMigration()`)
- **Context:** Going from version 3 to 4 just to reseed text drops all SRS history, mastery and unlocks on upgrade. Only acceptable while the app is unreleased.
- **Suggestion:** Keep it for now if the app isn't distributed. Otherwise add a `Migration(3, 4)` that updates the seed text and token columns by `id`, so progress rows survive. Either way, note the decision in the PSD.

#### RV-2.6 — Low — Docs and QA artifacts still mention the toggle
- **Files:** `qa-artifacts/2026-11-14-grammar-matrices/`, `qa-artifacts/2026-11-14-curriculum-progression/`, `qa-artifacts/2026-11-15-vocab-drills/` (test model, suite and coverage report); `Docs/tests/` pages flagged stale by the lint
- **Context:** The plan task is marked done, but these still mention `toggle_script_button`, `isLatinScript` and Cyrillic modes.
- **Suggestion:** Remove or retire those test-model elements and cases (e.g. mark them Obsolete in the suite and coverage report), then bump the `Docs/tests` page dates.

#### RV-2.7 — Low — Leftover `isLatin` parameter in SRS pairwise tests
- **File:** `app/src/test/java/com/example/qa/SpacedRepetitionTest.kt` (`runPairwiseConfigTest`, TC-026–TC-033)
- **Context:** `runPairwiseConfigTest(isLatin = …)` and test names like "TargetScript" or "Latin" are now meaningless, which makes the tests misleading.
- **Suggestion:** Drop the `isLatin` parameter and the script dimension from the TC-026–TC-033 names.
