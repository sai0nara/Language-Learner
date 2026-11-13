# Product Guide: Language Learner

## Vision & Overview
**Language Learner** is a strictly offline-first Android language learning application designed for self-directed learners who want full control over their study materials. Learners can import custom curricula (XML, CSV, JSON) from textbooks, tutors, or personal notes, and study through structured lessons, spaced repetition, vocabulary drills, sentence practice, 3x3 grammar matrices, and dual-audio pronunciation playback.

## Target Users
- **Self-directed language learners** importing custom curricula from textbooks, tutors, or personal study sets.

## Core Capabilities
- **Curriculum Management & Import/Export:** Structured lesson organization with custom XML, CSV, and JSON curriculum import and export.
- **Spaced Repetition & Active Drills:** Built-in SRS scheduling, vocabulary drills, and sentence practice screens backed by local Room persistence.
- **3x3 Grammar Matrices & Dual Audio:** Visual 3x3 grammar pattern matrices and dual-audio pronunciation playback for listening and comprehension.
- **AI-Assisted Support (Optional/Future):** Leveraging Firebase AI capabilities for content generation or study support without compromising offline availability.

## Product Principles
- **Strictly Offline-First:** All core study, import/export, SRS, grammar matrix, and audio features must work reliably without an internet connection.
- **Reliability & Data Integrity:** High confidence in spaced repetition scheduling, curriculum parsing/importing, and local Room database persistence.

## Near-Term Development Goals
1. Improve unit and integration test coverage and reliability across the SRS algorithm, curriculum importer, and Room persistence layer.
2. Expand import/export formats and thoughtfully integrate optional AI or cloud-assisted features while preserving the offline-first core.
