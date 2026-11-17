package com.example.data.importer

import android.util.Xml
import com.example.data.model.ConjugationCell
import com.example.data.model.ConjugationMatrixData
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.model.VocabItemEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.util.UUID

data class ImportResult(
    val lessons: List<LessonEntity> = emptyList(),
    val vocabItems: List<VocabItemEntity> = emptyList(),
    val grammarGuides: List<GrammarGuideEntity> = emptyList(),
    val sentences: List<SentenceEntity> = emptyList(),
    val message: String = ""
)

object CurriculumImporter {
    private val moshi = Moshi.Builder().build()

    // ==========================================
    // JSON PARSER
    // ==========================================
    fun parseJson(jsonString: String): ImportResult {
        return try {
            val genericMapAdapter = moshi.adapter<Map<String, Any>>(
                Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
            )
            val root = genericMapAdapter.fromJson(jsonString) ?: return ImportResult(message = "Invalid JSON structure")

            val lessons = mutableListOf<LessonEntity>()
            val vocabList = mutableListOf<VocabItemEntity>()
            val grammarList = mutableListOf<GrammarGuideEntity>()
            val sentenceList = mutableListOf<SentenceEntity>()

            // Case 1: Full curriculum with lessons
            val lessonsRaw = root["lessons"] as? List<Map<String, Any>>
            if (lessonsRaw != null) {
                lessonsRaw.forEachIndexed { index, lessonMap ->
                    val lessonId = (lessonMap["id"] as? String) ?: "lesson_${System.currentTimeMillis()}_$index"
                    val title = (lessonMap["title"] as? String) ?: "Custom Lesson ${index + 1}"
                    val subtitle = (lessonMap["subtitle"] as? String) ?: ""
                    val grammarFocus = (lessonMap["grammarFocus"] as? String) ?: "General"
                    val level = (lessonMap["level"] as? String) ?: "B1"

                    val vItems = (lessonMap["vocab"] as? List<Map<String, Any>>)?.mapIndexed { vIdx, vMap ->
                        val target = (vMap["targetWord"] as? String) ?: ""
                        val latin = (vMap["targetWordLatin"] as? String) ?: ""
                        val translation = (vMap["sourceTranslation"] as? String) ?: ""
                        val pos = (vMap["partOfSpeech"] as? String) ?: "Word"
                        val distractors = (vMap["distractors"] as? List<String>) ?: emptyList()
                        val audioUri = (vMap["audioUri"] as? String) ?: ""

                        VocabItemEntity(
                            id = "vocab_${lessonId}_$vIdx",
                            lessonId = lessonId,
                            targetWord = target,
                            targetWordLatin = latin,
                            sourceTranslation = translation,
                            partOfSpeech = pos,
                            distractorsJson = moshi.adapter<List<String>>(
                                Types.newParameterizedType(List::class.java, String::class.java)
                            ).toJson(distractors),
                            audioUri = audioUri
                        )
                    } ?: emptyList()

                    val gGuideMap = lessonMap["grammar"] as? Map<String, Any>
                    if (gGuideMap != null) {
                        val gTitle = (gGuideMap["title"] as? String) ?: title
                        val gExplanation = (gGuideMap["explanation"] as? String) ?: ""
                        val matrixRaw = gGuideMap["matrix"] as? Map<String, Any>
                        val matrixJson = if (matrixRaw != null) {
                            moshi.adapter(Any::class.java).toJson(matrixRaw)
                        } else "{}"

                        grammarList.add(
                            GrammarGuideEntity(
                                id = "grammar_$lessonId",
                                lessonId = lessonId,
                                title = gTitle,
                                summary = subtitle,
                                explanationMarkdown = gExplanation,
                                matrixJson = matrixJson
                            )
                        )
                    }

                    val sItems = (lessonMap["sentences"] as? List<Map<String, Any>>)?.mapIndexed { sIdx, sMap ->
                        val src = (sMap["sourceSentence"] as? String) ?: ""
                        val tgt = (sMap["targetSentence"] as? String) ?: ""
                        val tgtLatin = (sMap["targetSentenceLatin"] as? String) ?: ""
                        val tokens = (sMap["tokens"] as? List<String>) ?: tgtLatin.ifBlank { tgt }.split(" ")
                        val distractors = (sMap["distractors"] as? List<String>) ?: emptyList()
                        val hint = (sMap["hint"] as? String) ?: ""
                        val audioUri = (sMap["audioUri"] as? String) ?: ""

                        val strListAdapter = moshi.adapter<List<String>>(
                            Types.newParameterizedType(List::class.java, String::class.java)
                        )

                        SentenceEntity(
                            id = "sent_${lessonId}_$sIdx",
                            lessonId = lessonId,
                            sourceSentence = src,
                            targetSentence = tgt,
                            targetSentenceLatin = tgtLatin,
                            tokensJson = strListAdapter.toJson(tokens),
                            distractorTokensJson = strListAdapter.toJson(distractors),
                            grammarHint = hint,
                            audioUri = audioUri
                        )
                    } ?: emptyList()

                    vocabList.addAll(vItems)
                    sentenceList.addAll(sItems)

                    lessons.add(
                        LessonEntity(
                            id = lessonId,
                            orderIndex = index + 1,
                            title = title,
                            subtitle = subtitle,
                            grammarFocus = grammarFocus,
                            level = level,
                            vocabCount = vItems.size,
                            sentenceCount = sItems.size
                        )
                    )
                }
            } else {
                // Case 2: Direct list of vocabulary words in JSON
                val vocabRaw = root["vocab"] as? List<Map<String, Any>>
                if (vocabRaw != null) {
                    val defaultLessonId = "imported_vocab_${System.currentTimeMillis()}"
                    val vItems = vocabRaw.mapIndexed { idx, vMap ->
                        VocabItemEntity(
                            id = "vocab_${defaultLessonId}_$idx",
                            lessonId = defaultLessonId,
                            targetWord = (vMap["targetWord"] as? String) ?: "",
                            targetWordLatin = (vMap["targetWordLatin"] as? String) ?: "",
                            sourceTranslation = (vMap["sourceTranslation"] as? String) ?: "",
                            partOfSpeech = (vMap["partOfSpeech"] as? String) ?: "Word",
                            audioUri = (vMap["audioUri"] as? String) ?: ""
                        )
                    }
                    val lesson = LessonEntity(
                        id = defaultLessonId,
                        orderIndex = 99,
                        title = "Imported Vocabulary",
                        subtitle = "Imported via JSON",
                        grammarFocus = "Vocabulary",
                        level = "Custom",
                        vocabCount = vItems.size,
                        sentenceCount = 0
                    )
                    return ImportResult(
                        lessons = listOf(lesson),
                        vocabItems = vItems,
                        message = "Imported ${vItems.size} vocabulary items successfully"
                    )
                }
            }

            ImportResult(
                lessons = lessons,
                vocabItems = vocabList,
                grammarGuides = grammarList,
                sentences = sentenceList,
                message = "Imported ${lessons.size} lessons, ${vocabList.size} vocab words, and ${sentenceList.size} sentences."
            )
        } catch (e: Exception) {
            ImportResult(message = "JSON parse error: ${e.localizedMessage}")
        }
    }

