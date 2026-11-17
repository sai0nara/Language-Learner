package com.example.data.initial

import com.example.data.model.ConjugationCell
import com.example.data.model.ConjugationMatrixData
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.model.VocabItemEntity
import com.squareup.moshi.Moshi

object InitialCurriculumData {
    private val moshi = Moshi.Builder().build()
    private val matrixAdapter = moshi.adapter(ConjugationMatrixData::class.java)

    fun getInitialLessons(): List<LessonEntity> = listOf(
        LessonEntity(
            id = "lesson_1",
            orderIndex = 1,
            title = "Урок 1: Презент и Базовая матрица глагола",
            subtitle = "Глаголы на -ати, -ети, -ити. Модель 3x3",
            grammarFocus = "Презент / 3x3 Матрица",
            level = "B1",
            masteryScore = 0f,
            isCompleted = false,
            isUnlocked = true, // Lesson 1 is unlocked initially
            correctAnswersInLast100 = 0,
            totalAnswersInLast100 = 0,
            vocabCount = 6,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_2",
            orderIndex = 2,
            title = "Урок 2: Возвратные глаголы с частицей 'СЕ'",
            subtitle = "Сећати се, надати се, бавити се. Позиция энклитик",
            grammarFocus = "Повратни глаголи (се)",
            level = "B1",
            masteryScore = 0f,
            isCompleted = false,
            isUnlocked = false, // Locked until Lesson 1 reaches grade 4.5
            correctAnswersInLast100 = 0,
            totalAnswersInLast100 = 0,
            vocabCount = 6,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_3",
            orderIndex = 3,
            title = "Урок 3: Модальные конструкции (Хтети / Моћи / Морати)",
            subtitle = "Замена инфинитива союзом 'ДА + Презент'",
            grammarFocus = "Модални глаголи + ДА",
            level = "B1",
            masteryScore = 0f,
            isCompleted = false,
            isUnlocked = false,
            correctAnswersInLast100 = 0,
            totalAnswersInLast100 = 0,
            vocabCount = 6,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_4",
            orderIndex = 4,
            title = "Урок 4: Прошедшее время (Перфекат)",
            subtitle = "Вспомогательный глагол 'бити' + л-форма причастия",
            grammarFocus = "Перфекат (Прошло време)",
            level = "B1",
            masteryScore = 0f,
            isCompleted = false,
            isUnlocked = false,
            correctAnswersInLast100 = 0,
            totalAnswersInLast100 = 0,
            vocabCount = 6,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_5",
            orderIndex = 5,
            title = "Урок 5: Будущее время I (Футур I)",
            subtitle = "Краткие формы глагола 'хтети': ћу, ћеш, ће / радићу vs ћу радити",
            grammarFocus = "Футур I (Будуће време)",
            level = "B1",
            masteryScore = 0f,
            isCompleted = false,
            isUnlocked = false,
            correctAnswersInLast100 = 0,
            totalAnswersInLast100 = 0,
            vocabCount = 6,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_6",
            orderIndex = 6,
            title = "Урок 6: Видовые пары глаголов",
            subtitle = "Свршени и несвршени вид (писати/написати, куповати/купити)",
            grammarFocus = "Глаголски вид (Аспект)",
            level = "B1",
            masteryScore = 0f,
            isCompleted = false,
            isUnlocked = false,
            correctAnswersInLast100 = 0,
            totalAnswersInLast100 = 0,
            vocabCount = 6,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_7", orderIndex = 7,
            title = "Урок 7: Повелительное наклонение",
            subtitle = "Императив: ради! радите! немој да радиш!",
            grammarFocus = "Императив", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_8", orderIndex = 8,
            title = "Урок 8: Условное наклонение",
            subtitle = "Потенцијал: бих, би, бисмо, бисте + радни придев",
            grammarFocus = "Потенцијал", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_9", orderIndex = 9,
            title = "Урок 9: Глаголы движения",
            subtitle = "Ићи, доћи, отићи, ући, изаћи с предлозима",
            grammarFocus = "Глаголы движения", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_10", orderIndex = 10,
            title = "Урок 10: Дательный падеж и кому/чему",
            subtitle = "Личные местоимения и клитики ми, ти, му, јој, нам",
            grammarFocus = "Датив (Дательный падеж)", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_11", orderIndex = 11,
            title = "Урок 11: Винительный и локатив",
            subtitle = "Куда vs Где (у град / у граду)",
            grammarFocus = "Акузатив и Локатив", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_12", orderIndex = 12,
            title = "Урок 12: Притяжательные местоимения",
            subtitle = "Мој, твој, његов, њен, наш, ваш, њихов",
            grammarFocus = "Притяжательные формы", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_13", orderIndex = 13,
            title = "Урок 13: Степени сравнения",
            subtitle = "Компаратив и суперлатив прилагательных и наречий",
            grammarFocus = "Сравнение", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_14", orderIndex = 14,
            title = "Урок 14: Сложные союзы",
            subtitle = "Зато што, иако, док, чим, пошто",
            grammarFocus = "Придаточные предложения", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_15", orderIndex = 15,
            title = "Урок 15: Пассив и безличные конструкции",
            subtitle = "Овде се говори српски / Треба да...",
            grammarFocus = "Безличные конструкции", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_16", orderIndex = 16,
            title = "Урок 16: Итоговый синтез (Полиглот)",
            subtitle = "Свободное комбинирование времен, падежей и вопросов",
            grammarFocus = "Комплексная матрица", level = "B1",
            vocabCount = 1, sentenceCount = 1
        )
    )

