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
        )
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
        )
    }

    // All tokens and distractors are provided in inner case (lowercase)
    // with correct and wrong morphological variants
    fun getInitialSentences(): List<SentenceEntity> = listOf(
        // Lesson 1
        SentenceEntity(
            id = "s_1_1",
            lessonId = "lesson_1",
            sourceSentence = "Ты работаешь сегодня в офисе?",
            targetSentence = "Да ли данас радиш у канцеларији?",
            targetSentenceLatin = "Da li danas radiš u kancelariji?",
            tokensJson = "[\"да\", \"ли\", \"данас\", \"радиш\", \"у\", \"канцеларији\"]",
            // Distractors: wrong grammatical variants (радим, раде, радите, јеси)
            distractorTokensJson = "[\"радим\", \"раде\", \"радите\", \"јеси\", \"радио\"]",
            grammarHint = "Вопрос в настоящем времени: 'да ли' + глагол во 2 лице (радиш)"
        ),
        SentenceEntity(
            id = "s_1_2",
            lessonId = "lesson_1",
            sourceSentence = "Я не говорю по-английски быстро.",
            targetSentence = "Ја не говорим енглески брзо.",
            targetSentenceLatin = "Ja ne govorim engleski brzo.",
            tokensJson = "[\"ја\", \"не\", \"говорим\", \"енглески\", \"брзо\"]",
            // Distractors: wrong person and tense variants (говориш, говори, прича)
            distractorTokensJson = "[\"говориш\", \"говори\", \"говоримо\", \"нисам\", \"прича\"]",
            grammarHint = "Отрицание в презенте: частица 'не' перед глаголом 1 лица (говорим)"
        ),
        SentenceEntity(
            id = "s_1_3",
            lessonId = "lesson_1",
            sourceSentence = "Завтра я буду работать дома.",
            targetSentence = "Сутра ћу радити код куће.",
            targetSentenceLatin = "Sutra ću raditi kod kuće.",
            tokensJson = "[\"сутра\", \"ћу\", \"радити\", \"код\", \"куће\"]",
            // Distractors: wrong auxiliary / tense variants (ћеш, ће, бићу, кућа)
            distractorTokensJson = "[\"ћеш\", \"ће\", \"бићу\", \"кућа\", \"кућу\"]",
            grammarHint = "Футур I: энклитика «ћу» после первого слова"
        ),
        SentenceEntity(
            id = "s_1_4",
            lessonId = "lesson_1",
            sourceSentence = "Мы вчера внимательно слушали профессора.",
            targetSentence = "Ми смо јуче пажљиво слушали професора.",
            targetSentenceLatin = "Mi smo juče pažljivo slušali profesora.",
            tokensJson = "[\"ми\", \"смо\", \"јуче\", \"пажљиво\", \"слушали\", \"професора\"]",
            // Distractors: wrong auxiliary / participle agreement variants (сте, су, слушао, слушамо)
            distractorTokensJson = "[\"сте\", \"су\", \"слушао\", \"слушамо\", \"слушате\"]",
            grammarHint = "Перфекат: форма множественного числа «смо слушали»"
        ),

        // Lesson 2
        SentenceEntity(
            id = "s_2_1",
            lessonId = "lesson_2",
            sourceSentence = "Я хорошо помню этот красивый город.",
            targetSentence = "Добро се сећам овог лепог града.",
            targetSentenceLatin = "Dobro se sećam ovog lepog grada.",
            tokensJson = "[\"добро\", \"се\", \"сећам\", \"овог\", \"лепог\", \"града\"]",
            // Distractors: wrong person/case variants (сећаш, сећа, граду, град)
            distractorTokensJson = "[\"сећаш\", \"сећа\", \"граду\", \"град\", \"си\"]",
            grammarHint = "Частица 'се' на втором месте: добро се сећам"
        ),
        SentenceEntity(
            id = "s_2_2",
            lessonId = "lesson_2",
            sourceSentence = "Каким спортом ты занимаешься?",
            targetSentence = "Којим спортом се бавиш?",
            targetSentenceLatin = "Kojim sportom se baviš?",
            tokensJson = "[\"којим\", \"спортом\", \"се\", \"бавиш\"]",
            // Distractors: wrong verb variants (бавим, бави, баве)
            distractorTokensJson = "[\"бавим\", \"бави\", \"баве\", \"спорт\", \"ти\"]",
            grammarHint = "Глагол 'бавити се' требует творительного падежа"
        ),

        // Lesson 3
        SentenceEntity(
            id = "s_3_1",
            lessonId = "lesson_3",
            sourceSentence = "Я хочу говорить по-сербски свободно.",
            targetSentence = "Ја хоћу да говорим српски течно.",
            targetSentenceLatin = "Ja hoću da govorim srpski tečno.",
            tokensJson = "[\"ја\", \"хоћу\", \"да\", \"говорим\", \"српски\", \"течно\"]",
            // Distractors: wrong modal variants (нећу, хоћеш, хоће, говорити)
            distractorTokensJson = "[\"нећу\", \"хоћеш\", \"хоће\", \"говорити\", \"могу\"]",
            grammarHint = "Конструкция: хоћу + ДА + презенс (говорим)"
        ),
        SentenceEntity(
            id = "s_3_2",
            lessonId = "lesson_3",
            sourceSentence = "Мы должны сразу позвонить врачу.",
            targetSentence = "Морамо одмах да позовемо лекара.",
            targetSentenceLatin = "Moramo odmah da pozovemo lekara.",
            tokensJson = "[\"морамо\", \"одмах\", \"да\", \"позовемо\", \"лекара\"]",
            // Distractors: wrong verb forms (морам, морате, позвати, лекар)
            distractorTokensJson = "[\"морам\", \"морате\", \"позвати\", \"лекар\", \"можемо\"]",
            grammarHint = "Морамо + ДА + глагол совершенного вида (позовемо)"
        )
    )
}
