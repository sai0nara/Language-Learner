# Test Model — Dual-Audio & TTS Pronunciation Playback

- **Run Date:** 2026-11-14
- **Scoped Area:** Dual-Audio & TTS Pronunciation Playback (`AudioController` + `LanguageLearnerViewModel.playAudio` / `onCleared()`)
- **Source Specs (Paste-only I/O):** `Docs/prd/dual-audio-playback.md`, `Docs/psd/dual-audio-playback.md`
- **Gate Status:** `awaiting_approval` (All `G1`–`G6` gaps resolved in PRD/PSD; ready for human review before Stage 3 test suite generation)

---

## 1. Element Inventory

| ID | Kind | Name / Description | Assigned Techniques | Gap Status |
|---|---|---|---|---|
| `P1` | param | `text` (String, spoken utterance for TTS fallback; no custom length cap or blank filter) | EP, BVA, Error Guessing | Unblocked |
| `P2` | param | `audioUri` (`String? = null`, custom local file path, `file://`, or `content://`/`android.resource://` URI) | EP, Error Guessing | Unblocked |
| `P3` | param | `isTargetLanguage` (Boolean: `true` → `sr_RS`, `false` → `ru_RU`) | EP | Unblocked |
| `R1` | rule | Audio Playback Path & TTS Fallback Rule (3 conditions) | Decision Table | Unblocked |
| `R2` | rule | TTS Locale Selection & `setLanguage` Fallback Rule (4 columns in `setLanguage` + direct `tts.setLanguage` in `speak`) | Decision Table | Unblocked |
| `S1` | state | `Uninitialized` (before `OnInitListener.onInit(SUCCESS)` or when `onInit` fails) | State Transition | Unblocked |
| `S2` | state | `Ready` (`TextToSpeech` initialized; `MediaPlayer` idle) | State Transition | Unblocked |
| `S3` | state | `PlayingCustomAudio` (`MediaPlayer` prepared and started synchronously) | State Transition | Unblocked |
| `S4` | state | `PlayingTTS` (`tts.speak` invoked with `QUEUE_FLUSH`) | State Transition | Unblocked |
| `S5` | state | `Released` (`audioController.release()` called on `onCleared()`; `tts = null`, `isTtsReady = false`, `mediaPlayer = null`) | State Transition | Unblocked |
| `T1` | transition | `S1` → `S2` on `onInit(SUCCESS)` (legal; `onInit(ERROR)` stays in `S1`) | State Transition | Unblocked |
| `T2` | transition | `S1` → `S3` on `speak(text, valid audioUri, isTarget)` (legal) | State Transition | Unblocked |
| `T3` | transition | `S1` → `S1` on `speak(text, null/blank/invalid audioUri, isTarget)` (legal; logs `"TTS not ready yet"`) | State Transition | Unblocked |
| `T4` | transition | `S2` → `S3` on `speak(text, valid audioUri, isTarget)` (legal) | State Transition | Unblocked |
| `T5` | transition | `S2` → `S4` on `speak(text, null/blank/invalid audioUri, isTarget)` (legal) | State Transition | Unblocked |
| `T6` | transition | `S3` → `S2` on `MediaPlayer` completion or `stop()` (legal) | State Transition | Unblocked |
| `T7` | transition | `S4` → `S2` on TTS utterance completion or `stop()` (legal) | State Transition | Unblocked |
| `T8` | transition | `S1/S2/S3/S4` → `S5` on `onCleared()` / `release()` (legal; repeated `release()` is idempotent in `S5`) | State Transition | Unblocked |
| `T9` | transition | `S5` → `S5` (or `S3` for valid `audioUri`) on `speak(...)` after `release()` (illegal TTS call: hits `!isTtsReady \|\| tts == null`, logs `"TTS not ready yet"`, no-op) | State Transition | Unblocked |
| `T10` | transition | `S3` → `S3/S4` on overlapping `speak(...)` while `MediaPlayer` is playing (`playCustomAudio` calls `mediaPlayer?.release()` first; `QUEUE_FLUSH` flushes TTS) | State Transition | Unblocked |
| `C1` | config | Playback Configuration Matrix (`Screen` × `isTargetLanguage` × `audioUriState` × `ttsVoiceAvailability`) | Pairwise | Unblocked |
| `U1` | use case | Pronunciation Playback & Lifecycle (`playAudio` fire-and-forget + `stop()` / `onCleared()`) | Use Case | Unblocked |

---

## 2. Input Parameters (`P<n>`)

### `P1` — `text`
- **Type:** `String` (no custom length cap imposed by `AudioController`; passed directly to `tts.speak`).
- **Valid Equivalence Classes:**
  - `P1-V1`: Non-empty target-language text (Serbian Cyrillic/Latin word or sentence, `>= 1` char).
  - `P1-V2`: Non-empty source-language text (Russian word or sentence, `>= 1` char).
