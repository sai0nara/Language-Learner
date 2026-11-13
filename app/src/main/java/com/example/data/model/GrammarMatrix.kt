package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConjugationCell(
    val formType: String,      // "Affirmative", "Negative", "Interrogative"
    val tense: String,         // "Past", "Present", "Future"
    val formula: String,       // e.g. "Ја сам радио / радила"
    val exampleSerbian: String,// e.g. "Ја сам јуче дуго радио."
    val exampleSerbianLatin: String = "",
    val exampleRussian: String,// e.g. "Я вчера долго работал."
    val notes: String = ""     // e.g. "Past: глагол 'бити' у презенту + радни глаголски придев"
)

@JsonClass(generateAdapter = true)
data class ConjugationMatrixData(
    val verbInfinitive: String,      // "Радити" (to work)
    val verbInfinitiveLatin: String, // "Raditi"
    val verbTranslation: String,     // "Работать"
    val aspect: String = "Imperfective",
    val cells: List<ConjugationCell> // 9 cells for the 3x3 matrix
)