    // ==========================================
    // CSV PARSER
    // ==========================================
    fun parseCsv(csvContent: String): ImportResult {
        return try {
            val lines = csvContent.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) return ImportResult(message = "CSV file is empty")

            val delimiter = if (lines.first().contains(";")) ";" else ","
            val startIndex = if (lines.first().lowercase().contains("target") || lines.first().lowercase().contains("serbian")) 1 else 0

            val lessonId = "csv_lesson_${System.currentTimeMillis()}"
            val vocabList = mutableListOf<VocabItemEntity>()

            for (i in startIndex until lines.size) {
                val tokens = parseCsvLine(lines[i], delimiter)
                if (tokens.isNotEmpty()) {
                    val targetWord = tokens.getOrNull(0)?.trim() ?: continue
                    if (targetWord.isBlank()) continue
                    val latin = tokens.getOrNull(1)?.trim() ?: ""
                    val translation = tokens.getOrNull(2)?.trim() ?: ""
                    val pos = tokens.getOrNull(3)?.trim() ?: "Word"
                    val example = tokens.getOrNull(4)?.trim() ?: ""
                    val audioUri = tokens.getOrNull(5)?.trim() ?: ""

                    vocabList.add(
                        VocabItemEntity(
                            id = "csv_vocab_${vocabList.size}",
                            lessonId = lessonId,
                            targetWord = targetWord,
                            targetWordLatin = latin,
                            sourceTranslation = translation,
                            partOfSpeech = pos,
                            exampleSentence = example,
                            audioUri = audioUri
                        )
                    )
                }
            }

            val lesson = LessonEntity(
                id = lessonId,
                orderIndex = 100,
                title = "Imported CSV Deck",
                subtitle = "Imported ${vocabList.size} words",
                grammarFocus = "Lexical Study",
                level = "Custom",
                vocabCount = vocabList.size,
                sentenceCount = 0
            )

            ImportResult(
                lessons = listOf(lesson),
                vocabItems = vocabList,
                message = "Imported CSV deck with ${vocabList.size} words."
            )
        } catch (e: Exception) {
            ImportResult(message = "CSV parse error: ${e.localizedMessage}")
        }
    }

    private fun parseCsvLine(line: String, delimiter: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        for (ch in line) {
            if (ch == '\"') {
                inQuotes = !inQuotes
            } else if (ch.toString() == delimiter && !inQuotes) {
                tokens.add(sb.toString().trim())
                sb.clear()
            } else {
                sb.append(ch)
            }
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    // ==========================================
    // XML PARSER
    // ==========================================
    fun parseXml(xmlContent: String): ImportResult {
        return try {
            val parser = Xml.newPullParser()
            parser.setInput(StringReader(xmlContent))

            val lessons = mutableListOf<LessonEntity>()
            val vocabList = mutableListOf<VocabItemEntity>()
            val grammarList = mutableListOf<GrammarGuideEntity>()
            val sentenceList = mutableListOf<SentenceEntity>()

            var currentLessonId = ""
            var currentLessonTitle = ""
            var currentLessonOrder = 1
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name.lowercase()) {
                            "lesson" -> {
                                currentLessonId = parser.getAttributeValue(null, "id") ?: "xml_l_${System.currentTimeMillis()}"
                                currentLessonTitle = parser.getAttributeValue(null, "title") ?: "Lesson $currentLessonOrder"
                                val focus = parser.getAttributeValue(null, "grammarFocus") ?: "Grammar"
                                val level = parser.getAttributeValue(null, "level") ?: "B1"

                                lessons.add(
                                    LessonEntity(
                                        id = currentLessonId,
                                        orderIndex = currentLessonOrder++,
                                        title = currentLessonTitle,
                                        subtitle = "Imported XML Lesson",
                                        grammarFocus = focus,
                                        level = level
                                    )
                                )
                            }
                            "vocab" -> {
                                if (currentLessonId.isNotEmpty()) {
                                    val target = parser.getAttributeValue(null, "target") ?: ""
                                    val latin = parser.getAttributeValue(null, "latin") ?: ""
                                    val trans = parser.getAttributeValue(null, "translation") ?: ""
                                    val pos = parser.getAttributeValue(null, "pos") ?: "Word"
                                    val audio = parser.getAttributeValue(null, "audio") ?: ""

                                    if (target.isNotBlank()) {
                                        vocabList.add(
                                            VocabItemEntity(
                                                id = "xml_v_${vocabList.size}",
                                                lessonId = currentLessonId,
                                                targetWord = target,
                                                targetWordLatin = latin,
                                                sourceTranslation = trans,
                                                partOfSpeech = pos,
                                                audioUri = audio
                                            )
                                        )
                                    }
                                }
                            }
                            "sentence" -> {
                                if (currentLessonId.isNotEmpty()) {
                                    val src = parser.getAttributeValue(null, "source") ?: ""
                                    val tgt = parser.getAttributeValue(null, "target") ?: ""
                                    val latin = parser.getAttributeValue(null, "latin") ?: ""
                                    val tokensRaw = parser.getAttributeValue(null, "tokens") ?: latin.ifBlank { tgt }.replace(" ", ",")
                                    val distractorsRaw = parser.getAttributeValue(null, "distractors") ?: ""
                                    val hint = parser.getAttributeValue(null, "hint") ?: ""
                                    val audio = parser.getAttributeValue(null, "audio") ?: ""

                                    val tokens = tokensRaw.split(",").map { it.trim() }
                                    val distractors = distractorsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }

                                    val strListAdapter = moshi.adapter<List<String>>(
                                        Types.newParameterizedType(List::class.java, String::class.java)
                                    )

                                    sentenceList.add(
                                        SentenceEntity(
                                            id = "xml_s_${sentenceList.size}",
                                            lessonId = currentLessonId,
                                            sourceSentence = src,
                                            targetSentence = tgt,
                                            targetSentenceLatin = latin,
                                            tokensJson = strListAdapter.toJson(tokens),
                                            distractorTokensJson = strListAdapter.toJson(distractors),
                                            grammarHint = hint,
                                            audioUri = audio
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }

            ImportResult(
                lessons = lessons,
                vocabItems = vocabList,
                grammarGuides = grammarList,
                sentences = sentenceList,
                message = "Imported XML with ${lessons.size} lessons, ${vocabList.size} words, ${sentenceList.size} sentences."
            )
        } catch (e: Exception) {
            ImportResult(message = "XML parse error: ${e.localizedMessage}")
        }
    }

    // Helper reader
    fun readStream(stream: InputStream): String {
        return BufferedReader(InputStreamReader(stream)).use { it.readText() }
    }

    // Sample export templates
    fun getSampleJsonTemplate(): String = """{
  "lessons": [
    {
      "id": "lesson_custom_1",
      "title": "Урок: Модальные глаголы (Хтети / Моћи)",
      "subtitle": "Хоћу да радим / Могу да учим",
      "grammarFocus": "Modal Verbs + Da + Present",
      "level": "B1",
      "vocab": [
        {
          "targetWord": "хтети",
          "targetWordLatin": "hteti",
          "sourceTranslation": "хотеть",
          "partOfSpeech": "Глагол",
          "distractors": ["moći", "znati", "imati"],
          "audioUri": ""
        },
        {
          "targetWord": "моћи",
          "targetWordLatin": "moći",
          "sourceTranslation": "мочь",
          "partOfSpeech": "Глагол",
          "distractors": ["hteti", "morati", "biti"],
          "audioUri": ""
        }
      ],
      "grammar": {
        "title": "Конструкция 'Хтети + ДА + Презент'",
        "explanation": "В сербском языке инфинитив после модальных глаголов часто заменяется союзом 'da' с личной формой настоящего времени: Hoću da radim (Я хочу работать).",
        "matrix": {
          "verbInfinitive": "Хтети",
          "verbInfinitiveLatin": "Hteti",
          "verbTranslation": "Хотеть",
          "aspect": "Imperfective",
          "cells": [
            {
              "formType": "Affirmative",
              "tense": "Present",
              "formula": "hoću, hoćeš, hoće, hoćemo, hoćete, hoće",
              "exampleSerbian": "Ja hoću da učim srpski jezik.",
              "exampleRussian": "Я хочу учить сербский язык."
            }
          ]
        }
      },
      "sentences": [
        {
          "sourceSentence": "Я хочу говорить по-сербски.",
          "targetSentence": "Ја хоћу да говорим српски.",
          "targetSentenceLatin": "Ja hoću da govorim srpski.",
          "tokens": ["ja", "hoću", "da", "govorim", "srpski."],
          "distractors": ["neću", "mogu", "radim"],
          "hint": "Используйте связку 'hoću da + prezent'",
          "audioUri": ""
        }
      ]
    }
  ]
}"""

    fun getSampleCsvTemplate(): String = """targetWord,targetWordLatin,sourceTranslation,partOfSpeech,exampleSentence,audioUri
разговарати,razgovarati,разговаривать,Глагол,Волим да разговарам са тобом.,
путовати,putovati,путешествовать,Глагол,Сутра путујемо у Београд.,
вежбати,vežbati,тренироваться / упражняться,Глагол,Сваког дана вежбам српски.,
разумевање,razumevanje,понимание,Существительное,Разумевање граматике је лако.,
потребан,potreban,нужный / необходимый,Прилагательное,Речник је врло потребан.,
"""

    fun getSampleXmlTemplate(): String = """<?xml version="1.0" encoding="utf-8"?>
<curriculum>
  <lesson id="xml_lesson_1" title="Урок 1: Будущее время" grammarFocus="Futur I" level="B1">
    <vocab target="путовати" latin="putovati" translation="путешествовать" pos="Глагол" audio="" />
    <vocab target="куповати" latin="kupovati" translation="покупать" pos="Глагол" audio="" />
    <sentence source="Мы завтра будем путешествовать." target="Ми ћемо сутра путовати." latin="Mi ćemo sutra putovati." tokens="mi,ćemo,sutra,putovati." distractors="bićemo,jeste" hint="Будущее время Futur I" audio="" />
  </lesson>
</curriculum>"""
}
