package com.example.data.importer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurriculumImporterTest {

    @Test
    fun `parseJson parses full curriculum with lessons, vocab, grammar, and sentences`() {
        val result = CurriculumImporter.parseJson(CurriculumImporter.getSampleJsonTemplate())

        assertEquals(1, result.lessons.size)
        assertEquals("lesson_custom_1", result.lessons[0].id)
        assertEquals(2, result.vocabItems.size)
        assertEquals(1, result.grammarGuides.size)
        assertEquals(1, result.sentences.size)
        assertEquals("Конструкция 'Хтети + ДА + Презент'", result.grammarGuides[0].title)
    }

    @Test
    fun `parseJson parses standalone vocab array into a custom lesson`() {
        val json = """
            {
              "vocab": [
                {
                  "targetWord": "књига",
                  "targetWordLatin": "knjiga",
                  "sourceTranslation": "книга",
                  "partOfSpeech": "Существительное"
                }
              ]
            }
        """.trimIndent()

        val result = CurriculumImporter.parseJson(json)
        assertEquals(1, result.lessons.size)
        assertEquals("Imported Vocabulary", result.lessons[0].title)
        assertEquals(1, result.vocabItems.size)
        assertEquals("књига", result.vocabItems[0].targetWord)
    }

    @Test
    fun `parseJson handles malformed JSON gracefully`() {
        val result = CurriculumImporter.parseJson("{ not valid json")
        assertTrue(result.lessons.isEmpty())
        assertTrue(result.vocabItems.isEmpty())
        assertTrue(result.message.startsWith("JSON parse error:"))
    }

    @Test
    fun `parseCsv parses sample template and quoted fields with commas`() {
        val csv = """
            targetWord,targetWordLatin,sourceTranslation,partOfSpeech,exampleSentence,audioUri
            "добар, дан","dobar, dan","добрый день",Фраза,"Добар, дан!",
        """.trimIndent()

        val result = CurriculumImporter.parseCsv(csv)
        assertEquals(1, result.lessons.size)
        assertEquals(1, result.vocabItems.size)
        assertEquals("добар, дан", result.vocabItems[0].targetWord)
        assertEquals("добрый день", result.vocabItems[0].sourceTranslation)
    }

    @Test
    fun `parseCsv supports semicolon delimiter and headerless input`() {
        val csv = "кућа;kuća;дом;Существительное;Моја кућа.;"

        val result = CurriculumImporter.parseCsv(csv)
        assertEquals(1, result.vocabItems.size)
        assertEquals("кућа", result.vocabItems[0].targetWord)
        assertEquals("дом", result.vocabItems[0].sourceTranslation)
    }

    @Test
    fun `parseCsv returns empty result message for blank input`() {
        val result = CurriculumImporter.parseCsv("   \n  ")
        assertTrue(result.lessons.isEmpty())
        assertEquals("CSV file is empty", result.message)
    }

    @Test
    fun `parseXml parses sample XML template with lesson, vocab, and sentences`() {
        val result = CurriculumImporter.parseXml(CurriculumImporter.getSampleXmlTemplate())

        assertEquals(1, result.lessons.size)
        assertEquals("xml_lesson_1", result.lessons[0].id)
        assertEquals(2, result.vocabItems.size)
        assertEquals("путовати", result.vocabItems[0].targetWord)
        assertEquals(1, result.sentences.size)
        assertEquals("Ми ћемо сутра путовати.", result.sentences[0].targetSentence)
    }

    @Test
    fun `parseXml ignores vocab outside lesson and handles malformed XML`() {
        val orphanVocabXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <curriculum>
              <vocab target="orphan" translation="сирота" />
            </curriculum>
        """.trimIndent()
        val orphanResult = CurriculumImporter.parseXml(orphanVocabXml)
        assertTrue(orphanResult.lessons.isEmpty())
        assertTrue(orphanResult.vocabItems.isEmpty())

        val malformedResult = CurriculumImporter.parseXml("<<<not valid xml")
        assertTrue(malformedResult.lessons.isEmpty())
        assertTrue(
            "Expected XML parse error message, got: ${malformedResult.message}",
            malformedResult.message.startsWith("XML parse error:")
        )
    }

    @Test
    fun `readStream reads UTF-8 text from InputStream`() {
        val content = "тест,test,тест"
        val stream = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8))
        assertEquals(content, CurriculumImporter.readStream(stream))
    }

    @Test
    fun `sample XML template and JSON or XML token fallbacks use Latin`() {
        val cyrillicRegex = Regex("[\\p{InCyrillic}]")
        val xmlResult = CurriculumImporter.parseXml(CurriculumImporter.getSampleXmlTemplate())
        assertTrue(xmlResult.sentences.isNotEmpty())
        xmlResult.sentences.forEach { sentence ->
            assertTrue(
                "Sample XML sentence tokensJson should be Latin: ${sentence.tokensJson}",
                !cyrillicRegex.containsMatchIn(sentence.tokensJson)
            )
        }

        val jsonWithoutTokens = """
            {"lessons":[{"id":"l1","title":"T","sentences":[{"sourceSentence":"S","targetSentence":"Ја радим","targetSentenceLatin":"Ja radim"}]}]}
        """.trimIndent()
        val jsonResult = CurriculumImporter.parseJson(jsonWithoutTokens)
        assertEquals("[\"Ja\",\"radim\"]", jsonResult.sentences.single().tokensJson)

        val xmlWithoutTokens = """
            <curriculum><lesson id="l1" title="T"><sentence source="S" target="Ја радим" latin="Ja radim" /></lesson></curriculum>
        """.trimIndent()
        val xmlFallbackResult = CurriculumImporter.parseXml(xmlWithoutTokens)
        assertEquals("[\"Ja\",\"radim\"]", xmlFallbackResult.sentences.single().tokensJson)
    }
}