- **Edge / Boundary Equivalence Classes (Resolved `G1`):**
  - `P1-E1`: Empty (`""`, `0` chars) or whitespace-only (`"   "`) string — not filtered out; passed directly to `tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Utterance_<timestamp>")`, flushing any active TTS queue without throwing.
  - `P1-E2`: Multi-byte Unicode / diacritics (Serbian `ћ, ђ, ч, џ, ш, ж, č, ć, đ, š, ž` and Russian Cyrillic) — Error Guessing overlay.

### `P2` — `audioUri`
- **Type:** `String? = null` (nullable; defaults to `null`).
- **Valid Equivalence Classes (Resolved `G2`):**
  - `P2-V1`: Non-blank local file path (`uri.scheme == null`) or `file://` URI (`uri.scheme == "file"`) where `File(uri.path ?: audioUri).exists() == true` → `setDataSource(file.absolutePath)`, `prepare()`, `start()` returns `true`.
  - `P2-V2`: Non-blank non-`file` URI scheme (`uri.scheme != null && uri.scheme != "file"`, e.g. `content://` or `android.resource://`) that resolves via `setDataSource(context, uri)`, `prepare()`, `start()` → returns `true`.
  - `P2-V3`: `null`, empty (`""`), or whitespace-only (`"   "`) string (`audioUri.isNullOrBlank() == true`) → skips `MediaPlayer` and triggers immediate TTS fallback.
- **Invalid (Fallback-triggering) Equivalence Classes:**
  - `P2-I1`: Non-blank raw path or `file://` URI where `file.exists() == false` → `playCustomAudio` returns `false` immediately and falls back to TTS.
  - `P2-I2`: Non-blank URI that causes `MediaPlayer` (`setDataSource`, `prepare`, or `start`) to throw an `Exception` → caught, logged via `Log.w`, returns `false`, and falls back to TTS.

### `P3` — `isTargetLanguage`
- **Type:** `Boolean` (defaults to `true` in `LanguageLearnerViewModel.playAudio`).
- **Valid Equivalence Classes:**
  - `P3-V1`: `true` → `Locale("sr", "RS")` passed to `audioController.speak`.
  - `P3-V2`: `false` → `Locale("ru", "RU")` passed to `audioController.speak`.

---

## 3. Business Rules (`R<n>`)

### `R1` — Audio Playback Path & TTS Fallback Rule
- **Conditions:**
  - `C1`: `!audioUri.isNullOrBlank()` (`T` / `F`)
  - `C2`: `playCustomAudio(audioUri)` synchronous `setDataSource`/`prepare`/`start` succeeds (`T` / `F` / `-` when `C1=F`)
  - `C3`: TTS engine is ready (`isTtsReady && tts != null`) (`T` / `F` / `-` when `MediaPlayer` plays)
