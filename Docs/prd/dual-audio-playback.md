# Dual-Audio & TTS Pronunciation Playback — Product Requirements

> Provides custom audio file/URI playback with automatic Android Text-to-Speech (TTS) fallback across target (Serbian) and source (Russian) languages.

Last updated: 2026-11-13

## Overview

`app/src/main/java/com/example/audio/AudioController.kt` powers pronunciation playback across vocabulary drills, sentence construction, 3x3 grammar matrices, and SRS flashcards. Whenever a vocabulary item or sentence specifies a custom `audioUri`, `AudioController` plays it via `MediaPlayer`; if no custom audio is present or playback fails, it falls back seamlessly to Android `TextToSpeech` in the appropriate locale (`sr_RS` or `ru_RU`).

## Goals / Non-goals

**Goals**

- Play custom local audio files or URIs when provided on a vocabulary or sentence item.
- Automatically fall back to offline Android TTS when `audioUri` is blank or unreachable.
- Support switching TTS locales between target language (`sr_RS`, with `hr_HR` and default locale fallbacks) and source language (`ru_RU`).

**Non-goals**

- Cloud-only neural TTS requiring an active internet connection.

## User stories

- As a learner, I want to hear Serbian words and sentences pronounced even when my imported deck does not bundle `.mp3` files.
- As a curriculum author, I want to attach custom `audioUri` paths to vocabulary or sentence items and have the app play them automatically.

## Functional requirements

- FR-1: Attempt `MediaPlayer` playback first when `audioUri` is non-blank.
- FR-2: Fall back to Android `TextToSpeech` when `audioUri` is blank, the file does not exist, or `MediaPlayer` throws an exception.
- FR-3: Configure TTS locale to `sr_RS` for target-language utterances (falling back to `hr_HR` then `Locale.getDefault()` if Serbian voice data is missing) and `ru_RU` for source-language translations.
- FR-4: Release `MediaPlayer` and shut down `TextToSpeech` cleanly when `LanguageLearnerViewModel.onCleared()` is called.

## Non-functional requirements

- **Silent fallback:** Missing audio files or unsupported TTS locales never crash or block study flows.

## Acceptance criteria

Defined in `Docs/psd/dual-audio-playback.md` and verified in `Docs/tests/dual-audio-playback.md`.

## Out of scope

- In-app voice recording.

## Cross-references

- `Docs/psd/dual-audio-playback.md` — the specification that implements these requirements.
- `Docs/tests/dual-audio-playback.md` — the test cases that verify them.
- [[srs-and-audio]] — SRS and audio module overview.

## Sources

- `app/src/main/java/com/example/audio/AudioController.kt` — `MediaPlayer` and `TextToSpeech` controller.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `playAudio` locale routing and lifecycle cleanup.
- `Docs/psd/dual-audio-playback.md` — product specification.
- `Docs/tests/dual-audio-playback.md` — test cases.