    fun getInitialVocab(): List<VocabItemEntity> = listOf(
        // Lesson 1
        VocabItemEntity(
            id = "v_1_1", lessonId = "lesson_1",
            targetWord = "радити", targetWordLatin = "raditi",
            sourceTranslation = "работать / делать", partOfSpeech = "Глагол",
            exampleSentence = "Сваког дана вредно радим у канцеларији.",
            distractorsJson = "[\"учити\", \"путовати\", \"спавати\", \"радим\"]"
        ),
        VocabItemEntity(
            id = "v_1_2", lessonId = "lesson_1",
            targetWord = "говорити", targetWordLatin = "govoriti",
            sourceTranslation = "говорить / разговаривать", partOfSpeech = "Глагол",
            exampleSentence = "Да ли добро говорите српски језик?",
            distractorsJson = "[\"видети\", \"читати\", \"писати\", \"говоримо\"]"
        ),
        VocabItemEntity(
            id = "v_1_3", lessonId = "lesson_1",
            targetWord = "разумети", targetWordLatin = "razumeti",
            sourceTranslation = "понимать", partOfSpeech = "Глагол",
            exampleSentence = "Све савршено разумем кад причате споро.",
            distractorsJson = "[\"мислити\", \"знати\", \"чути\", \"разуме\"]"
        ),
        VocabItemEntity(
            id = "v_1_4", lessonId = "lesson_1",
            targetWord = "питање", targetWordLatin = "pitanje",
            sourceTranslation = "вопрос", partOfSpeech = "Существительное",
            exampleSentence = "Имам једно важно питање за професора.",
            distractorsJson = "[\"одговор\", \"реч\", \"реченица\", \"питања\"]"
        ),
        VocabItemEntity(
            id = "v_1_5", lessonId = "lesson_1",
            targetWord = "одговор", targetWordLatin = "odgovor",
            sourceTranslation = "ответ", partOfSpeech = "Существительное",
            exampleSentence = "Чекам тачан одговор на своју поруку.",
            distractorsJson = "[\"питање\", \"разговор\", \"књига\", \"одговори\"]"
        ),
        VocabItemEntity(
            id = "v_1_6", lessonId = "lesson_1",
            targetWord = "пажљиво", targetWordLatin = "pažljivo",
            sourceTranslation = "внимательно", partOfSpeech = "Наречие",
            exampleSentence = "Молим вас, пажљиво слушајте упутства.",
            distractorsJson = "[\"брзо\", \"гласно\", \"споро\", \"пажљив\"]"
        ),

        // Lesson 2
        VocabItemEntity(
            id = "v_2_1", lessonId = "lesson_2",
            targetWord = "сећати се", targetWordLatin = "sećati se",
            sourceTranslation = "помнить / вспоминать", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Добро се сећам тог лепог дана.",
            distractorsJson = "[\"смејати се\", \"надати се\", \"бавити се\", \"сећам\"]"
        ),
        VocabItemEntity(
            id = "v_2_2", lessonId = "lesson_2",
            targetWord = "надати се", targetWordLatin = "nadati se",
            sourceTranslation = "надеяться", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Надамо се да ће време сутра бити сунчано.",
            distractorsJson = "[\"плашити се\", \"чудити се\", \"љутити се\", \"нада\"]"
        ),
        VocabItemEntity(
            id = "v_2_3", lessonId = "lesson_2",
            targetWord = "бавити се", targetWordLatin = "baviti se",
            sourceTranslation = "заниматься (спортом/делом)", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Којим спортом се обично бавите?",
            distractorsJson = "[\"допадати се\", \"одмарати се\", \"налазити се\", \"бавимо\"]"
        ),
        VocabItemEntity(
            id = "v_2_4", lessonId = "lesson_2",
            targetWord = "смејати се", targetWordLatin = "smejati se",
            sourceTranslation = "смеяться", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Сви се гласно смеју његовој шали.",
            distractorsJson = "[\"плакати\", \"љутити се\", \"стидети се\", \"смех\"]"
        ),
        VocabItemEntity(
            id = "v_2_5", lessonId = "lesson_2",
            targetWord = "допадати се", targetWordLatin = "dopadati se",
            sourceTranslation = "нравиться", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Много ми се допада Београд увече.",
            distractorsJson = "[\"сметати\", \"помагати\", \"користити\", \"допао\"]"
        ),
        VocabItemEntity(
            id = "v_2_6", lessonId = "lesson_2",
            targetWord = "навика", targetWordLatin = "navika",
            sourceTranslation = "привычка", partOfSpeech = "Существительное",
            exampleSentence = "Јутарње учење је моја најбоља навика.",
            distractorsJson = "[\"одлука\", \"правило\", \"особина\", \"навике\"]"
        ),

        // Lesson 3
        VocabItemEntity(
            id = "v_3_1", lessonId = "lesson_3",
            targetWord = "морати", targetWordLatin = "morati",
            sourceTranslation = "быть должным / обязанным", partOfSpeech = "Модальный глагол",
            exampleSentence = "Данас морам да завршим сав посао.",
            distractorsJson = "[\"моћи\", \"хтети\", \"смети\", \"морамо\"]"
        ),
        VocabItemEntity(
            id = "v_3_2", lessonId = "lesson_3",
            targetWord = "моћи", targetWordLatin = "moći",
            sourceTranslation = "мочь / иметь возможность", partOfSpeech = "Модальный глагол",
            exampleSentence = "Могу ли да добијем чашу воде, молим вас?",
            distractorsJson = "[\"морати\", \"знати\", \"требати\", \"можемо\"]"
        ),
        VocabItemEntity(
            id = "v_3_3", lessonId = "lesson_3",
            targetWord = "хтети", targetWordLatin = "hteti",
            sourceTranslation = "хотеть", partOfSpeech = "Модальный глагол",
            exampleSentence = "Хоћу да купим ову занимљиву књигу.",
            distractorsJson = "[\"желети\", \"волети\", \"тражити\", \"нећу\"]"
        ),
        VocabItemEntity(
            id = "v_3_4", lessonId = "lesson_3",
            targetWord = "смети", targetWordLatin = "smeti",
            sourceTranslation = "сметь / разрешено", partOfSpeech = "Модальный глагол",
            exampleSentence = "Овде не смете да паркирате аутомобил.",
            distractorsJson = "[\"моћи\", \"морати\", \"требати\", \"смем\"]"
        ),
        VocabItemEntity(
            id = "v_3_5", lessonId = "lesson_3",
            targetWord = "састанак", targetWordLatin = "sastanak",
            sourceTranslation = "встреча / собрание", partOfSpeech = "Существительное",
            exampleSentence = "Имамо важан пословни састанак у подне.",
            distractorsJson = "[\"разговор\", \"предавање\", \"договор\", \"састанци\"]"
        ),
        VocabItemEntity(
            id = "v_3_6", lessonId = "lesson_3",
            targetWord = "одмах", targetWordLatin = "odmah",
            sourceTranslation = "сейчас же / сразу", partOfSpeech = "Наречие",
            exampleSentence = "Морамо одмах да позовемо такси.",
            distractorsJson = "[\"касније\", \"сутра\", \"јуче\", \"брзо\"]"
        ),
        VocabItemEntity("v_4_1", "lesson_4", "јуче", "juče", "вчера", "Наречие", distractorsJson = "[\"данас\",\"сутра\",\"увек\"]"),
        VocabItemEntity("v_5_1", "lesson_5", "сутра", "sutra", "завтра", "Наречие", distractorsJson = "[\"јуче\",\"данас\",\"сада\"]"),
        VocabItemEntity("v_6_1", "lesson_6", "написати", "napisati", "написать", "Глагол", distractorsJson = "[\"писати\",\"читати\",\"рећи\"]"),
        VocabItemEntity("v_7_1", "lesson_7", "помоћи", "pomoći", "помочь", "Глагол", distractorsJson = "[\"радити\",\"учити\",\"дати\"]"),
        VocabItemEntity("v_8_1", "lesson_8", "желети", "želeti", "желать", "Глагол", distractorsJson = "[\"моћи\",\"имати\",\"знати\"]"),
        VocabItemEntity("v_9_1", "lesson_9", "доћи", "doći", "прийти", "Глагол", distractorsJson = "[\"отићи\",\"ићи\",\"ући\"]"),
        VocabItemEntity("v_10_1", "lesson_10", "поклон", "poklon", "подарок", "Существительное", distractorsJson = "[\"писмо\",\"књига\",\"посао\"]"),
        VocabItemEntity("v_11_1", "lesson_11", "град", "grad", "город", "Существительное", distractorsJson = "[\"село\",\"улица\",\"кућа\"]"),
        VocabItemEntity("v_12_1", "lesson_12", "наш", "naš", "наш", "Местоимение", distractorsJson = "[\"мој\",\"твој\",\"ваш\"]"),
        VocabItemEntity("v_13_1", "lesson_13", "бољи", "bolji", "лучше", "Прилагательное", distractorsJson = "[\"гори\",\"већи\",\"мањи\"]"),
        VocabItemEntity("v_14_1", "lesson_14", "зато што", "zato što", "потому что", "Союз", distractorsJson = "[\"иако\",\"док\",\"кад\"]"),
        VocabItemEntity("v_15_1", "lesson_15", "требати", "trebati", "быть нужным", "Глагол", distractorsJson = "[\"морати\",\"моћи\",\"смети\"]"),
        VocabItemEntity("v_16_1", "lesson_16", "разумевање", "razumevanje", "понимание", "Существительное", distractorsJson = "[\"питање\",\"одговор\",\"знање\"]")
    )

