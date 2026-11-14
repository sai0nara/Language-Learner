# Test Suite — Dual-Audio & TTS Pronunciation Playback

- **Run Date:** 2026-11-14
- **Scoped Area:** Dual-Audio & TTS Pronunciation Playback (`AudioController` + `LanguageLearnerViewModel.playAudio` / `onCleared()`)
- **Automated Translation:** OFF (Stage 4 not requested)

---

## 1. Test Cases

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | Custom audio playback on VocabDrillScreen (target language, Ready state) | Decision Table, State Transition, Pairwise, Use Case, EP | `AudioController` in `Ready` (`S2`); `sr_RS` TTS voice available; valid local audio file exists at `audioUri` | `Screen`: `VocabDrillScreen`, `text`: `"Здраво"`, `audioUri`: `"/local/zdravo.mp3"`, `isTargetLanguage`: `true` | `AudioController.speak` invokes `playCustomAudio(audioUri)`; `MediaPlayer` prepares and starts playback (`S3`); `tts.speak` is NOT called; returns to `Ready` (`S2`) on completion; no crash or UI block | P2 | `P1, P2, P3, R1, S2, S3, T4, T6, C1, U1` |
| `TC-002` | Custom audio playback while TTS engine is uninitialized (source language) | Decision Table, State Transition, Pairwise, EP | `AudioController` in `Uninitialized` (`S1`, `onInit` not yet called); valid local audio file exists at `audioUri` | `Screen`: `VocabDrillScreen`, `text`: `"Привет"`, `audioUri`: `"/local/privet.mp3"`, `isTargetLanguage`: `false` | `AudioController.speak` invokes `playCustomAudio(audioUri)` first; `MediaPlayer` prepares and starts playback (`S3`) without requiring TTS initialization; no warning logged | P2 | `P2, P3, R1, S1, S3, T2, C1` |
| `TC-003` | Blank `audioUri` falls back to `sr_RS` TTS after `onInit(SUCCESS)` | Decision Table, State Transition, Use Case, EP | `AudioController` transitions `S1` → `S2` via `onInit(TextToSpeech.SUCCESS)`; `sr_RS` TTS voice data available | `Screen`: `VocabDrillScreen`, `text`: `"Добар дан"`, `audioUri`: `""`, `isTargetLanguage`: `true` | `playCustomAudio` is skipped; `LanguageLearnerViewModel` selects `Locale("sr", "RS")`; `tts.setLanguage` uses `sr_RS`; `tts.speak` is called with `TextToSpeech.QUEUE_FLUSH` (`S4`); returns to `S2` on completion | P2 | `P1, P2, P3, R1, R2, S1, S2, S4, T1, T5, T7, U1` |
| `TC-004` | Blank `audioUri` falls back to `ru_RU` TTS on SpacedRepetitionScreen | Decision Table, State Transition, Pairwise, Use Case, EP | `AudioController` in `Ready` (`S2`); `ru_RU` TTS voice data available | `Screen`: `SpacedRepetitionScreen`, `text`: `"Спасибо"`, `audioUri`: `"   "`, `isTargetLanguage`: `false` | `playCustomAudio` is skipped; `LanguageLearnerViewModel` selects `Locale("ru", "RU")`; `tts.setLanguage` uses `ru_RU`; `tts.speak` is called with `TextToSpeech.QUEUE_FLUSH` (`S4`) | P2 | `P1, P2, P3, R1, R2, S2, S4, T5, C1, U1` |
| `TC-005` | Non-existent `audioUri` falls back to `hr_HR` TTS when `sr_RS` is missing | Decision Table, State Transition, Pairwise, Use Case, EP | `AudioController` in `Ready` (`S2`); device lacks `sr_RS` voice data; `hr_HR` voice data available; `audioUri` file does not exist | `Screen`: `SentencePracticeScreen`, `text`: `"Хвала"`, `audioUri`: `"/local/missing.mp3"`, `isTargetLanguage`: `true` | `playCustomAudio` returns `false`; `AudioController` falls back immediately to TTS (`S4`), falls back from `sr_RS` to `Locale("hr", "HR")`, and calls `tts.speak` with `TextToSpeech.QUEUE_FLUSH`; study flow is unblocked | P2 | `P1, P2, P3, R1, R2, S2, S4, T5, C1, U1` |
| `TC-006` | `MediaPlayer` exception falls back to `Locale.getDefault()` when `sr_RS` and `hr_HR` are missing | Decision Table, State Transition, Pairwise, Use Case, EP | `AudioController` in `Ready` (`S2`); device lacks `sr_RS` and `hr_HR` voice data; `Locale.getDefault()` available; `MediaPlayer` throws on `audioUri` | `Screen`: `VocabDrillScreen`, `text`: `"Молим"`, `audioUri`: `"corrupt://bad-audio"`, `isTargetLanguage`: `true` | `playCustomAudio` catches exception and returns `false`; `AudioController` falls back to TTS (`S4`) with `Locale.getDefault()` and calls `tts.speak` with `TextToSpeech.QUEUE_FLUSH`; no crash | P2 | `P2, P3, R1, R2, S2, S4, T5, C1, U1` |
| `TC-007` | Blank `audioUri` when TTS is uninitialized logs warning and returns safely | Decision Table, State Transition, Pairwise, Use Case | `AudioController` in `Uninitialized` (`S1`) | `Screen`: `SentencePracticeScreen`, `text`: `"Идем"`, `audioUri`: `""`, `isTargetLanguage`: `true` | `playCustomAudio` is skipped; `AudioController` detects TTS is not initialized, logs a warning, remains in `S1`, and returns safely without throwing | P2 | `P2, R1, S1, T3, C1, U1` |
| `TC-008` | Non-existent `audioUri` when TTS is uninitialized logs warning and returns safely | Decision Table, State Transition, Use Case | `AudioController` in `Uninitialized` (`S1`); `audioUri` file does not exist | `Screen`: `VocabDrillScreen`, `text`: `"Кућа"`, `audioUri`: `"/local/missing.mp3"`, `isTargetLanguage`: `true` | `playCustomAudio` returns `false`; `AudioController` detects TTS is not initialized, logs a warning, remains in `S1`, and returns safely without throwing | P2 | `P2, R1, S1, T3, U1` |
| `TC-009` | `LanguageLearnerViewModel.onCleared()` releases `MediaPlayer` and shuts down `TextToSpeech` | State Transition, Use Case | `AudioController` active in `Ready` (`S2`), `PlayingCustomAudio` (`S3`), or `PlayingTTS` (`S4`) | Lifecycle event: `LanguageLearnerViewModel.onCleared()` | `audioController.release()` is invoked; `MediaPlayer` is released and `TextToSpeech` is shut down cleanly (`S5`) | P2 | `S2, S3, S4, S5, T8, U1` |
| `TC-010` | Pairwise config: VocabDrillScreen × `false` × `blank` × `hr_HR_only` | Pairwise | `AudioController` in `Ready` (`S2`); `hr_HR` only available (`ru_RU` missing) | `Screen`: `VocabDrillScreen`, `text`: `"Да"`, `audioUri`: `""`, `isTargetLanguage`: `false` | `playCustomAudio` skipped; `LanguageLearnerViewModel` selects `ru_RU`; `[UNSTATED / G3]` fallback behavior when `ru_RU` is missing; no crash or study flow block | P2 | `C1` |
| `TC-011` | Pairwise config: SentencePracticeScreen × `false` × `blank` × `sr_RS_available` | Pairwise | `AudioController` in `Ready` (`S2`); `sr_RS` available (`ru_RU` missing) | `Screen`: `SentencePracticeScreen`, `text`: `"Нет"`, `audioUri`: `""`, `isTargetLanguage`: `false` | `playCustomAudio` skipped; `LanguageLearnerViewModel` selects `ru_RU`; `[UNSTATED / G3]` fallback behavior when `ru_RU` is missing; no crash | P2 | `C1` |
| `TC-012` | Pairwise config: SentencePracticeScreen × `false` × `valid` × `default_only` | Pairwise | `AudioController` in `Ready` (`S2`); `default_only` voice; valid `audioUri` | `Screen`: `SentencePracticeScreen`, `text`: `"Вода"`, `audioUri`: `"/local/voda.mp3"`, `isTargetLanguage`: `false` | `MediaPlayer` plays custom audio (`S3`); TTS not invoked | P2 | `C1` |
| `TC-013` | Pairwise config: GrammarMatrixScreen × `true` × `nonExistentOrError` × `sr_RS_available` | Pairwise | `AudioController` in `Ready` (`S2`); `sr_RS` available; `audioUri` missing | `Screen`: `GrammarMatrixScreen`, `text`: `"Јесам"`, `audioUri`: `"/local/missing.mp3"`, `isTargetLanguage`: `true` | `playCustomAudio` returns `false`; falls back to `sr_RS` TTS (`S4`) with `QUEUE_FLUSH` | P2 | `C1` |
| `TC-014` | Pairwise config: GrammarMatrixScreen × `false` × `valid` × `hr_HR_only` | Pairwise | `AudioController` in `Ready` (`S2`); `hr_HR_only`; valid `audioUri` | `Screen`: `GrammarMatrixScreen`, `text`: `"Быть"`, `audioUri`: `"/local/byt.mp3"`, `isTargetLanguage`: `false` | `MediaPlayer` plays custom audio (`S3`); TTS not invoked | P2 | `C1` |
| `TC-015` | Pairwise config: GrammarMatrixScreen × `true` × `blank` × `default_only` | Pairwise | `AudioController` in `Ready` (`S2`); `default_only` voice (`sr_RS` and `hr_HR` missing) | `Screen`: `GrammarMatrixScreen`, `text`: `"Радим"`, `audioUri`: `""`, `isTargetLanguage`: `true` | `playCustomAudio` skipped; TTS falls back from `sr_RS` → `hr_HR` → `Locale.getDefault()` and speaks (`S4`) | P2 | `C1` |
| `TC-016` | Pairwise config: GrammarMatrixScreen × `false` × `nonExistentOrError` × `ru_RU_available` | Pairwise | `AudioController` in `Ready` (`S2`); `ru_RU` available; `audioUri` missing | `Screen`: `GrammarMatrixScreen`, `text`: `"Делаю"`, `audioUri`: `"/local/missing.mp3"`, `isTargetLanguage`: `false` | `playCustomAudio` returns `false`; falls back to `ru_RU` TTS (`S4`) with `QUEUE_FLUSH` | P2 | `C1` |
| `TC-017` | Pairwise config: SpacedRepetitionScreen × `false` × `blank` × `sr_RS_available` | Pairwise | `AudioController` in `Ready` (`S2`); `sr_RS` available (`ru_RU` missing) | `Screen`: `SpacedRepetitionScreen`, `text`: `"Книга"`, `audioUri`: `""`, `isTargetLanguage`: `false` | `playCustomAudio` skipped; `LanguageLearnerViewModel` selects `ru_RU`; `[UNSTATED / G3]` fallback behavior when `ru_RU` is missing; no crash | P2 | `C1` |
| `TC-018` | Pairwise config: SpacedRepetitionScreen × `true` × `nonExistentOrError` × `hr_HR_only` | Pairwise | `AudioController` in `Ready` (`S2`); `hr_HR_only`; `audioUri` missing | `Screen`: `SpacedRepetitionScreen`, `text`: `"Књига"`, `audioUri`: `"/local/missing.mp3"`, `isTargetLanguage`: `true` | `playCustomAudio` returns `false`; TTS falls back from `sr_RS` to `Locale("hr", "HR")` and speaks (`S4`) | P2 | `C1` |
| `TC-019` | Pairwise config: SpacedRepetitionScreen × `true` × `valid` × `default_only` | Pairwise | `AudioController` in `Ready` (`S2`); `default_only`; valid `audioUri` | `Screen`: `SpacedRepetitionScreen`, `text`: `"Град"`, `audioUri`: `"/local/grad.mp3"`, `isTargetLanguage`: `true` | `MediaPlayer` plays custom audio (`S3`); TTS not invoked | P2 | `C1` |
| `TC-020` | Error Guessing: Multi-byte Unicode Serbian diacritics & Cyrillic text via TTS | Error Guessing, EP, Decision Table | `AudioController` in `Ready` (`S2`); `sr_RS` TTS available | `Screen`: `VocabDrillScreen`, `text`: `"Ђурђевдан — љубав (čćžšđ)"`, `audioUri`: `""`, `isTargetLanguage`: `true` | `AudioController` passes intact multi-byte Unicode string to `tts.speak` with `Locale("sr", "RS")` and `QUEUE_FLUSH` (`S4`); no encoding corruption or crash | P1 | `P1, R1, R2, S4, T5` |
| `TC-021` | Error Guessing: Empty `text` payload on TTS fallback | Error Guessing, EP | `AudioController` in `Ready` (`S2`); `sr_RS` TTS available | `Screen`: `VocabDrillScreen`, `text`: `""`, `audioUri`: `""`, `isTargetLanguage`: `true` | `[UNSTATED / G1]` — expected handling of empty `text` in `tts.speak` is unstated; must not crash or block study flow | P2 | `P1, R1` |