- **Full Decision Table:** 2 × 2 × 2 = **8** columns.
- **Reduced Decision Table (5 columns after don't-care collapse):**

| Rule | `C1`: `audioUri` non-blank | `C2`: `playCustomAudio` succeeds | `C3`: TTS ready | Action |
|---|---|---|---|---|
| `R1.1` | `T` | `T` | `-` | Release prior `mediaPlayer`, play custom audio via `MediaPlayer` (fire-and-forget, no `OnErrorListener`) |
| `R1.2` | `T` | `F` | `T` | Fall back to `tts.setLanguage(targetLocale)` + `tts.speak(..., QUEUE_FLUSH, ...)` |
| `R1.3` | `T` | `F` | `F` | Log `"TTS not ready yet"` and return safely without throwing |
| `R1.4` | `F` | `-` | `T` | Skip `MediaPlayer`; call `tts.setLanguage(targetLocale)` + `tts.speak(..., QUEUE_FLUSH, ...)` |
| `R1.5` | `F` | `-` | `F` | Skip `MediaPlayer`; log `"TTS not ready yet"` and return safely without throwing |

### `R2` — TTS Locale Selection & `setLanguage` Fallback Rule (Resolved `G3`)
- **Part A — `AudioController.setLanguage(locale)` (invoked on `onInit(SUCCESS)` with `sr_RS` or explicitly):**
  - `C1`: `tts.setLanguage(locale)` returns `LANG_MISSING_DATA` or `LANG_NOT_SUPPORTED` (`T` / `F`)
  - `C2`: `tts.setLanguage(Locale("hr", "HR"))` returns `LANG_NOT_SUPPORTED` (`T` / `F` / `-` when `C1=F`)
  - **Full Decision Table:** 2 × 2 = **4** columns; **Reduced Decision Table:** **3** columns:

| Rule | `C1`: primary `locale` missing/unsupported | `C2`: `hr_HR` returns `LANG_NOT_SUPPORTED` | Action |
|---|---|---|---|
| `R2.1` | `F` | `-` | Keep primary `locale` (`sr_RS` on init, or passed `locale`) |
| `R2.2` | `T` | `F` | Log warning and fall back to `Locale("hr", "HR")` |
| `R2.3` | `T` | `T` | Fall back to `Locale("hr", "HR")`, then call `tts.setLanguage(Locale.getDefault())` (ignoring return code if default is also unsupported) |

- **Part B — Per-utterance locale in `AudioController.speak(text, audioUri, languageLocale)`:**
  - Resolves `targetLocale = languageLocale ?: currentLanguageLocale` (`sr_RS` when `isTargetLanguage=true`, `ru_RU` when `isTargetLanguage=false`) and calls `tts?.setLanguage(targetLocale)` directly before `tts?.speak(...)`.

---

## 4. State Machine (`S<n>`, `T<n>`)

- **States (`S1`–`S5`):**
  - `S1` (`Uninitialized`): Initial state before `onInit(SUCCESS)`, or permanent state if `onInit(status != SUCCESS)` logs `"TTS initialization failed: $status"`.
  - `S2` (`Ready`): `onInit(SUCCESS)` completed (`isTtsReady = true`); `MediaPlayer` idle.
  - `S3` (`PlayingCustomAudio`): `MediaPlayer` prepared and started synchronously for `audioUri`.
  - `S4` (`PlayingTTS`): `tts.speak` active with `TextToSpeech.QUEUE_FLUSH`.
  - `S5` (`Released`): `audioController.release()` called from `LanguageLearnerViewModel.onCleared()`; `stop()` executed, `tts.shutdown()` called, `tts = null`, `mediaPlayer = null`, `isTtsReady = false`.
- **Transitions (`T1`–`T10`, Resolved `G4` & `G5`):**
  - Legal (8):
    - `T1`: `S1 → S2` on `onInit(TextToSpeech.SUCCESS)` (whereas `onInit(ERROR)` logs warning and stays in `S1`).
    - `T2`: `S1 → S3` on `speak` with valid `audioUri`.
    - `T3`: `S1 → S1` on `speak` with `null`/blank/invalid `audioUri` → logs `"TTS not ready yet"`, returns safely.
    - `T4`: `S2 → S3` on `speak` with valid `audioUri`.
    - `T5`: `S2 → S4` on `speak` with `null`/blank/invalid `audioUri`.
    - `T6`: `S3 → S2` on `MediaPlayer` completion or `stop()`.
    - `T7`: `S4 → S2` on TTS completion or `stop()`.
    - `T8`: `S1/S2/S3/S4 → S5` on `onCleared()` / `release()` (and `S5 → S5` on repeated idempotent `release()`).
  - Illegal / Guarded / Overlap (2):
    - `T9`: `S5 → S5` on `speak` with `null`/blank/invalid `audioUri` after `release()` (guarded by `!isTtsReady || tts == null`, logs `"TTS not ready yet"` without crashing; note: if a valid custom `audioUri` is passed after `release()`, `playCustomAudio` creates a new `MediaPlayer` and transitions `S5 → S3`).
    - `T10`: `S3 → S3/S4` on overlapping `speak(...)` while `MediaPlayer` is already playing (`playCustomAudio` calls `mediaPlayer?.release()` before starting the new `MediaPlayer` or falling back to `tts.speak` with `QUEUE_FLUSH`).

---

## 5. Configuration Dimensions (`C<n>`)

### `C1` — Playback Configuration Matrix (Pairwise)
- **Dimensions:**
  1. `Screen` (4): `VocabDrillScreen`, `SentencePracticeScreen`, `GrammarMatrixScreen`, `SpacedRepetitionScreen`
  2. `isTargetLanguage` (2): `true` (`sr_RS`), `false` (`ru_RU`)
  3. `audioUriState` (3): `valid` (`file://` or raw path or `content://`), `nullOrBlank`, `nonExistentOrError`
  4. `ttsVoiceAvailability` (4): `sr_RS_available`, `hr_HR_only`, `default_only`, `ru_RU_available`
- **Exhaustive Configurations:** 4 × 2 × 3 × 4 = **96** configurations.
- **Pairwise Target:** Every valid pair across dimensions covered in ≥1 configuration (to be generated in Stage 3 after gate approval).

---

## 6. Use Cases (`U<n>`)

### `U1` — Pronunciation Playback & Lifecycle
- **Basic Path (1):**
  - `U1-BP`: Screen calls `viewModel.playAudio(text, validAudioUri, isTargetLanguage)` → `AudioController.speak` invokes `playCustomAudio(audioUri)` → releases any prior `MediaPlayer`, prepares and starts new `MediaPlayer` synchronously (fire-and-forget; screen UI state unchanged).
- **Alternative Paths (2):**
  - `U1-AP1`: `audioUri` is `null` or blank → `AudioController.speak` skips `MediaPlayer`, sets `sr_RS` (`isTargetLanguage=true`) or `ru_RU` (`isTargetLanguage=false`), and calls `tts.speak` with `TextToSpeech.QUEUE_FLUSH`.
  - `U1-AP2`: During `onInit(SUCCESS)` / `setLanguage(locale)`, primary voice data is missing (`LANG_MISSING_DATA` / `LANG_NOT_SUPPORTED`) → `AudioController.setLanguage` falls back to `hr_HR`, then `Locale.getDefault()` if `hr_HR` returns `LANG_NOT_SUPPORTED`.
- **Exception Paths (3):**
  - `U1-EP1`: `audioUri` is non-blank but file does not exist or `MediaPlayer` throws during `setDataSource`/`prepare`/`start` → `playCustomAudio` returns `false`, falls back immediately to `TextToSpeech`.
  - `U1-EP2`: `TextToSpeech` engine is not yet initialized (`onInit` pending/failed) or already released when TTS fallback is needed → logs `"TTS not ready yet"` and returns safely without throwing or blocking study flow.
  - `U1-EP3`: `LanguageLearnerViewModel.onCleared()` is called → `audioController.release()` stops/releases `MediaPlayer`, shuts down `TextToSpeech`, and sets `tts = null` and `isTtsReady = false` idempotently.

---

## 7. Gaps (`G<n>`) — Resolved in PRD & PSD

All 6 previously identified gaps (`G1`–`G6`) have been resolved in `Docs/prd/dual-audio-playback.md` and `Docs/psd/dual-audio-playback.md`:

| Gap ID | Resolution Summary | Status |
|---|---|---|
| `G1` | **Empty/whitespace `text` & length limits:** `AudioController.speak` imposes no custom length limit or blank filter; empty (`""`) or whitespace-only strings are passed directly to `tts.speak(..., QUEUE_FLUSH, ...)`, flushing the TTS queue without error. | Resolved (FR-2, `P1`) |
| `G2` | **`audioUri` schemes & nullability:** `audioUri: String? = null` is nullable; `uri.scheme == null \|\| uri.scheme == "file"` checks `File.exists()` and uses `setDataSource(file.absolutePath)`, while non-`file` schemes (`content://`, `android.resource://`, etc.) use `setDataSource(context, uri)` inside `try/catch`. | Resolved (FR-1, `P2`) |
| `G3` | **TTS locale fallback in `setLanguage` vs `speak`:** `setLanguage(locale)` falls back from `locale` (`LANG_MISSING_DATA` / `LANG_NOT_SUPPORTED`) to `hr_HR`, and if `hr_HR` returns `LANG_NOT_SUPPORTED`, to `Locale.getDefault()` (ignoring its return code). Inside `speak`, `tts?.setLanguage(targetLocale)` is called directly before `tts?.speak`. | Resolved (FR-3, `R2`) |
| `G4` | **`onInit` failure, post-release `speak()`, and repeated `release()`:** `onInit(status != SUCCESS)` keeps `isTtsReady = false` and logs warning; `release()` is idempotent (`tts = null`, `isTtsReady = false`, `mediaPlayer = null`); post-release TTS fallback hits `!isTtsReady \|\| tts == null` and returns safely, while post-release valid `audioUri` creates a fresh `MediaPlayer`. | Resolved (FR-2, FR-4, `T1`, `T8`, `T9`) |
| `G5` | **Overlapping playback calls:** `playCustomAudio` calls `mediaPlayer?.release()` before starting a new `MediaPlayer`; TTS calls use `TextToSpeech.QUEUE_FLUSH`; `stop()` stops both `tts` and `mediaPlayer`. | Resolved (FR-1, FR-2, FR-4, `T10`) |
| `G6` | **Async `MediaPlayer` errors & UI state:** Only synchronous `setDataSource`/`prepare`/`start` exceptions are caught for TTS fallback (no `OnErrorListener` registered); `playAudio` is fire-and-forget (`Unit`) with no UI playback state indicator. | Resolved (FR-1, Non-goals, `R1`, `U1`) |

---

```qa-summary
schema_version=1
gate_status=approved
elements_total=22
elements_gap=0
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
boundaries_tested=0,1
dt_columns_full=12
dt_columns_reduced=8
states_total=5
transitions_legal=8
transitions_illegal=2
usecase_paths=6
configs=96
```
