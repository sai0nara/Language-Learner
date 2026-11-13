# Language Learner — Dynamic Grading, Lesson Unlocking & Inner-Case Word Banks

This update addresses three key pedagogical refinements:
1. **Dynamic 100-Answer Grading & Progression**: All lessons initialize with a default grade of `0.0`. Every right and wrong answer in both Sentence Practice and Vocabulary Drills updates a rolling history of the user's last 100 answers per lesson. The grade is computed as `(right_answers / 100) * 5.0`. Reaching 90 or more correct answers yields a grade of `4.5` or higher, which officially masters the lesson and unblocks the subsequent lesson.
2. **Morphological Word Variants (Distractors)**: Sentences and vocabulary drills will generate plausible grammatical variants (e.g., alternative tenses, numbers, grammatical cases, and near-homophones) alongside correct tokens to test active syntax mastery.
3. **Inner-Case (Lowercase) Word Bank Tiles**: All word chips in the tile pool and assembly bay are standardized to lowercase without initial capital letters or punctuation clues, ensuring users must actively reconstruct the syntax rather than identifying the first word by capitalization.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following parameters were confirmed during clarification:
> - **Grade Scaling**: Calculated against 100 total slots from the start (`grade = (correctAnswersInLast100 / 100.0) * 5.0`). The grade begins at `0.0` and scales progressively towards `5.0`.
> - **Progression Gate**: Lesson 1 is unlocked by default. Subsequent lessons remain locked until the immediately preceding lesson achieves a grade of `4.5` (90/100 correct answers).
> - **Input Sources**: Both Vocabulary Matching drills and Sentence Construction exercises feed into the 100-answer history.
> - **Case Normalization**: All word chips in the sentence construction bank are strictly lowercased.

---

### 1. Overview & Core Concept

- **What It Does**:
  - Replaces all hardcoded `4.5` ratings with real-time dynamic grades stored in Room (`LessonEntity.masteryScore`).
  - Maintains an answer history table `lesson_answer_history` tracking the last 100 answers (`isCorrect: Boolean`) per lesson.
  - Computes `masteryScore = (correctCount / 100.0f) * 5.0f` on every single answer submission.
  - Automatically unlocks `Lesson N + 1` as soon as `Lesson N` achieves `masteryScore >= 4.5f`.
  - Normalizes all sentence word tokens to lower-case ("inner case"), removing period/question mark attachments from tiles so capitalization doesn't hint at the sentence opener.
  - Enriches sentence word pools with morphological distractor variants (e.g., for *„хоћу“*, distractors include *„нећу“*, *„хоћеш“*, *„хтео“*).

---

### 2. User Experience & Visual Design

- **Lesson List**:
  - Unlocked lessons display current dynamic score badge (e.g. `0.0`, `2.1`, `4.5`) or green checkmark when `masteryScore >= 4.5`.
  - Locked lessons show a lock icon with disabled card state and indicator (e.g., *"Требуется оценка 4.5 в Уроке 1"*).
  - Lesson 1 is always unlocked.
- **Lesson Detail Screen**:
  - Shows actual current lesson grade (e.g. `1.8 / 5.0`) and progress towards unlocking the next lesson (e.g., `36/100 правильных ответов`).
- **Sentence Construction Screen**:
  - Word pool chips render strictly in lowercase (e.g., `„да“`, `„ли“`, `„данас“`, `„радиш“`, `„у“`, `„канцеларији“`).
  - Distractor pool contains grammatical variants (different person/tense forms of verbs, case variants of nouns).
  - Answering correctly/incorrectly immediately updates the top counter and registers an answer in the 100-slot sliding window.
- **Vocabulary Drill Screen**:
  - Each translation choice records into the lesson's answer history and updates the live grade.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Persistent Rolling 100-Answer Window**
  - *Chosen Approach*: A dedicated Room entity `LessonAnswerEntity` storing `(id, lessonId, isCorrect, timestamp)`. When a new answer is recorded, old answers beyond the last 100 for that lesson are pruned, and the lesson's `masteryScore` is recalculated and saved to `LessonEntity`.
  - *Why*: Ensures resilience across app restarts and exact compliance with the formula `(correct / 100) * 5.0`.
- **Decision 2: Inner-Case Normalization**
  - *Chosen Approach*: Filter and transform all tokens via `.trim().lowercase().replace(Regex("[.,?!]"), "")` before presenting them as chips. Punctuation is re-attached on assembly validation or ignored during syntactic equality check.
  - *Why*: Eliminates the "capital letter gives away first word" loophole common in tile-based language apps.

---

### 4. Technical Architecture & Component Structure

```
┌────────────────────────────────────────────────────────────────────────┐
│                        UI LAYER (Compose)                              │
│  ┌───────────────────────┐ ┌─────────────────────────────────────────┐ │
│  │ LessonStreamCard      │ │ SentencePracticeScreen                   │ │
│  │ (Lock / Grade 0.0-5.0)│ │ (Inner-case word chips + distractor pool)│ │
│  └───────────▲───────────┘ └────────────────────▲────────────────────┘ │
│              │                                  │                      │
│              └─────────────────┬────────────────┘                      │
└────────────────────────────────┼───────────────────────────────────────┘
                                 │
┌────────────────────────────────▼───────────────────────────────────────┐
│                    VIEWMODEL & REPOSITORY                              │
│  LanguageLearnerViewModel.recordAnswer(lessonId, isCorrect: Boolean)   │
│  - Appends answer to LessonAnswerEntity                                │
│  - Prunes records older than 100 most recent for lesson                │
│  - Computes: grade = (count(isCorrect=true) / 100.0f) * 5.0f          │
│  - If grade >= 4.5f: marks lesson isCompleted = true                   │
│  - Updates lessons state stream                                        │
└────────────────────────────────┬───────────────────────────────────────┘
                                 │
┌────────────────────────────────▼───────────────────────────────────────┐
│                     ROOM DATABASE                                      │
│  ┌─────────────────────────┐  ┌─────────────────────────────────────┐  │
│  │ LessonEntity            │  │ LessonAnswerEntity                  │  │
│  │ - id, orderIndex        │  │ - id (auto)                         │  │
│  │ - masteryScore (0.0-5.0)│◄─┤ - lessonId                          │  │
│  │ - isCompleted (grade>=4.5) │ - isCorrect (Boolean)               │  │
│  │ - isUnlocked (index=1 || │ │ - timestamp                         │  │
│  │   prev.isCompleted)     │  │ (Max 100 records per lesson)        │  │
│  └─────────────────────────┘  └─────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```