    fun getInitialGrammarGuides(): List<GrammarGuideEntity> {
        val lesson1Matrix = ConjugationMatrixData(
            verbInfinitive = "Радити",
            verbInfinitiveLatin = "Raditi",
            verbTranslation = "Работать / Делать",
            aspect = "Несвршени (Imperfective)",
            cells = listOf(
                ConjugationCell(
                    formType = "Affirmative (+)",
                    tense = "Past",
                    formula = "Радио/Радила сам (ja)",
                    exampleSerbian = "Ја сам јуче дуго радио.",
                    exampleSerbianLatin = "Ja sam juče dugo radio.",
                    exampleRussian = "Я вчера долго работал.",
                    notes = "Энклитика 'сам' ставится сразу после ударного первого слова."
                ),
                ConjugationCell(
                    formType = "Affirmative (+)",
                    tense = "Present",
                    formula = "Радим, радиш, ради, радимо, радите, раде",
                    exampleSerbian = "Ја радим у овој компанији.",
                    exampleSerbianLatin = "Ja radim u ovoj kompaniji.",
                    exampleRussian = "Я работаю в этой компании.",
                    notes = "Глаголы на -ити в презенте имеют окончания -им, -иш, -и..."
                ),
                ConjugationCell(
                    formType = "Affirmative (+)",
                    tense = "Future",
                    formula = "Радићу или Ја ћу радити",
                    exampleSerbian = "Сутра ћу радити од куће.",
                    exampleSerbianLatin = "Sutra ću raditi od kuće.",
                    exampleRussian = "Завтра я буду работать из дома.",
                    notes = "Если местоимение опущено: Радићу. Если стоит впереди: Ја ћу радити."
                ),
                ConjugationCell(
                    formType = "Negative (-)",
                    tense = "Past",
                    formula = "Нисам радио/радила",
                    exampleSerbian = "Нисам радио ништа лоше.",
                    exampleSerbianLatin = "Nisam radio ništa loše.",
                    exampleRussian = "Я не делал ничего плохого.",
                    notes = "Отрицательные формы: нисам, ниси, није, нисмо, нисте, нису."
                ),
                ConjugationCell(
                    formType = "Negative (-)",
                    tense = "Present",
                    formula = "Не радим, не радиш, не ради...",
                    exampleSerbian = "Данас не радим, имам слободан дан.",
                    exampleSerbianLatin = "Danas ne radim, imam slobodan dan.",
                    exampleRussian = "Сегодня я не работаю, у меня выходной.",
                    notes = "Частица 'не' пишется раздельно с глаголом (кроме нећу, немам, нисам, немој)."
                ),
                ConjugationCell(
                    formType = "Negative (-)",
                    tense = "Future",
                    formula = "Нећу радити",
                    exampleSerbian = "Нећу радити за викенд.",
                    exampleSerbianLatin = "Neću raditi za vikend.",
                    exampleRussian = "Я не буду работать на выходных.",
                    notes = "Формы: нећу, нећеш, неће, нећемо, нећете, неће + инфинитив."
                ),
                ConjugationCell(
                    formType = "Interrogative (?)",
                    tense = "Past",
                    formula = "Да ли си радио? / Јеси ли радио?",
                    exampleSerbian = "Да ли си радио на овом пројекту?",
                    exampleSerbianLatin = "Da li si radio na ovom projektu?",
                    exampleRussian = "Ты работал над этим проектом?",
                    notes = "Вопрос строится через 'Да ли + краткая связка' или полная форма + ли."
                ),
                ConjugationCell(
                    formType = "Interrogative (?)",
                    tense = "Present",
                    formula = "Да ли радиш? / Радиш ли?",
                    exampleSerbian = "Да ли радиш у Београду?",
                    exampleSerbianLatin = "Da li radiš u Beogradu?",
                    exampleRussian = "Ты работаешь в Белграде?",
                    notes = "Две равноправные модели вопроса: Да ли + глагол или Глагол + ли."
                ),
                ConjugationCell(
                    formType = "Interrogative (?)",
                    tense = "Future",
                    formula = "Да ли ћеш радити? / Хоћеш ли радити?",
                    exampleSerbian = "Хоћеш ли сутра радити са нама?",
                    exampleSerbianLatin = "Hoćeš li sutra raditi sa nama?",
                    exampleRussian = "Будешь ли ты завтра работать с нами?",
                    notes = "Вопрос: Хоћеш ли радити? или Да ли ћеш радити?"
                )
            )
        )

        return listOf(
            GrammarGuideEntity(
                id = "g_1",
                lessonId = "lesson_1",
                title = "Матрица Полиглота: Глагол 'Радити' (3x3)",
                summary = "Фундаментальная сетка 3x3: 3 времени × 3 формы предложения",
                explanationMarkdown = """
                    ### Базовая структура сербского глагола (Метод Полиглот 16)
                    В сербском языке глагол спрягается по лицам, числам и временам.
                    
                    **Ключевые отличия от русского:**
                    1. Личные местоимения (*ја, ти, он, ми, ви, они*) часто опускаются.
                    2. Краткие безударные связки (*сам, си, је...* и *ћу, ћеш, ће*) стоят строго на втором месте.
                    3. Вопрос строится через «Да ли» (*Да ли радиш?*) или инверсией (*Радиш ли?*).
                """.trimIndent(),
                matrixJson = matrixAdapter.toJson(lesson1Matrix)
            ),
            GrammarGuideEntity(
                id = "g_2",
                lessonId = "lesson_2",
                title = "Возвратные глаголы с 'СЕ'",
                summary = "Частица 'се' пишется раздельно и занимает строго второе место в предложении",
                explanationMarkdown = """
                    ### Повратни глаголи у српском језику
                    В сербском языке клитика **се** пишется отдельно от глагола:
                    - *Ја **се** надам* или *Надам **се***.
                    
                    Клитика **се** ставится сразу после первого ударного слова в предложении.
                """.trimIndent(),
                matrixJson = "{}"
            ),
            GrammarGuideEntity(
                id = "g_3",
                lessonId = "lesson_3",
                title = "Модальные глаголы и конструкция 'ДА + Презент'",
                summary = "Вместо инфинитива в сербском используется 'да + личная форма глагола'",
                explanationMarkdown = """
                    ### Конструкция: Модальный глагол + ДА + Презент
                    - *Ја хоћу **да радим**.* (Я хочу работать.)
                    - *Ти мораш **да учиш**.* (Ты должен учиться.)
                    - *Ми можемо **да помогнемо**.* (Мы можем помочь.)
                """.trimIndent(),
                matrixJson = "{}"
            )
        ) + (4..16).map { idx ->
            GrammarGuideEntity(
                id = "g_$idx",
                lessonId = "lesson_$idx",
                title = "Грамматическая матрица урока $idx",
                summary = "Ключевые формы и согласование местоимений для урока $idx",
                explanationMarkdown = "### Урок $idx\nИспользуйте 3x3 матрицу времён и форм (Past / Present / Future × Affirmative / Negative / Interrogative).",
                matrixJson = "{}"
            )
        }
    }

