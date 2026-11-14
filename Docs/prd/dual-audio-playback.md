# Dual-Audio & TTS Pronunciation Playback — Product Requirements

> Provides custom audio file/URI playback with automatic Android Text-to-Speech (TTS) fallback across target (Serbian) and source (Russian) languages.

Last updated: 2026-11-14

## Overview

`app/src/main/java/com/example/audio/AudioController.kt` and `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` power fire-and-forget pronunciation playback across vocabulary drills, sentence construction, 3x3 grammar matrices, and SRS flashcards. Whenever a vocabulary item or sentence specifies a custom `audioUri`, `AudioController` plays it synchronously via `MediaPlayer`; if no custom audio is present or `setDataSource`/`prepare`/`start` fails, it falls back seamlessly to Android `TextToSpeech` in the requested locale (`sr_RS` or `ru_RU`).

## Goals / Non-goals

**Goals**

- Play custom local audio files (`file://` or raw paths) or content/resource URIs when provided on a vocabulary or sentence item.
- Automatically fall back to offline Android TTS when `audioUri` is `null`, blank, points to a missing file, or throws during `MediaPlayer` setup.
- Support switching TTS locales between target language (`sr_RS`, with `hr_HR` and `Locale.getDefault()` initialization fallbacks) and source language (`ru_RU`).

**Non-goals**

- Cloud-only neural TTS requiring an active internet connection.
- UI playback progress bars or reactive playing/stopped state indicators on study screens.

## User stories

- As a learner, I want to hear Serbian words and sentences pronounced even when my imported deck does not bundle `.mp3` files.
- As a curriculum author, I want to attach custom `audioUri` paths to vocabulary or sentence items and have the app play them automatically.

## Functional requirements

- **FR-1 (Custom `audioUri` playback & URI scheme handling):**
  - `LanguageLearnerViewModel.playAudio(text: String, audioUri: String? = null, isTargetLanguage: Boolean = true)` accepts a nullable `audioUri` (defaulting to `null`) and delegates to `AudioController.speak(text, audioUri, locale)`.
  - When `!audioUri.isNullOrBlank()`, `AudioController.speak` invokes `playCustomAudio(audioUri)`, which first calls `mediaPlayer?.release()` on any existing `MediaPlayer` instance, configures `AudioAttributes` (`CONTENT_TYPE_SPEECH`, `USAGE_MEDIA`), parses `Uri.parse(audioUri)`, and:
    1. If `uri.scheme == null || uri.scheme == "file"`, checks `File(uri.path ?: audioUri).exists()`; if `true`, calls `setDataSource(file.absolutePath)`, `prepare()`, and `start()`, returning `true`; if `false`, returns `false` immediately.
    2. For any other non-null scheme (e.g., `content://`, `android.resource://`), calls `setDataSource(context, uri)`, `prepare()`, and `start()`, returning `true`.
  - If `playCustomAudio` returns `true`, `speak` returns without invoking TTS. No `OnErrorListener` or `OnCompletionListener` is registered for asynchronous post-`start()` errors.
- **FR-2 (TTS fallback & `text` handling):**
  - When `audioUri.isNullOrBlank()`, the local file does not exist, or `playCustomAudio` catches an `Exception` during `setDataSource`/`prepare`/`start` (logging `"Failed playing custom audio: $uriString"` and returning `false`), `AudioController.speak` falls back to Android `TextToSpeech`.
  - If `!isTtsReady || tts == null` (before `onInit(TextToSpeech.SUCCESS)`, when `onInit` fails with `status != TextToSpeech.SUCCESS`, or after `release()`), `speak` logs `"TTS not ready yet"` and returns safely as a no-op.
  - When TTS is ready, `speak` imposes no custom length cap or blank-string filter on `text` (empty `""` or whitespace-only strings are passed through to `tts.speak`), flushes any in-progress TTS utterance via `TextToSpeech.QUEUE_FLUSH`, and uses utterance ID `"Utterance_<timestamp>"`.
- **FR-3 (Locale selection & fallback):**
  - On `onInit(TextToSpeech.SUCCESS)`, `isTtsReady` is set to `true` and `AudioController.setLanguage(currentLanguageLocale)` (initial `Locale("sr", "RS")`) is called. If `tts.setLanguage(locale)` returns `LANG_MISSING_DATA` or `LANG_NOT_SUPPORTED`, `setLanguage` attempts `Locale("hr", "HR")`; if that returns `LANG_NOT_SUPPORTED`, it calls `tts.setLanguage(Locale.getDefault())` (ignoring any further error code).
  - On each `viewModel.playAudio(text, audioUri, isTargetLanguage)` call, `locale` is mapped to `Locale("sr", "RS")` when `isTargetLanguage == true` and `Locale("ru", "RU")` when `false`. Inside `AudioController.speak`, `tts?.setLanguage(languageLocale ?: currentLanguageLocale)` is called directly before `tts?.speak(...)`.
- **FR-4 (Stop, release, and post-release lifecycle):**
  - `AudioController.stop()` calls `tts?.stop()` and stops/releases `mediaPlayer` (setting `mediaPlayer = null` and swallowing any `MediaPlayer` state exceptions).
  - `LanguageLearnerViewModel.onCleared()` calls `audioController.release()`, which invokes `stop()`, calls `tts?.shutdown()`, and sets `tts = null` and `isTtsReady = false`. Repeated calls to `release()` are idempotent no-ops.
  - If `speak` is called after `release()`, a valid non-blank `audioUri` still instantiates a new `MediaPlayer` and plays custom audio, whereas the TTS fallback path hits `!isTtsReady || tts == null`, logs `"TTS not ready yet"`, and returns safely.

## Non-functional requirements

- **Silent fallback & fire-and-forget UX:** Missing audio files, uninitialized TTS, or unsupported TTS locales never crash or block study flows, and `playAudio` executes without altering screen UI state.

## Acceptance criteria

Defined in `Docs/psd/dual-audio-playback.md` and verified in `Docs/tests/dual-audio-playback.md`.

## Out of scope

- In-app voice recording.
- Asynchronous post-`start()` `MediaPlayer.OnErrorListener` recovery.

## Cross-references

- `Docs/psd/dual-audio-playback.md` — the specification that implements these requirements.
- `Docs/tests/dual-audio-playback.md` — the test cases that verify them.
- [[srs-and-audio]] — SRS and audio module overview.

## Sources

- `app/src/main/java/com/example/audio/AudioController.kt` — `MediaPlayer` and `TextToSpeech` controller.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `playAudio` locale routing and lifecycle cleanup.
- `Docs/psd/dual-audio-playback.md` — product specification.
- `Docs/tests/dual-audio-playback.md` — test cases.
