# Dual-Audio & TTS Pronunciation Playback — Product Specification

> Dual-path audio engine in `AudioController` combining Android `MediaPlayer` for custom URIs with locale-aware `TextToSpeech` fallback.

Last updated: 2026-11-13

## Overview

Implements `Docs/prd/dual-audio-playback.md` via `AudioController`, owned by `LanguageLearnerViewModel` and invoked through `viewModel.playAudio(text, audioUri, isTargetLanguage)`.

## Architecture

- **Controller:** `AudioController` implements `TextToSpeech.OnInitListener` and manages a single `MediaPlayer` instance and `TextToSpeech` engine.
- **ViewModel integration:** `LanguageLearnerViewModel.playAudio` selects `Locale("sr", "RS")` when `isTargetLanguage == true` and `Locale("ru", "RU")` when `false`, then delegates to `audioController.speak(...)`.

## Data flow

1. A screen (`VocabDrillScreen`, `SentencePracticeScreen`, `GrammarMatrixScreen`, or `SpacedRepetitionScreen`) calls `viewModel.playAudio(text, audioUri, isTargetLanguage)`.
2. `AudioController.speak` checks if `audioUri` is non-blank and calls `playCustomAudio(audioUri)`.
3. If `playCustomAudio` succeeds (file exists or valid URI prepares and starts), playback completes via `MediaPlayer`.
4. If `audioUri` is blank or `playCustomAudio` returns `false`, `AudioController` sets the requested `Locale` on `TextToSpeech` (applying `hr_HR` / default fallback if needed) and calls `tts.speak` with `TextToSpeech.QUEUE_FLUSH`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `AudioController` | `app/src/main/java/com/example/audio/AudioController.kt` | Initializes TTS, plays custom audio URIs via `MediaPlayer`, applies locale fallbacks, and releases resources. |
| `LanguageLearnerViewModel` | `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` | Exposes `playAudio` with target/source locale selection and calls `audioController.release()` in `onCleared()`. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| Custom `audioUri` points to a non-existent file | `playCustomAudio` returns `false` and `speak` falls back to TTS immediately. |
| Device lacks `sr_RS` TTS voice data | `setLanguage` falls back to `Locale("hr", "HR")`, then `Locale.getDefault()`. |
| TTS engine not yet initialized when `speak` is called | Logs a warning and returns safely without throwing. |

## Dependencies

- Android `android.speech.tts.TextToSpeech` and `android.media.MediaPlayer`.

## Cross-references

- `Docs/prd/dual-audio-playback.md` — the requirements this specification implements.
- `Docs/tests/dual-audio-playback.md` — the test cases that verify this design.
- [[srs-and-audio]] — SRS and audio module overview.

## Sources

- `app/src/main/java/com/example/audio/AudioController.kt` — dual-audio and TTS implementation.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `playAudio` and `onCleared`.
- `Docs/prd/dual-audio-playback.md` — requirements this specification implements.
- `Docs/tests/dual-audio-playback.md` — test cases.