---

## 2. Gherkin Scenarios

```gherkin
Feature: Dual-Audio & TTS Pronunciation Playback

  @TC-001
  Scenario: Custom audio playback on VocabDrillScreen for target language
    Given AudioController is in Ready state with sr_RS TTS available
    And a valid local audio file exists at "/local/zdravo.mp3"
    When VocabDrillScreen calls viewModel.playAudio("Здраво", "/local/zdravo.mp3", true)
    Then AudioController plays "/local/zdravo.mp3" via MediaPlayer
    And TextToSpeech.speak is not invoked
    And AudioController returns to Ready state when playback completes

  @TC-002
  Scenario: Custom audio playback succeeds even when TTS is uninitialized
    Given AudioController is in Uninitialized state
    And a valid local audio file exists at "/local/privet.mp3"
    When VocabDrillScreen calls viewModel.playAudio("Привет", "/local/privet.mp3", false)
    Then AudioController plays "/local/privet.mp3" via MediaPlayer without throwing

  @TC-003
  Scenario: Blank audioUri falls back to sr_RS TTS after initialization
    Given AudioController completes onInit with TextToSpeech.SUCCESS
    And sr_RS TTS voice data is available
    When VocabDrillScreen calls viewModel.playAudio("Добар дан", "", true)
    Then AudioController skips MediaPlayer
    And TextToSpeech language is set to "sr_RS"
    And TextToSpeech.speak is called with TextToSpeech.QUEUE_FLUSH

  @TC-004
  Scenario: Blank audioUri falls back to ru_RU TTS for source language
    Given AudioController is in Ready state with ru_RU TTS available
    When SpacedRepetitionScreen calls viewModel.playAudio("Спасибо", "   ", false)
    Then AudioController skips MediaPlayer
    And TextToSpeech language is set to "ru_RU"
    And TextToSpeech.speak is called with TextToSpeech.QUEUE_FLUSH

  @TC-005
  Scenario: Missing custom audio file falls back to hr_HR TTS when sr_RS is unavailable
    Given AudioController is in Ready state
    And device lacks sr_RS TTS voice data but has hr_HR voice data
    And "/local/missing.mp3" does not exist
    When SentencePracticeScreen calls viewModel.playAudio("Хвала", "/local/missing.mp3", true)
    Then playCustomAudio returns false
    And TextToSpeech language falls back to "hr_HR"
    And TextToSpeech.speak is called with TextToSpeech.QUEUE_FLUSH

  @TC-006
  Scenario: MediaPlayer exception falls back to Locale.getDefault when sr_RS and hr_HR are unavailable
    Given AudioController is in Ready state
    And device lacks both sr_RS and hr_HR TTS voice data
    And MediaPlayer throws an exception for "corrupt://bad-audio"
    When VocabDrillScreen calls viewModel.playAudio("Молим", "corrupt://bad-audio", true)
    Then playCustomAudio returns false
    And TextToSpeech language falls back to Locale.getDefault
    And TextToSpeech.speak is called with TextToSpeech.QUEUE_FLUSH

  @TC-007
  Scenario: Blank audioUri when TTS is uninitialized logs warning and returns safely
    Given AudioController is in Uninitialized state
    When SentencePracticeScreen calls viewModel.playAudio("Идем", "", true)
    Then AudioController logs a warning
    And returns safely without throwing

  @TC-008
  Scenario: Non-existent audioUri when TTS is uninitialized logs warning and returns safely
    Given AudioController is in Uninitialized state
    And "/local/missing.mp3" does not exist
    When VocabDrillScreen calls viewModel.playAudio("Кућа", "/local/missing.mp3", true)
    Then playCustomAudio returns false
    And AudioController logs a warning and returns safely without throwing

  @TC-009
  Scenario: ViewModel onCleared releases MediaPlayer and shuts down TextToSpeech
    Given AudioController is active in Ready, PlayingCustomAudio, or PlayingTTS state
    When LanguageLearnerViewModel.onCleared is called
    Then audioController.release is invoked
    And MediaPlayer is released and TextToSpeech is shut down

  @TC-020
  Scenario: Multi-byte Unicode Serbian diacritics and Cyrillic text via TTS
    Given AudioController is in Ready state with sr_RS TTS available
    When VocabDrillScreen calls viewModel.playAudio("Ђурђевдан — љубав (čćžšđ)", "", true)
    Then TextToSpeech.speak receives the intact Unicode string with Locale("sr", "RS") and QUEUE_FLUSH

  @TC-021
  Scenario: Empty text payload on TTS fallback
    Given AudioController is in Ready state with sr_RS TTS available
    When VocabDrillScreen calls viewModel.playAudio("", "", true)
    Then behavior is [UNSTATED / G1] and the app does not crash or block study flow
```