    // All tokens and distractors are provided in inner case (lowercase)
    // with correct and wrong morphological variants
    private fun getExplicitInitialSentences(): List<SentenceEntity> = listOf(
        // Lesson 1
        SentenceEntity(
            id = "s_1_1",
            lessonId = "lesson_1",
            sourceSentence = "Ты работаешь сегодня в офисе?",
            targetSentence = "Да ли данас радиш у канцеларији?",
            targetSentenceLatin = "Da li danas radiš u kancelariji?",
            tokensJson = "[\"да\", \"ли\", \"данас\", \"радиш\", \"у\", \"канцеларији\"]",
            distractorTokensJson = "[\"радим\", \"раде\", \"радите\", \"јеси\", \"радио\"]",
            grammarHint = "Вопрос в настоящем времени: 'да ли' + глагол во 2 лице (радиш)",
            matrixTense = "PRESENT",
            matrixForm = "INTERROGATIVE",
            personGroup = "2SG_PL"
        ),
        SentenceEntity(
            id = "s_1_2",
            lessonId = "lesson_1",
            sourceSentence = "Я не говорю по-английски быстро.",
            targetSentence = "Ја не говорим енглески брзо.",
            targetSentenceLatin = "Ja ne govorim engleski brzo.",
            tokensJson = "[\"ја\", \"не\", \"говорим\", \"енглески\", \"брзо\"]",
            distractorTokensJson = "[\"говориш\", \"говори\", \"говоримо\", \"нисам\", \"прича\"]",
            grammarHint = "Отрицание в презенте: частица 'не' перед глаголом 1 лица (говорим)",
            matrixTense = "PRESENT",
            matrixForm = "NEGATIVE",
            personGroup = "1SG"
        ),
        SentenceEntity(
            id = "s_1_3",
            lessonId = "lesson_1",
            sourceSentence = "Завтра я буду работать дома.",
            targetSentence = "Сутра ћу радити код куће.",
            targetSentenceLatin = "Sutra ću raditi kod kuće.",
            tokensJson = "[\"сутра\", \"ћу\", \"радити\", \"код\", \"куће\"]",
            distractorTokensJson = "[\"ћеш\", \"ће\", \"бићу\", \"кућа\", \"кућу\"]",
            grammarHint = "Футур I: энклитика «ћу» после первого слова",
            matrixTense = "FUTURE",
            matrixForm = "AFFIRMATIVE",
            personGroup = "1SG"
        ),
        SentenceEntity(
            id = "s_1_4",
            lessonId = "lesson_1",
            sourceSentence = "Мы вчера внимательно слушали профессора.",
            targetSentence = "Ми смо јуче пажљиво слушали професора.",
            targetSentenceLatin = "Mi smo juče pažljivo slušali profesora.",
            tokensJson = "[\"ми\", \"смо\", \"јуче\", \"пажљиво\", \"слушали\", \"професора\"]",
            distractorTokensJson = "[\"сте\", \"су\", \"слушао\", \"слушамо\", \"слушате\"]",
            grammarHint = "Перфекат: форма множественного числа «смо слушали»",
            matrixTense = "PAST",
            matrixForm = "AFFIRMATIVE",
            personGroup = "2SG_PL"
        ),

        // Lesson 2
        SentenceEntity(
            id = "s_2_1",
            lessonId = "lesson_2",
            sourceSentence = "Я хорошо помню этот красивый город.",
            targetSentence = "Добро се сећам овог лепог града.",
            targetSentenceLatin = "Dobro se sećam ovog lepog grada.",
            tokensJson = "[\"добро\", \"се\", \"сећам\", \"овог\", \"лепог\", \"града\"]",
            distractorTokensJson = "[\"сећаш\", \"сећа\", \"граду\", \"град\", \"си\"]",
            grammarHint = "Частица 'се' на втором месте: добро се сећам",
            matrixTense = "PRESENT",
            matrixForm = "AFFIRMATIVE",
            personGroup = "1SG"
        ),
        SentenceEntity(
            id = "s_2_2",
            lessonId = "lesson_2",
            sourceSentence = "Каким спортом ты занимаешься?",
            targetSentence = "Којим спортом се бавиш?",
            targetSentenceLatin = "Kojim sportom se baviš?",
            tokensJson = "[\"којим\", \"спортом\", \"се\", \"бавиш\"]",
            distractorTokensJson = "[\"бавим\", \"бави\", \"баве\", \"спорт\", \"ти\"]",
            grammarHint = "Глагол 'бавити се' требует творительного падежа",
            matrixTense = "PRESENT",
            matrixForm = "INTERROGATIVE",
            personGroup = "2SG_PL"
        ),

        // Lesson 3
        SentenceEntity(
            id = "s_3_1",
            lessonId = "lesson_3",
            sourceSentence = "Я хочу говорить по-сербски свободно.",
            targetSentence = "Ја хоћу да говорим српски течно.",
            targetSentenceLatin = "Ja hoću da govorim srpski tečno.",
            tokensJson = "[\"ја\", \"хоћу\", \"да\", \"говорим\", \"српски\", \"течно\"]",
            distractorTokensJson = "[\"нећу\", \"хоћеш\", \"хоће\", \"говорити\", \"могу\"]",
            grammarHint = "Конструкция: хоћу + ДА + презенс (говорим)",
            matrixTense = "PRESENT",
            matrixForm = "AFFIRMATIVE",
            personGroup = "1SG"
        ),
        SentenceEntity(
            id = "s_3_2",
            lessonId = "lesson_3",
            sourceSentence = "Мы должны сразу позвонить врачу.",
            targetSentence = "Морамо одмах да позовемо лекара.",
            targetSentenceLatin = "Moramo odmah da pozovemo lekara.",
            tokensJson = "[\"морамо\", \"одмах\", \"да\", \"позовемо\", \"лекара\"]",
            distractorTokensJson = "[\"морам\", \"морате\", \"позвати\", \"лекар\", \"можемо\"]",
            grammarHint = "Морамо + ДА + глагол совершенного вида (позовемо)",
            matrixTense = "PRESENT",
            matrixForm = "AFFIRMATIVE",
            personGroup = "2SG_PL"
        )
    )

