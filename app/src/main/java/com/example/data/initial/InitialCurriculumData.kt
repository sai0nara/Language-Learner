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
            title = "Урок 2: Возвратные глаголы с частицей 'SE'",
            subtitle = "Sećati se, nadati se, baviti se. Позиция энклитик",
            grammarFocus = "Возвратные глаголы (se)",
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
            title = "Урок 3: Модальные конструкции (Hteti / Moći / Morati)",
            subtitle = "Замена инфинитива союзом 'DA + Презент'",
            grammarFocus = "Модальные глаголы + DA",
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
            subtitle = "Вспомогательный глагол 'biti' + л-форма причастия",
            grammarFocus = "Перфекат (Prošlo vreme)",
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
            subtitle = "Краткие формы глагола 'hteti': ću, ćeš, će / radiću vs ću raditi",
            grammarFocus = "Футур I (Buduće vreme)",
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
            subtitle = "Совершенный и несовершенный вид (pisati/napisati, kupovati/kupiti)",
            grammarFocus = "Глагольный вид (Аспект)",
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
            subtitle = "Императив: radi! radite! nemoj da radiš!",
            grammarFocus = "Императив", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_8", orderIndex = 8,
            title = "Урок 8: Условное наклонение",
            subtitle = "Потенциал: bih, bi, bismo, biste + radni pridev",
            grammarFocus = "Потенцијал", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_9", orderIndex = 9,
            title = "Урок 9: Глаголы движения",
            subtitle = "Ići, doći, otići, ući, izaći с предлогами",
            grammarFocus = "Глаголы движения", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_10", orderIndex = 10,
            title = "Урок 10: Дательный падеж и кому/чему",
            subtitle = "Личные местоимения и клитики mi, ti, mu, joj, nam",
            grammarFocus = "Датив (Дательный падеж)", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_11", orderIndex = 11,
            title = "Урок 11: Винительный и локатив",
            subtitle = "Куда vs Где (u grad / u gradu)",
            grammarFocus = "Акузатив и Локатив", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_12", orderIndex = 12,
            title = "Урок 12: Притяжательные местоимения",
            subtitle = "Moj, tvoj, njegov, njen, naš, vaš, njihov",
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
            subtitle = "Zato što, iako, dok, čim, pošto",
            grammarFocus = "Придаточные предложения", level = "B1",
            vocabCount = 1, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_15", orderIndex = 15,
            title = "Урок 15: Пассив и безличные конструкции",
            subtitle = "Ovde se govori srpski / Treba da...",
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
            exampleSentence = "Svakog dana vredno radim u kancelariji.",
            distractorsJson = "[\"učiti\", \"putovati\", \"spavati\", \"radim\"]"
        ),
        VocabItemEntity(
            id = "v_1_2", lessonId = "lesson_1",
            targetWord = "говорити", targetWordLatin = "govoriti",
            sourceTranslation = "говорить / разговаривать", partOfSpeech = "Глагол",
            exampleSentence = "Da li dobro govorite srpski jezik?",
            distractorsJson = "[\"videti\", \"čitati\", \"pisati\", \"govorimo\"]"
        ),
        VocabItemEntity(
            id = "v_1_3", lessonId = "lesson_1",
            targetWord = "разумети", targetWordLatin = "razumeti",
            sourceTranslation = "понимать", partOfSpeech = "Глагол",
            exampleSentence = "Sve savršeno razumem kad pričate sporo.",
            distractorsJson = "[\"misliti\", \"znati\", \"čuti\", \"razume\"]"
        ),
        VocabItemEntity(
            id = "v_1_4", lessonId = "lesson_1",
            targetWord = "питање", targetWordLatin = "pitanje",
            sourceTranslation = "вопрос", partOfSpeech = "Существительное",
            exampleSentence = "Imam jedno važno pitanje za profesora.",
            distractorsJson = "[\"odgovor\", \"reč\", \"rečenica\", \"pitanja\"]"
        ),
        VocabItemEntity(
            id = "v_1_5", lessonId = "lesson_1",
            targetWord = "одговор", targetWordLatin = "odgovor",
            sourceTranslation = "ответ", partOfSpeech = "Существительное",
            exampleSentence = "Čekam tačan odgovor na svoju poruku.",
            distractorsJson = "[\"pitanje\", \"razgovor\", \"knjiga\", \"odgovori\"]"
        ),
        VocabItemEntity(
            id = "v_1_6", lessonId = "lesson_1",
            targetWord = "пажљиво", targetWordLatin = "pažljivo",
            sourceTranslation = "внимательно", partOfSpeech = "Наречие",
            exampleSentence = "Molim vas, pažljivo slušajte uputstva.",
            distractorsJson = "[\"brzo\", \"glasno\", \"sporo\", \"pažljiv\"]"
        ),

        // Lesson 2
        VocabItemEntity(
            id = "v_2_1", lessonId = "lesson_2",
            targetWord = "сећати се", targetWordLatin = "sećati se",
            sourceTranslation = "помнить / вспоминать", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Dobro se sećam tog lepog dana.",
            distractorsJson = "[\"smejati se\", \"nadati se\", \"baviti se\", \"sećam\"]"
        ),
        VocabItemEntity(
            id = "v_2_2", lessonId = "lesson_2",
            targetWord = "надати се", targetWordLatin = "nadati se",
            sourceTranslation = "надеяться", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Nadamo se da će vreme sutra biti sunčano.",
            distractorsJson = "[\"plašiti se\", \"čuditi se\", \"ljutiti se\", \"nada\"]"
        ),
        VocabItemEntity(
            id = "v_2_3", lessonId = "lesson_2",
            targetWord = "бавити се", targetWordLatin = "baviti se",
            sourceTranslation = "заниматься (спортом/делом)", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Kojim sportom se obično bavite?",
            distractorsJson = "[\"dopadati se\", \"odmarati se\", \"nalaziti se\", \"bavimo\"]"
        ),
        VocabItemEntity(
            id = "v_2_4", lessonId = "lesson_2",
            targetWord = "смејати се", targetWordLatin = "smejati se",
            sourceTranslation = "смеяться", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Svi se glasno smeju njegovoj šali.",
            distractorsJson = "[\"plakati\", \"ljutiti se\", \"stideti se\", \"smeh\"]"
        ),
        VocabItemEntity(
            id = "v_2_5", lessonId = "lesson_2",
            targetWord = "допадати се", targetWordLatin = "dopadati se",
            sourceTranslation = "нравиться", partOfSpeech = "Возвратный глагол",
            exampleSentence = "Mnogo mi se dopada Beograd uveče.",
            distractorsJson = "[\"smetati\", \"pomagati\", \"koristiti\", \"dopao\"]"
        ),
        VocabItemEntity(
            id = "v_2_6", lessonId = "lesson_2",
            targetWord = "навика", targetWordLatin = "navika",
            sourceTranslation = "привычка", partOfSpeech = "Существительное",
            exampleSentence = "Jutarnje učenje je moja najbolja navika.",
            distractorsJson = "[\"odluka\", \"pravilo\", \"osobina\", \"navike\"]"
        ),

        // Lesson 3
        VocabItemEntity(
            id = "v_3_1", lessonId = "lesson_3",
            targetWord = "морати", targetWordLatin = "morati",
            sourceTranslation = "быть должным / обязанным", partOfSpeech = "Модальный глагол",
            exampleSentence = "Danas moram da završim sav posao.",
            distractorsJson = "[\"moći\", \"hteti\", \"smeti\", \"moramo\"]"
        ),
        VocabItemEntity(
            id = "v_3_2", lessonId = "lesson_3",
            targetWord = "моћи", targetWordLatin = "moći",
            sourceTranslation = "мочь / иметь возможность", partOfSpeech = "Модальный глагол",
            exampleSentence = "Mogu li da dobijem čašu vode, molim vas?",
            distractorsJson = "[\"morati\", \"znati\", \"trebati\", \"možemo\"]"
        ),
        VocabItemEntity(
            id = "v_3_3", lessonId = "lesson_3",
            targetWord = "хтети", targetWordLatin = "hteti",
            sourceTranslation = "хотеть", partOfSpeech = "Модальный глагол",
            exampleSentence = "Hoću da kupim ovu zanimljivu knjigu.",
            distractorsJson = "[\"želeti\", \"voleti\", \"tražiti\", \"neću\"]"
        ),
        VocabItemEntity(
            id = "v_3_4", lessonId = "lesson_3",
            targetWord = "смети", targetWordLatin = "smeti",
            sourceTranslation = "сметь / разрешено", partOfSpeech = "Модальный глагол",
            exampleSentence = "Ovde ne smete da parkirate automobil.",
            distractorsJson = "[\"moći\", \"morati\", \"trebati\", \"smem\"]"
        ),
        VocabItemEntity(
            id = "v_3_5", lessonId = "lesson_3",
            targetWord = "састанак", targetWordLatin = "sastanak",
            sourceTranslation = "встреча / собрание", partOfSpeech = "Существительное",
            exampleSentence = "Imamo važan poslovni sastanak u podne.",
            distractorsJson = "[\"razgovor\", \"predavanje\", \"dogovor\", \"sastanci\"]"
        ),
        VocabItemEntity(
            id = "v_3_6", lessonId = "lesson_3",
            targetWord = "одмах", targetWordLatin = "odmah",
            sourceTranslation = "сейчас же / сразу", partOfSpeech = "Наречие",
            exampleSentence = "Moramo odmah da pozovemo taksi.",
            distractorsJson = "[\"kasnije\", \"sutra\", \"juče\", \"brzo\"]"
        ),
        VocabItemEntity("v_4_1", "lesson_4", "јуче", "juče", "вчера", "Наречие", distractorsJson = "[\"danas\",\"sutra\",\"uvek\"]"),
        VocabItemEntity("v_5_1", "lesson_5", "сутра", "sutra", "завтра", "Наречие", distractorsJson = "[\"juče\",\"danas\",\"sada\"]"),
        VocabItemEntity("v_6_1", "lesson_6", "написати", "napisati", "написать", "Глагол", distractorsJson = "[\"pisati\",\"čitati\",\"reći\"]"),
        VocabItemEntity("v_7_1", "lesson_7", "помоћи", "pomoći", "помочь", "Глагол", distractorsJson = "[\"raditi\",\"učiti\",\"dati\"]"),
        VocabItemEntity("v_8_1", "lesson_8", "желети", "želeti", "желать", "Глагол", distractorsJson = "[\"moći\",\"imati\",\"znati\"]"),
        VocabItemEntity("v_9_1", "lesson_9", "доћи", "doći", "прийти", "Глагол", distractorsJson = "[\"otići\",\"ići\",\"ući\"]"),
        VocabItemEntity("v_10_1", "lesson_10", "поклон", "poklon", "подарок", "Существительное", distractorsJson = "[\"pismo\",\"knjiga\",\"posao\"]"),
        VocabItemEntity("v_11_1", "lesson_11", "град", "grad", "город", "Существительное", distractorsJson = "[\"selo\",\"ulica\",\"kuća\"]"),
        VocabItemEntity("v_12_1", "lesson_12", "наш", "naš", "наш", "Местоимение", distractorsJson = "[\"moj\",\"tvoj\",\"vaš\"]"),
        VocabItemEntity("v_13_1", "lesson_13", "бољи", "bolji", "лучше", "Прилагательное", distractorsJson = "[\"gori\",\"veći\",\"manji\"]"),
        VocabItemEntity("v_14_1", "lesson_14", "зато што", "zato što", "потому что", "Союз", distractorsJson = "[\"iako\",\"dok\",\"kad\"]"),
        VocabItemEntity("v_15_1", "lesson_15", "требати", "trebati", "быть нужным", "Глагол", distractorsJson = "[\"morati\",\"moći\",\"smeti\"]"),
        VocabItemEntity("v_16_1", "lesson_16", "разумевање", "razumevanje", "понимание", "Существительное", distractorsJson = "[\"pitanje\",\"odgovor\",\"znanje\"]")
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
                    formula = "Radio/Radila sam (ja)",
                    exampleSerbian = "Ја сам јуче дуго радио.",
                    exampleSerbianLatin = "Ja sam juče dugo radio.",
                    exampleRussian = "Я вчера долго работал.",
                    notes = "Энклитика 'sam' ставится сразу после ударного первого слова."
                ),
                ConjugationCell(
                    formType = "Affirmative (+)",
                    tense = "Present",
                    formula = "Radim, radiš, radi, radimo, radite, rade",
                    exampleSerbian = "Ја радим у овој компанији.",
                    exampleSerbianLatin = "Ja radim u ovoj kompaniji.",
                    exampleRussian = "Я работаю в этой компании.",
                    notes = "Глаголы на -iti в презенте имеют окончания -im, -iš, -i..."
                ),
                ConjugationCell(
                    formType = "Affirmative (+)",
                    tense = "Future",
                    formula = "Radiću ili Ja ću raditi",
                    exampleSerbian = "Сутра ћу радити од куће.",
                    exampleSerbianLatin = "Sutra ću raditi od kuće.",
                    exampleRussian = "Завтра я буду работать из дома.",
                    notes = "Если местоимение опущено: Radiću. Если стоит впереди: Ja ću raditi."
                ),
                ConjugationCell(
                    formType = "Negative (-)",
                    tense = "Past",
                    formula = "Nisam radio/radila",
                    exampleSerbian = "Нисам радио ништа лоше.",
                    exampleSerbianLatin = "Nisam radio ništa loše.",
                    exampleRussian = "Я не делал ничего плохого.",
                    notes = "Отрицательные формы: nisam, nisi, nije, nismo, niste, nisu."
                ),
                ConjugationCell(
                    formType = "Negative (-)",
                    tense = "Present",
                    formula = "Ne radim, ne radiš, ne radi...",
                    exampleSerbian = "Данас не радим, имам слободан дан.",
                    exampleSerbianLatin = "Danas ne radim, imam slobodan dan.",
                    exampleRussian = "Сегодня я не работаю, у меня выходной.",
                    notes = "Частица 'ne' пишется раздельно с глаголом (кроме neću, nemam, nisam, nemoj)."
                ),
                ConjugationCell(
                    formType = "Negative (-)",
                    tense = "Future",
                    formula = "Neću raditi",
                    exampleSerbian = "Нећу радити за викенд.",
                    exampleSerbianLatin = "Neću raditi za vikend.",
                    exampleRussian = "Я не буду работать на выходных.",
                    notes = "Формы: neću, nećeš, neće, nećemo, nećete, neće + инфинитив."
                ),
                ConjugationCell(
                    formType = "Interrogative (?)",
                    tense = "Past",
                    formula = "Da li si radio? / Jesi li radio?",
                    exampleSerbian = "Да ли си радио на овом пројекту?",
                    exampleSerbianLatin = "Da li si radio na ovom projektu?",
                    exampleRussian = "Ты работал над этим проектом?",
                    notes = "Вопрос строится через 'Da li + краткая связка' или полная форма + li."
                ),
                ConjugationCell(
                    formType = "Interrogative (?)",
                    tense = "Present",
                    formula = "Da li radiš? / Radiš li?",
                    exampleSerbian = "Да ли радиш у Београду?",
                    exampleSerbianLatin = "Da li radiš u Beogradu?",
                    exampleRussian = "Ты работаешь в Белграде?",
                    notes = "Две равноправные модели вопроса: Da li + глагол или Глагол + li."
                ),
                ConjugationCell(
                    formType = "Interrogative (?)",
                    tense = "Future",
                    formula = "Da li ćeš raditi? / Hoćeš li raditi?",
                    exampleSerbian = "Хоћеш ли сутра радити са нама?",
                    exampleSerbianLatin = "Hoćeš li sutra raditi sa nama?",
                    exampleRussian = "Будешь ли ты завтра работать с нами?",
                    notes = "Вопрос: Hoćeš li raditi? или Da li ćeš raditi?"
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
            tokensJson = "[\"da\", \"li\", \"danas\", \"radiš\", \"u\", \"kancelariji\"]",
            distractorTokensJson = "[\"radim\", \"rade\", \"radite\", \"jesi\", \"radio\"]",
            grammarHint = "Вопрос в настоящем времени: 'da li' + глагол во 2 лице (radiš)",
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
            tokensJson = "[\"ja\", \"ne\", \"govorim\", \"engleski\", \"brzo\"]",
            distractorTokensJson = "[\"govoriš\", \"govori\", \"govorimo\", \"nisam\", \"priča\"]",
            grammarHint = "Отрицание в презенте: частица 'ne' перед глаголом 1 лица (govorim)",
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
            tokensJson = "[\"sutra\", \"ću\", \"raditi\", \"kod\", \"kuće\"]",
            distractorTokensJson = "[\"ćeš\", \"će\", \"biću\", \"kuća\", \"kuću\"]",
            grammarHint = "Futur I: энклитика «ću» после первого слова",
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
            tokensJson = "[\"mi\", \"smo\", \"juče\", \"pažljivo\", \"slušali\", \"profesora\"]",
            distractorTokensJson = "[\"ste\", \"su\", \"slušao\", \"slušamo\", \"slušate\"]",
            grammarHint = "Perfekat: форма множественного числа «smo slušali»",
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
            tokensJson = "[\"dobro\", \"se\", \"sećam\", \"ovog\", \"lepog\", \"grada\"]",
            distractorTokensJson = "[\"sećaš\", \"seća\", \"gradu\", \"grad\", \"si\"]",
            grammarHint = "Частица 'se' на втором месте: dobro se sećam",
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
            tokensJson = "[\"kojim\", \"sportom\", \"se\", \"baviš\"]",
            distractorTokensJson = "[\"bavim\", \"bavi\", \"bave\", \"sport\", \"ti\"]",
            grammarHint = "Глагол 'baviti se' требует творительного падежа",
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
            tokensJson = "[\"ja\", \"hoću\", \"da\", \"govorim\", \"srpski\", \"tečno\"]",
            distractorTokensJson = "[\"neću\", \"hoćeš\", \"hoće\", \"govoriti\", \"mogu\"]",
            grammarHint = "Конструкция: hoću + DA + презенс (govorim)",
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
            tokensJson = "[\"moramo\", \"odmah\", \"da\", \"pozovemo\", \"lekara\"]",
            distractorTokensJson = "[\"moram\", \"morate\", \"pozvati\", \"lekar\", \"možemo\"]",
            grammarHint = "Moramo + DA + глагол совершенного вида (pozovemo)",
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
        MatrixSentenceTemplate("PAST", "AFFIRMATIVE", "1SG", "Я вчера работал.", "Ја сам јуче радио.", "Ja sam juče radio.", "[\"ja\",\"sam\",\"juče\",\"radio\"]", "[\"si\",\"radim\"]", "Perfekat (+): ja sam radio"),
        MatrixSentenceTemplate("PAST", "NEGATIVE", "1SG", "Я вчера не работал.", "Ја нисам јуче радио.", "Ja nisam juče radio.", "[\"ja\",\"nisam\",\"juče\",\"radio\"]", "[\"sam\",\"ne\"]", "Perfekat (-): nisam radio"),
        MatrixSentenceTemplate("PAST", "INTERROGATIVE", "2SG_PL", "Ты вчера работал?", "Да ли си јуче радио?", "Da li si juče radio.", "[\"da\",\"li\",\"si\",\"juče\",\"radio\"]", "[\"sam\",\"radim\"]", "Perfekat (?): da li si radio"),
        MatrixSentenceTemplate("PRESENT", "AFFIRMATIVE", "1SG", "Я сегодня работаю.", "Ја данас радим.", "Ja danas radim.", "[\"ja\",\"danas\",\"radim\"]", "[\"radiš\",\"radio\"]", "Prezent (+): radim"),
        MatrixSentenceTemplate("PRESENT", "NEGATIVE", "1SG", "Я сегодня не работаю.", "Ја данас не радим.", "Ja danas ne radim.", "[\"ja\",\"danas\",\"ne\",\"radim\"]", "[\"nisam\",\"neću\"]", "Prezent (-): ne radim"),
        MatrixSentenceTemplate("PRESENT", "INTERROGATIVE", "2SG_PL", "Ты сегодня работаешь?", "Да ли данас радиш?", "Da li danas radiš?", "[\"da\",\"li\",\"danas\",\"radiš\"]", "[\"radim\",\"si\"]", "Prezent (?): da li radiš"),
        MatrixSentenceTemplate("FUTURE", "AFFIRMATIVE", "1SG", "Я завтра буду работать.", "Ја ћу сутра радити.", "Ja ću sutra raditi.", "[\"ja\",\"ću\",\"sutra\",\"raditi\"]", "[\"neću\",\"radim\"]", "Futur I (+): ću raditi"),
        MatrixSentenceTemplate("FUTURE", "NEGATIVE", "1SG", "Я завтра не буду работать.", "Ја сутра нећу радити.", "Ja sutra neću raditi.", "[\"ja\",\"sutra\",\"neću\",\"raditi\"]", "[\"ću\",\"nisam\"]", "Futur I (-): neću raditi"),
        MatrixSentenceTemplate("FUTURE", "INTERROGATIVE", "2SG_PL", "Ты завтра будешь работать?", "Да ли ћеш сутра радити?", "Da li ćeš sutra raditi?", "[\"da\",\"li\",\"ćeš\",\"sutra\",\"raditi\"]", "[\"ću\",\"si\"]", "Futur I (?): da li ćeš raditi")
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