---

## 3. Risk Register

| Test Case ID | Impact Score & Rationale | Probability Signals Counted | Probability Signals Unassessed | Probability Level | Priority |
|---|---|---|---|---|---|
| `TC-001` | 3 (core pronunciation workflow) | `boolean-logic complexity` (R1 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-002` | 3 (core pronunciation workflow) | `boolean-logic complexity` (R1 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-003` | 3 (core pronunciation workflow) | `boolean-logic complexity` (R1/R2 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-004` | 3 (core pronunciation workflow) | `boolean-logic complexity` (R1/R2 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-005` | 2 (degraded but recoverable fallback) | `boolean-logic complexity` (R1/R2 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-006` | 2 (degraded but recoverable fallback) | `boolean-logic complexity` (R1/R2 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-007` | 3 (core workflow safe degradation) | `boolean-logic complexity` (R1 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-008` | 3 (core workflow safe degradation) | `boolean-logic complexity` (R1 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |
| `TC-009` | 3 (resource cleanup on lifecycle end) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-010` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-011` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-012` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-013` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-014` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-015` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-016` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-017` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-018` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-019` | 2 (secondary pairwise config) | none → 0 | `boundary density`, `novelty` | Low (0) | P2 |
| `TC-020` | 3 (core pronunciation workflow) | `boolean-logic complexity` (R1/R2 ≥3 conditions), `known-fragile pattern` (character encoding) → 2 | `boundary density`, `novelty` | Medium (2) | P1 |
| `TC-021` | 2 (edge-case input handling) | `boolean-logic complexity` (R1 ≥3 conditions) → 1 | `boundary density`, `novelty` | Low (1) | P2 |

---

```qa-summary
schema_version=1
gate_status=approved
cases_total=21
cases_p0=0
cases_p1=1
cases_p2=20
techniques=EP,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=8
dt_columns_reduced=5
states_total=5
transitions_legal=8
transitions_illegal=2
transitions_illegal_tested=0
usecase_paths=6
usecase_paths_tested=6
configs=16
pairs_target=62
pairs_covered=62
pairs_missing=0
exhaustive_equivalent=132
post_reduction=49
post_dedup=21
```