    private data class MatrixSentenceTemplate(
        val tense: String,
        val form: String,
        val personGroup: String,
        val ru: String,
        val sr: String,
        val srLat: String,
        val tokens: String,
        val distractors: String,
        val hint: String
    )

    private val nineMatrixTemplates = listOf(
        MatrixSentenceTemplate("PAST", "AFFIRMATIVE", "1SG", "Я вчера работал.", "Ја сам јуче радио.", "Ja sam juče radio.", "[\"ја\",\"сам\",\"јуче\",\"радио\"]", "[\"си\",\"радим\"]", "Перфекат (+): ја сам радио"),
        MatrixSentenceTemplate("PAST", "NEGATIVE", "1SG", "Я вчера не работал.", "Ја нисам јуче радио.", "Ja nisam juče radio.", "[\"ја\",\"нисам\",\"јуче\",\"радио\"]", "[\"сам\",\"не\"]", "Перфекат (-): нисам радио"),
        MatrixSentenceTemplate("PAST", "INTERROGATIVE", "2SG_PL", "Ты вчера работал?", "Да ли си јуче радио?", "Da li si juče radio?", "[\"да\",\"ли\",\"си\",\"јуче\",\"радио\"]", "[\"сам\",\"радим\"]", "Перфекат (?): да ли си радио"),
        MatrixSentenceTemplate("PRESENT", "AFFIRMATIVE", "1SG", "Я сегодня работаю.", "Ја данас радим.", "Ja danas radim.", "[\"ја\",\"данас\",\"радим\"]", "[\"радиш\",\"радио\"]", "Презент (+): радим"),
        MatrixSentenceTemplate("PRESENT", "NEGATIVE", "1SG", "Я сегодня не работаю.", "Ја данас не радим.", "Ja danas ne radim.", "[\"ја\",\"данас\",\"не\",\"радим\"]", "[\"нисам\",\"нећу\"]", "Презент (-): не радим"),
        MatrixSentenceTemplate("PRESENT", "INTERROGATIVE", "2SG_PL", "Ты сегодня работаешь?", "Да ли данас радиш?", "Da li danas radiš?", "[\"да\",\"ли\",\"данас\",\"радиш\"]", "[\"радим\",\"си\"]", "Презент (?): да ли радиш"),
        MatrixSentenceTemplate("FUTURE", "AFFIRMATIVE", "1SG", "Я завтра буду работать.", "Ја ћу сутра радити.", "Ja ću sutra raditi.", "[\"ја\",\"ћу\",\"сутра\",\"радити\"]", "[\"нећу\",\"радим\"]", "Футур I (+): ћу радити"),
        MatrixSentenceTemplate("FUTURE", "NEGATIVE", "1SG", "Я завтра не буду работать.", "Ја сутра нећу радити.", "Ja sutra neću raditi.", "[\"ја\",\"сутра\",\"нећу\",\"радити\"]", "[\"ћу\",\"нисам\"]", "Футур I (-): нећу радити"),
        MatrixSentenceTemplate("FUTURE", "INTERROGATIVE", "2SG_PL", "Ты завтра будешь работать?", "Да ли ћеш сутра радити?", "Da li ćeš sutra raditi?", "[\"да\",\"ли\",\"ћеш\",\"сутра\",\"радити\"]", "[\"ћу\",\"си\"]", "Футур I (?): да ли ћеш радити")
    )

    fun getInitialSentences(): List<SentenceEntity> {
        val explicit = getExplicitInitialSentences()
        val generated = (1..16).flatMap { idx ->
            val lessonId = "lesson_$idx"
            val existingCoords = explicit.filter { it.lessonId == lessonId }.map { it.matrixCoordinateKey() }.toSet()
            nineMatrixTemplates
                .filter { "${it.tense}_${it.form}" !in existingCoords }
                .map { t ->
                    SentenceEntity(
                        id = "s_${idx}_${t.tense.lowercase()}_${t.form.lowercase()}",
                        lessonId = lessonId,
                        sourceSentence = t.ru,
                        targetSentence = t.sr,
                        targetSentenceLatin = t.srLat,
                        tokensJson = t.tokens,
                        distractorTokensJson = t.distractors,
                        grammarHint = t.hint,
                        matrixTense = t.tense,
                        matrixForm = t.form,
                        personGroup = t.personGroup
                    )
                }
        }
        return explicit + generated
    }
}
