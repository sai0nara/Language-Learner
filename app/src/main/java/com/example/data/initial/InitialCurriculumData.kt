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
            vocabCount = 10,
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
            vocabCount = 10,
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
            vocabCount = 10,
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
            vocabCount = 10,
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
            vocabCount = 10,
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
            vocabCount = 10,
            sentenceCount = 4
        ),
        LessonEntity(
            id = "lesson_7", orderIndex = 7,
            title = "Урок 7: Повелительное наклонение",
            subtitle = "Императив: radi! radite! nemoj da radiš!",
            grammarFocus = "Императив", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_8", orderIndex = 8,
            title = "Урок 8: Условное наклонение",
            subtitle = "Потенциал: bih, bi, bismo, biste + radni pridev",
            grammarFocus = "Потенцијал", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_9", orderIndex = 9,
            title = "Урок 9: Глаголы движения",
            subtitle = "Ići, doći, otići, ući, izaći с предлогами",
            grammarFocus = "Глаголы движения", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_10", orderIndex = 10,
            title = "Урок 10: Дательный падеж и кому/чему",
            subtitle = "Личные местоимения и клитики mi, ti, mu, joj, nam",
            grammarFocus = "Датив (Дательный падеж)", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_11", orderIndex = 11,
            title = "Урок 11: Винительный и локатив",
            subtitle = "Куда vs Где (u grad / u gradu)",
            grammarFocus = "Акузатив и Локатив", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_12", orderIndex = 12,
            title = "Урок 12: Притяжательные местоимения",
            subtitle = "Moj, tvoj, njegov, njen, naš, vaš, njihov",
            grammarFocus = "Притяжательные формы", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_13", orderIndex = 13,
            title = "Урок 13: Степени сравнения",
            subtitle = "Компаратив и суперлатив прилагательных и наречий",
            grammarFocus = "Сравнение", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_14", orderIndex = 14,
            title = "Урок 14: Сложные союзы",
            subtitle = "Zato što, iako, dok, čim, pošto",
            grammarFocus = "Придаточные предложения", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_15", orderIndex = 15,
            title = "Урок 15: Пассив и безличные конструкции",
            subtitle = "Ovde se govori srpski / Treba da...",
            grammarFocus = "Безличные конструкции", level = "B1",
            vocabCount = 10, sentenceCount = 1
        ),
        LessonEntity(
            id = "lesson_16", orderIndex = 16,
            title = "Урок 16: Итоговый синтез (Полиглот)",
            subtitle = "Свободное комбинирование времен, падежей и вопросов",
            grammarFocus = "Комплексная матрица", level = "B1",
            vocabCount = 10, sentenceCount = 1
        )
    )

    private fun v(
        id: String,
        lessonId: String,
        latin: String,
        ru: String,
        pos: String,
        example: String,
        d1: String,
        d2: String,
        d3: String
    ) = VocabItemEntity(
        id = id,
        lessonId = lessonId,
        targetWord = latin,
        targetWordLatin = latin,
        sourceTranslation = ru,
        partOfSpeech = pos,
        exampleSentence = example,
        distractorsJson = "[\"$d1\", \"$d2\", \"$d3\"]"
    )

    fun getInitialVocab(): List<VocabItemEntity> = listOf(
        // Lesson 1: Презент и базовая матрица глагола
        v("v_1_1", "lesson_1", "raditi", "работать / делать", "Глагол", "Svakog dana vredno radim u kancelariji.", "учиться", "путешествовать", "спать"),
        v("v_1_2", "lesson_1", "govoriti", "говорить / разговаривать", "Глагол", "Da li dobro govorite srpski jezik?", "видеть", "читать", "писать"),
        v("v_1_3", "lesson_1", "razumeti", "понимать", "Глагол", "Sve savršeno razumem kad pričate sporo.", "думать", "знать", "слышать"),
        v("v_1_4", "lesson_1", "pitanje", "вопрос", "Существительное", "Imam jedno važno pitanje za profesora.", "ответ", "слово", "предложение"),
        v("v_1_5", "lesson_1", "odgovor", "ответ", "Существительное", "Čekam tačan odgovor na svoju poruku.", "вопрос", "разговор", "книга"),
        v("v_1_6", "lesson_1", "pažljivo", "внимательно", "Наречие", "Molim vas, pažljivo slušajte uputstva.", "быстро", "громко", "медленно"),
        v("v_1_7", "lesson_1", "učiti", "учиться / изучать", "Глагол", "Svako jutro učim nove srpske reči.", "работать", "отдыхать", "забывать"),
        v("v_1_8", "lesson_1", "čitati", "читать", "Глагол", "Uveče volim da čitam zanimljivu knjigu.", "писать", "слушать", "смотреть"),
        v("v_1_9", "lesson_1", "pisati", "писать", "Глагол", "Pišem kratku poruku svom prijatelju.", "читать", "говорить", "спрашивать"),
        v("v_1_10", "lesson_1", "danas", "сегодня", "Наречие", "Danas imamo važan čas gramatike.", "вчера", "завтра", "всегда"),

        // Lesson 2: Возвратные глаголы с частицей 'se'
        v("v_2_1", "lesson_2", "sećati se", "помнить / вспоминать", "Возвратный глагол", "Dobro se sećam tog lepog dana.", "смеяться", "надеяться", "заниматься"),
        v("v_2_2", "lesson_2", "nadati se", "надеяться", "Возвратный глагол", "Nadamo se da će vreme sutra biti sunčano.", "бояться", "удивляться", "сердиться"),
        v("v_2_3", "lesson_2", "baviti se", "заниматься", "Возвратный глагол", "Kojim sportom se obično bavite?", "нравиться", "отдыхать", "находиться"),
        v("v_2_4", "lesson_2", "smejati se", "смеяться", "Возвратный глагол", "Svi se glasno smeju njegovoj šali.", "плакать", "сердиться", "стесняться"),
        v("v_2_5", "lesson_2", "dopadati se", "нравиться", "Возвратный глагол", "Mnogo mi se dopada Beograd uveče.", "мешать", "помогать", "казаться"),
        v("v_2_6", "lesson_2", "navika", "привычка", "Существительное", "Jutarnje učenje je moja najbolja navika.", "решение", "правило", "особенность"),
        v("v_2_7", "lesson_2", "odmarati se", "отдыхать", "Возвратный глагол", "Posle posla se odmaram kod kuće.", "уставать", "торопиться", "работать"),
        v("v_2_8", "lesson_2", "plašiti se", "бояться", "Возвратный глагол", "Ne plašim se teških zadataka.", "радоваться", "надеяться", "удивляться"),
        v("v_2_9", "lesson_2", "nalaziti se", "находиться", "Возвратный глагол", "Muzej se nalazi u centru grada.", "двигаться", "закрываться", "начинаться"),
        v("v_2_10", "lesson_2", "radovati se", "радоваться", "Возвратный глагол", "Veoma se radujem našem susretu.", "грустить", "бояться", "сомневаться"),

        // Lesson 3: Модальные конструкции (hteti / moći / morati)
        v("v_3_1", "lesson_3", "morati", "быть должным", "Модальный глагол", "Danas moram da završim sav posao.", "мочь", "хотеть", "сметь"),
        v("v_3_2", "lesson_3", "moći", "мочь / уметь", "Модальный глагол", "Mogu li da dobijem čašu vode, molim vas?", "быть должным", "знать", "хотеть"),
        v("v_3_3", "lesson_3", "hteti", "хотеть", "Модальный глагол", "Hoću da kupim ovu zanimljivu knjigu.", "желать", "любить", "искать"),
        v("v_3_4", "lesson_3", "smeti", "сметь / иметь право", "Модальный глагол", "Ovde ne smete da parkirate automobil.", "мочь", "быть должным", "нуждаться"),
        v("v_3_5", "lesson_3", "sastanak", "встреча / собрание", "Существительное", "Imamo važan poslovni sastanak u podne.", "разговор", "лекция", "договор"),
        v("v_3_6", "lesson_3", "odmah", "сейчас же / сразу", "Наречие", "Moramo odmah da pozovemo taksi.", "позже", "завтра", "вчера"),
        v("v_3_7", "lesson_3", "trebati", "быть нужным", "Модальный глагол", "Treba da krenemo na vreme.", "сметь", "любить", "помнить"),
        v("v_3_8", "lesson_3", "želeti", "желать", "Глагол", "Želim da naučim da govorim tečno.", "мочь", "забывать", "терять"),
        v("v_3_9", "lesson_3", "dozvola", "разрешение", "Существительное", "Za ulaz vam je potrebna dozvola.", "запрет", "ошибка", "причина"),
        v("v_3_10", "lesson_3", "slobodan", "свободный", "Прилагательное", "Da li ste slobodni večeras u sedam?", "занятый", "важный", "трудный"),

        // Lesson 4: Прошедшее время (Перфекат)
        v("v_4_1", "lesson_4", "juče", "вчера", "Наречие", "Juče sam ceo dan radio u biblioteci.", "сегодня", "завтра", "всегда"),
        v("v_4_2", "lesson_4", "prošle nedelje", "на прошлой неделе", "Наречие", "Prošle nedelje smo posetili Novi Sad.", "на следующей неделе", "завтра", "сейчас"),
        v("v_4_3", "lesson_4", "slušati", "слушать", "Глагол", "Pažljivo smo slušali profesora na predavanju.", "смотреть", "писать", "отвечать"),
        v("v_4_4", "lesson_4", "kupiti", "купить", "Глагол", "Kupio sam novi rečnik u knjižari.", "продать", "потерять", "подарить"),
        v("v_4_5", "lesson_4", "videti", "увидеть / видеть", "Глагол", "Juče sam video starog prijatelja.", "услышать", "сказать", "найти"),
        v("v_4_6", "lesson_4", "putovanje", "путешествие", "Существительное", "Naše putovanje po Srbiji bilo je divno.", "работа", "учеба", "собрание"),
        v("v_4_7", "lesson_4", "završiti", "закончить", "Глагол", "Završila je projekat pre roka.", "начать", "продолжить", "отменить"),
        v("v_4_8", "lesson_4", "posetiti", "посетить", "Глагол", "Posetili su istorijski muzej u centru.", "покинуть", "закрыть", "построить"),
        v("v_4_9", "lesson_4", "događaj", "событие", "Существительное", "Taj događaj je bio veoma važan za nas.", "причина", "план", "совет"),
        v("v_4_10", "lesson_4", "zaboraviti", "забыть", "Глагол", "Izvini, potpuno sam zaboravio na poziv.", "вспомнить", "узнать", "повторить"),

        // Lesson 5: Будущее время I (Футур I)
        v("v_5_1", "lesson_5", "sutra", "завтра", "Наречие", "Sutra ću raditi od kuće.", "вчера", "сегодня", "давно"),
        v("v_5_2", "lesson_5", "uskoro", "скоро", "Наречие", "Uskoro ćemo početi novi kurs.", "поздно", "вчера", "никогда"),
        v("v_5_3", "lesson_5", "putovati", "путешествовать", "Глагол", "U julu ćemo putovati na more.", "оставаться", "работать", "учиться"),
        v("v_5_4", "lesson_5", "planirati", "планировать", "Глагол", "Planiramo da otvorimo novu kancelariju.", "вспоминать", "забывать", "менять"),
        v("v_5_5", "lesson_5", "odmor", "отпуск / отдых", "Существительное", "Naš letnji odmor počinje u ponedeljak.", "работа", "экзамен", "задача"),
        v("v_5_6", "lesson_5", "pozvati", "позвать / позвонить", "Глагол", "Pozvaću te čim stignem na stanicu.", "ответить", "встретить", "проводить"),
        v("v_5_7", "lesson_5", "stići", "прибыть / успеть", "Глагол", "Voz će stići tačno na vreme.", "уехать", "опоздать", "остановиться"),
        v("v_5_8", "lesson_5", "vikend", "выходные", "Существительное", "Za vikend ćemo ići u prirodu.", "будний день", "утро", "месяц"),
        v("v_5_9", "lesson_5", "odlučiti", "решить", "Глагол", "Sutra ćemo odlučiti o sledećem koraku.", "сомневаться", "спрашивать", "ждать"),
        v("v_5_10", "lesson_5", "obećati", "пообещать", "Глагол", "Obećavam da ću redovno vežbati.", "отказать", "забыть", "промолчать"),

        // Lesson 6: Видовые пары глаголов
        v("v_6_1", "lesson_6", "napisati", "написать (сов.)", "Глагол", "Moram da napišem izveštaj do pet.", "писать", "читать", "стирать"),
        v("v_6_2", "lesson_6", "kupovati", "покупать (несов.)", "Глагол", "Subotom obično kupujem sveže voće.", "купить", "продавать", "выбирать"),
        v("v_6_3", "lesson_6", "pročitati", "прочитать (сов.)", "Глагол", "Pročitao sam ceo članak za deset minuta.", "читать", "переводить", "слушать"),
        v("v_6_4", "lesson_6", "učiti", "учить (процесс)", "Глагол", "Svaki dan učim po dvadeset minuta.", "выучить", "забыть", "преподавать"),
        v("v_6_5", "lesson_6", "naučiti", "выучить (результат)", "Глагол", "Konačno sam naučio sva pravila.", "учить", "повторять", "путать"),
        v("v_6_6", "lesson_6", "praviti", "делать / готовить (несов.)", "Глагол", "Mama pravi ukusnu večeru u kuhinji.", "сделать", "разрушить", "заказать"),
        v("v_6_7", "lesson_6", "napraviti", "сделать (сов.)", "Глагол", "Napravili smo odličnu prezentaciju.", "делать", "исправлять", "удалять"),
        v("v_6_8", "lesson_6", "otvarati", "открывать (несов.)", "Глагол", "Prodavnica se otvara u osam ujutru.", "открыть", "закрывать", "запирать"),
        v("v_6_9", "lesson_6", "otvoriti", "открыть (сов.)", "Глагол", "Molim vas, otvorite prozor u sobi.", "закрыть", "открывать", "разбить"),
        v("v_6_10", "lesson_6", "ponavljati", "повторять (несов.)", "Глагол", "Redovno ponavljamo stare lekcije.", "повторить", "забывать", "пропускать"),

        // Lesson 7: Повелительное наклонение (Императив)
        v("v_7_1", "lesson_7", "pomoći", "помочь", "Глагол", "Molim te, pomozi mi sa ovim prevodom.", "помешать", "отказать", "подождать"),
        v("v_7_2", "lesson_7", "sačekati", "подождать", "Глагол", "Sačekaj me ispred glavnog ulaza!", "уйти", "убежать", "поторопить"),
        v("v_7_3", "lesson_7", "pogledati", "посмотреть", "Глагол", "Pogledaj ovu novu mapu grada.", "отвернуться", "закрыть", "спрятать"),
        v("v_7_4", "lesson_7", "otvoriti", "открыть", "Глагол", "Otvorite knjige na dvanaestoj strani.", "закрыть", "спрятать", "убрать"),
        v("v_7_5", "lesson_7", "zatvoriti", "закрыть", "Глагол", "Zatvori vrata kad izađeš iz sobe.", "открыть", "сломать", "оставить"),
        v("v_7_6", "lesson_7", "ponoviti", "повторить", "Глагол", "Ponovite ovu rečenicu još jednom.", "забыть", "промолчать", "исправить"),
        v("v_7_7", "lesson_7", "krenuti", "отправиться / тронуться", "Глагол", "Krenimo odmah da ne bismo zakasnili!", "остановиться", "вернуться", "остаться"),
        v("v_7_8", "lesson_7", "paziti", "быть осторожным", "Глагол", "Pazi na stepenice kad silaziš!", "рисковать", "спешить", "прыгать"),
        v("v_7_9", "lesson_7", "poslati", "отправить", "Глагол", "Pošalji mi adresu porukom.", "получить", "удалить", "сохранить"),
        v("v_7_10", "lesson_7", "ostati", "остаться", "Глагол", "Ostanite još malo na kafi.", "уйти", "прийти", "уехать"),

        // Lesson 8: Условное наклонение (Потенциал)
        v("v_8_1", "lesson_8", "želeti", "хотеть / желать", "Глагол", "Želeo bih da naručim domaću kafu.", "мочь", "знать", "иметь"),
        v("v_8_2", "lesson_8", "voleti", "любить / хотеть (бы)", "Глагол", "Voleo bih da posetim Kotor na leto.", "ненавидеть", "бояться", "избегать"),
        v("v_8_3", "lesson_8", "preporučiti", "порекомендовать", "Глагол", "Šta biste nam preporučili za ručak?", "запретить", "отменить", "критиковать"),
        v("v_8_4", "lesson_8", "predložiti", "предложить", "Глагол", "Predložio bih da počnemo ranije.", "отказаться", "возразить", "запретить"),
        v("v_8_5", "lesson_8", "savet", "совет", "Существительное", "Vaš savet bi mi mnogo pomogao.", "приказ", "жалоба", "вопрос"),
        v("v_8_6", "lesson_8", "prilika", "возможность / шанс", "Существительное", "Iskoristio bih ovu priliku za razgovor.", "препятствие", "ошибка", "причина"),
        v("v_8_7", "lesson_8", "izabrati", "выбрать", "Глагол", "Koju opciju biste vi izabrali?", "отвергнуть", "потерять", "забыть"),
        v("v_8_8", "lesson_8", "rado", "с удовольствием / охотно", "Наречие", "Rado bih došao na vašu proslavu.", "неохотно", "редко", "напрасно"),
        v("v_8_9", "lesson_8", "mogućnost", "возможность", "Существительное", "Bila bi to odlična mogućnost za napredak.", "необходимость", "проблема", "сомнение"),
        v("v_8_10", "lesson_8", "zamoliti", "попросить", "Глагол", "Zamolio bih vas za malo tišine.", "приказать", "отказать", "поблагодарить"),

        // Lesson 9: Глаголы движения
        v("v_9_1", "lesson_9", "doći", "прийти", "Глагол", "Doći ćemo kod tebe oko šest sati.", "уйти", "выйти", "войти"),
        v("v_9_2", "lesson_9", "ići", "идти / ехать", "Глагол", "Svako jutro idem peške na posao.", "стоять", "сидеть", "лежать"),
        v("v_9_3", "lesson_9", "otići", "уйти / уехать", "Глагол", "Otišao je u Beograd na konferenciju.", "прийти", "остаться", "вернуться"),
        v("v_9_4", "lesson_9", "ući", "войти", "Глагол", "Molim vas, uđite u salu za sastanke.", "выйти", "убежать", "пройти"),
        v("v_9_5", "lesson_9", "izaći", "выйти", "Глагол", "Izašli smo iz zgrade na svež vazduh.", "войти", "остаться", "зайти"),
        v("v_9_6", "lesson_9", "vratiti se", "вернуться", "Возвратный глагол", "Vratićemo se kući kasno uveče.", "уехать", "отправиться", "остаться"),
        v("v_9_7", "lesson_9", "proći", "пройти", "Глагол", "Prošli smo pored stare tvrđave.", "остановиться", "вернуться", "остаться"),
        v("v_9_8", "lesson_9", "preći", "перейти", "Глагол", "Pređite ulicu na pešačkom prelazu.", "обойти", "остаться", "вернуться"),
        v("v_9_9", "lesson_9", "stanica", "остановка / вокзал", "Существительное", "Autobuska stanica je na kraju ulice.", "перекресток", "площадь", "мост"),
        v("v_9_10", "lesson_9", "peške", "пешком", "Наречие", "Od hotela do centra idemo peške.", "на машине", "на поезде", "самолетом"),

        // Lesson 10: Дательный падеж и клитики
        v("v_10_1", "lesson_10", "poklon", "подарок", "Существительное", "Kupili smo lep poklon našoj prijateljici.", "письмо", "книга", "работа"),
        v("v_10_2", "lesson_10", "dati", "дать", "Глагол", "Daj mi molim te tu plavu olovku.", "взять", "украсть", "потерять"),
        v("v_10_3", "lesson_10", "reći", "сказать", "Глагол", "Reci nam šta se juče dogodilo.", "промолчать", "спросить", "услышать"),
        v("v_10_4", "lesson_10", "objasniti", "объяснить", "Глагол", "Profesor nam je jasno objasnio pravilo.", "запутать", "спросить", "скрыть"),
        v("v_10_5", "lesson_10", "verovati", "верить", "Глагол", "Potpuno ti verujem na reč.", "сомневаться", "обманывать", "спорить"),
        v("v_10_6", "lesson_10", "zahvaliti se", "поблагодарить", "Возвратный глагол", "Zahvaljujem vam se na brzoj pomoći.", "извиниться", "пожаловаться", "отказать"),
        v("v_10_7", "lesson_10", "odgovarati", "подходить / отвечать", "Глагол", "Da li vam odgovara termin u sredu?", "мешать", "спрашивать", "запрещать"),
        v("v_10_8", "lesson_10", "čestitati", "поздравить", "Глагол", "Čestitam ti na velikom uspehu!", "посочувствовать", "пожаловаться", "отказать"),
        v("v_10_9", "lesson_10", "prijatelj", "друг", "Существительное", "Poslao sam poruku svom najboljem prijatelju.", "сосед", "коллега", "незнакомец"),
        v("v_10_10", "lesson_10", "javiti se", "откликнуться / позвонить", "Возвратный глагол", "Javiću ti se čim završim sastanak.", "исчезнуть", "замолчать", "отказаться"),

        // Lesson 11: Винительный и локатив (куда vs где)
        v("v_11_1", "lesson_11", "grad", "город", "Существительное", "Uveče šetamo po starom gradu.", "село", "улица", "дом"),
        v("v_11_2", "lesson_11", "posao", "работа", "Существительное", "Ujutru žurim na posao, a popodne sam na poslu.", "отдых", "учеба", "праздник"),
        v("v_11_3", "lesson_11", "knjižara", "книжный магазин", "Существительное", "Idemo u knjižaru da kupimo rečnik.", "аптека", "пекарня", "банк"),
        v("v_11_4", "lesson_11", "trg", "площадь", "Существительное", "Na trgu se nalazi veliki spomenik.", "улица", "парк", "двор"),
        v("v_11_5", "lesson_11", "sto", "стол", "Существительное", "Stavi ključeve na sto u dnevnoj sobi.", "стул", "шкаф", "полка"),
        v("v_11_6", "lesson_11", "park", "парк", "Существительное", "Deca se igraju u gradskom parku.", "лес", "стадион", "площадь"),
        v("v_11_7", "lesson_11", "kancelarija", "офис / кабинет", "Существительное", "Kolege su već u kancelariji.", "квартира", "гостиница", "магазин"),
        v("v_11_8", "lesson_11", "aerodrom", "аэропорт", "Существительное", "Idemo na aerodrom dva sata pre leta.", "вокзал", "порт", "стоянка"),
        v("v_11_9", "lesson_11", "pozorište", "театр", "Существительное", "Večeras idemo u narodno pozorište.", "кинотеатр", "музей", "концерт"),
        v("v_11_10", "lesson_11", "stan", "квартира", "Существительное", "Živimo u svetlom stanu blizu reke.", "дом", "комната", "отель"),

        // Lesson 12: Притяжательные местоимения
        v("v_12_1", "lesson_12", "naš", "наш", "Местоимение", "Naš tim radi na novom projektu.", "мой", "твой", "ваш"),
        v("v_12_2", "lesson_12", "moj", "мой", "Местоимение", "Ovo je moj omiljeni kafić u gradu.", "твой", "его", "наш"),
        v("v_12_3", "lesson_12", "tvoj", "твой", "Местоимение", "Da li je ovo tvoj telefon na stolu?", "мой", "ваш", "их"),
        v("v_12_4", "lesson_12", "njegov", "его", "Местоимение", "Njegov brat studira arhitekturu.", "её", "наш", "твой"),
        v("v_12_5", "lesson_12", "njen", "её", "Местоимение", "Njena ideja je bila veoma korisna.", "его", "мой", "ваш"),
        v("v_12_6", "lesson_12", "vaš", "ваш", "Местоимение", "Hvala na vašem strpljenju i podršci.", "наш", "мой", "их"),
        v("v_12_7", "lesson_12", "njihov", "их", "Местоимение", "Njihov plan je jednostavan i jasan.", "наш", "его", "её"),
        v("v_12_8", "lesson_12", "porodica", "семья", "Существительное", "Moja porodica živi u mirnom kraju.", "друзья", "коллеги", "соседи"),
        v("v_12_9", "lesson_12", "komšija", "сосед", "Существительное", "Naš komšija je veoma ljubazan čovek.", "родственник", "коллега", "гость"),
        v("v_12_10", "lesson_12", "iskustvo", "опыт", "Существительное", "Njeno iskustvo je dragoceno za tim.", "знание", "мнение", "время"),

        // Lesson 13: Степени сравнения
        v("v_13_1", "lesson_13", "bolji", "лучше / более хороший", "Прилагательное", "Ovaj metod učenja je mnogo bolji.", "хуже", "больше", "меньше"),
        v("v_13_2", "lesson_13", "gori", "хуже / более плохой", "Прилагательное", "Vreme danas nije gore nego juče.", "лучше", "старше", "моложе"),
        v("v_13_3", "lesson_13", "veći", "больший", "Прилагательное", "Treba nam veći sto za sastanke.", "меньший", "длинный", "короткий"),
        v("v_13_4", "lesson_13", "manji", "меньший", "Прилагательное", "Izabrali smo manji, ali tiši hotel.", "больший", "высокий", "широкий"),
        v("v_13_5", "lesson_13", "brži", "более быстрый", "Прилагательное", "Voz je brži od autobusa na ovoj liniji.", "медленнее", "тяжелее", "дешевле"),
        v("v_13_6", "lesson_13", "skuplji", "дороже", "Прилагательное", "Restoran u centru je malo skuplji.", "дешевле", "проще", "дальше"),
        v("v_13_7", "lesson_13", "jeftiniji", "дешевле", "Прилагательное", "Na pijaci je voće mnogo jeftinije.", "дороже", "слаще", "свежее"),
        v("v_13_8", "lesson_13", "najvažniji", "самый важный", "Прилагательное", "Ovo je najvažniji korak u našem planu.", "самый простой", "самый быстрый", "самый легкий"),
        v("v_13_9", "lesson_13", "lakši", "более легкий", "Прилагательное", "Drugi zadatak je lakši od prvog.", "сложнее", "длиннее", "строже"),
        v("v_13_10", "lesson_13", "teži", "более трудный", "Прилагательное", "Gramatika na početku deluje teža.", "легче", "короче", "быстрее"),

        // Lesson 14: Сложные союзы
        v("v_14_1", "lesson_14", "zato što", "потому что", "Союз", "Ostajem kod kuće zato što pada kiša.", "хотя", "пока", "когда"),
        v("v_14_2", "lesson_14", "iako", "хотя", "Союз", "Izašli smo u šetnju iako je hladno.", "потому что", "если", "пока"),
        v("v_14_3", "lesson_14", "dok", "пока / в то время как", "Союз", "Čitam vesti dok pijem jutarnju kafu.", "после того как", "хотя", "потому что"),
        v("v_14_4", "lesson_14", "čim", "как только", "Союз", "Pozovi me čim završiš sa poslom.", "прежде чем", "хотя", "пока"),
        v("v_14_5", "lesson_14", "pošto", "так как / после того как", "Союз", "Pošto imaš vremena, hajde na ručak.", "несмотря на", "чтобы", "или"),
        v("v_14_6", "lesson_14", "ako", "если", "Союз", "Ako bude lepo vreme, idemo na reku.", "хотя", "потому что", "пока"),
        v("v_14_7", "lesson_14", "da bi", "чтобы", "Союз", "Vežbam svaki dan da bih govorio tečno.", "потому что", "хотя", "как только"),
        v("v_14_8", "lesson_14", "kada", "когда", "Союз", "Javi se kada stigneš u Beograd.", "где", "почему", "сколько"),
        v("v_14_9", "lesson_14", "pre nego što", "прежде чем", "Союз", "Proveri kartu pre nego što kreneš.", "после того как", "потому что", "хотя"),
        v("v_14_10", "lesson_14", "međutim", "однако", "Союз", "Plan je dobar, međutim treba nam više vremena.", "поэтому", "также", "или"),

        // Lesson 15: Пассив и безличные конструкции
        v("v_15_1", "lesson_15", "trebati", "быть нужным / следует", "Глагол", "Treba da proverimo sve detalje ugovora.", "быть должным", "мочь", "сметь"),
        v("v_15_2", "lesson_15", "govori se", "говорят / используется", "Безличная форма", "Ovde se govore srpski i engleski jezik.", "пишется", "читается", "считается"),
        v("v_15_3", "lesson_15", "čini se", "кажется", "Безличная форма", "Čini se da će danas biti toplo.", "знает", "хочется", "удается"),
        v("v_15_4", "lesson_15", "moguće", "возможно", "Наречие", "Da li je moguće rezervisati sto za večeras?", "запрещено", "сложно", "опасно"),
        v("v_15_5", "lesson_15", "potrebno", "необходимо", "Наречие", "Potrebno je poneti pasoš na putovanje.", "нежелательно", "случайно", "достаточно"),
        v("v_15_6", "lesson_15", "zabranjeno", "запрещено", "Причастие", "Ovde je zabranjeno fotografisanje.", "разрешено", "рекомендовано", "обязательно"),
        v("v_15_7", "lesson_15", "dozvoljeno", "разрешено", "Причастие", "Parkiranje je dozvoljeno samo za goste.", "запрещено", "ограничено", "отменено"),
        v("v_15_8", "lesson_15", "otvoreno", "открыто", "Причастие", "Muzej je otvoren svakog dana osim ponedeljka.", "закрыто", "занято", "построено"),
        v("v_15_9", "lesson_15", "zatvoreno", "закрыто", "Причастие", "U nedelju su sve banke zatvorene.", "открыто", "готово", "свободно"),
        v("v_15_10", "lesson_15", "važno", "важно", "Наречие", "Važno je da razumete osnovnu strukturu.", "скучно", "поздно", "трудно"),

        // Lesson 16: Итоговый синтез (Полиглот)
        v("v_16_1", "lesson_16", "razumevanje", "понимание", "Существительное", "Međusobno razumevanje je ključ dobrog razgovora.", "вопрос", "ответ", "знание"),
        v("v_16_2", "lesson_16", "napredak", "прогресс", "Существительное", "Tvoj napredak u srpskom je već vidljiv.", "ошибка", "пауза", "сомнение"),
        v("v_16_3", "lesson_16", "tečno", "свободно / бегло", "Наречие", "Posle redovne vežbe govorićete tečno.", "медленно", "тихо", "редко"),
        v("v_16_4", "lesson_16", "siguran", "уверенный", "Прилагательное", "Sada sam siguran u upotrebu glagolskih vremena.", "растерянный", "уставший", "занятой"),
        v("v_16_5", "lesson_16", "komunikacija", "общение / коммуникация", "Существительное", "Svakodnevna komunikacija ubrzava učenje jezika.", "молчание", "чтение", "перевод"),
        v("v_16_6", "lesson_16", "sporazumeti se", "договориться / понять друг друга", "Возвратный глагол", "Lako smo se sporazumeli bez rečnika.", "поссориться", "разойтись", "ошибиться"),
        v("v_16_7", "lesson_16", "uspeh", "успех", "Существительное", "Želimo vam mnogo uspeha u daljem učenju!", "неудача", "сомнение", "усталость"),
        v("v_16_8", "lesson_16", "vežba", "упражнение / тренировка", "Существительное", "Kratka dnevna vežba daje odlične rezultate.", "экзамен", "перерыв", "оценка"),
        v("v_16_9", "lesson_16", "iskren", "искренний", "Прилагательное", "Hvala vam na iskrenom razgovoru i savetima.", "формальный", "случайный", "короткий"),
        v("v_16_10", "lesson_16", "cilj", "цель", "Существительное", "Naš glavni cilj je prirodan i tačan govor.", "препятствие", "начало", "привычка")
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
