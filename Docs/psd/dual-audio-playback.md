# Dual-Audio & TTS Pronunciation Playback — Product Specification

> Dual-path audio engine in `AudioController` combining Android `MediaPlayer` for custom URIs with locale-aware `TextToSpeech` fallback.

Last updated: 2026-11-14

## Overview

Implements `Docs/prd/dual-audio-playback.md` via `AudioController`, owned by `LanguageLearnerViewModel` and invoked as a fire-and-forget call through `viewModel.playAudio(text, audioUri, isTargetLanguage)`.

## Architecture

- **Controller:** `AudioController` implements `TextToSpeech.OnInitListener` and manages a single `MediaPlayer?` reference and `TextToSpeech?` engine (`isTtsReady: Boolean`, `currentLanguageLocale: Locale = Locale("sr", "RS")`).
- **ViewModel integration:** `LanguageLearnerViewModel.playAudio(text: String, audioUri: String? = null, isTargetLanguage: Boolean = true)` selects `Locale("sr", "RS")` when `isTargetLanguage == true` and `Locale("ru", "RU")` when `false`, then delegates to `audioController.speak(text, audioUri, locale)`.

## Data flow

1. A screen (`VocabDrillScreen`, `SentencePracticeScreen`, `GrammarMatrixScreen`, or `SpacedRepetitionScreen`) calls `viewModel.playAudio(text, audioUri, isTargetLanguage)`.
2. `AudioController.speak` checks `if (!audioUri.isNullOrBlank())` and calls `playCustomAudio(audioUri)`:
   - Releases any existing `mediaPlayer` (`mediaPlayer?.release()`), creates a new `MediaPlayer` with `CONTENT_TYPE_SPEECH` and `USAGE_MEDIA`, and parses `Uri.parse(audioUri)`.
   - If `uri.scheme == null || uri.scheme == "file"`, resolves `File(uri.path ?: audioUri)`: if `file.exists()`, calls `setDataSource(file.absolutePath)`, `prepare()`, and `start()`, returning `true`; otherwise returns `false`.
   - For any other non-null scheme (`content://`, `android.resource://`, etc.), calls `setDataSource(context, uri)`, `prepare()`, and `start()`, returning `true` (or catching any `Exception` and returning `false`).
3. If `playCustomAudio` returns `true`, `speak` returns immediately without invoking TTS.
4. If `audioUri.isNullOrBlank()` or `playCustomAudio` returns `false`, `AudioController.speak` checks `if (!isTtsReady || tts == null)` (logging `"TTS not ready yet"` and returning if true), otherwise sets `tts?.setLanguage(languageLocale ?: currentLanguageLocale)` and calls `tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Utterance_${System.currentTimeMillis()}")`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `AudioController` | `app/src/main/java/com/example/audio/AudioController.kt` | Initializes TTS (`onInit`), plays custom audio URIs via `MediaPlayer`, applies locale fallbacks in `setLanguage`, and stops/releases resources (`stop`, `release`). |
| `LanguageLearnerViewModel` | `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` | Exposes fire-and-forget `playAudio` with target/source locale selection and calls `audioController.release()` in `onCleared()`. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| `audioUri` is `null`, `""`, whitespace-only, or points to a non-existent file | `playCustomAudio` is skipped (when null/blank) or returns `false` (when file missing), and `speak` falls back to TTS immediately. |
| `MediaPlayer` throws during `setDataSource`, `prepare`, or `start` | Caught in `playCustomAudio`, logs `"Failed playing custom audio: $uriString"`, returns `false`, and falls back to TTS. (No asynchronous `OnErrorListener` is registered after `start()`.) |
| `text` is empty (`""`) or whitespace-only on TTS path | Passed directly to `tts?.speak(text, TextToSpeech.QUEUE_FLUSH, ...)` without throwing, flushing any active TTS queue. |
| Device lacks `sr_RS` TTS voice data during `onInit` / `setLanguage(locale)` | `setLanguage` checks `LANG_MISSING_DATA` / `LANG_NOT_SUPPORTED` and tries `Locale("hr", "HR")`; if that returns `LANG_NOT_SUPPORTED`, calls `tts.setLanguage(Locale.getDefault())`. Inside `speak`, `tts?.setLanguage(targetLocale)` is called directly before `tts?.speak`. |
| `onInit` receives `status != TextToSpeech.SUCCESS`, or `speak` called before `onInit` | `isTtsReady` remains `false`; `onInit` logs `"TTS initialization failed: $status"`, and TTS path in `speak` logs `"TTS not ready yet"` and returns safely. |
| Overlapping `speak` calls while `MediaPlayer` or TTS is active | Custom audio path calls `mediaPlayer?.release()` before starting the new `MediaPlayer`; TTS path passes `TextToSpeech.QUEUE_FLUSH` to replace any active TTS utterance. `stop()` stops both `tts` and `mediaPlayer`. |
| `release()` called multiple times, or `speak` called after `release()` | `release()` is idempotent (`tts = null`, `mediaPlayer = null`, `isTtsReady = false`). Post-release `speak` with a valid `audioUri` creates a fresh `MediaPlayer` and plays; post-release TTS fallback hits `!isTtsReady || tts == null` and returns safely. |

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
