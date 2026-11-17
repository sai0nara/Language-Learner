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
            sentenceCount = 9
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
            sentenceCount = 9
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
            sentenceCount = 9
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
            sentenceCount = 9
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
            sentenceCount = 9
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
            sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_7", orderIndex = 7,
            title = "Урок 7: Повелительное наклонение",
            subtitle = "Императив: radi! radite! nemoj da radiš!",
            grammarFocus = "Императив", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_8", orderIndex = 8,
            title = "Урок 8: Условное наклонение",
            subtitle = "Потенциал: bih, bi, bismo, biste + radni pridev",
            grammarFocus = "Потенциал", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_9", orderIndex = 9,
            title = "Урок 9: Глаголы движения",
            subtitle = "Ići, doći, otići, ući, izaći с предлогами",
            grammarFocus = "Глаголы движения", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_10", orderIndex = 10,
            title = "Урок 10: Дательный падеж и кому/чему",
            subtitle = "Личные местоимения и клитики mi, ti, mu, joj, nam",
            grammarFocus = "Датив (Дательный падеж)", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_11", orderIndex = 11,
            title = "Урок 11: Винительный и локатив",
            subtitle = "Куда vs Где (u grad / u gradu)",
            grammarFocus = "Акузатив и Локатив", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_12", orderIndex = 12,
            title = "Урок 12: Притяжательные местоимения",
            subtitle = "Moj, tvoj, njegov, njen, naš, vaš, njihov",
            grammarFocus = "Притяжательные формы", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_13", orderIndex = 13,
            title = "Урок 13: Степени сравнения",
            subtitle = "Компаратив и суперлатив прилагательных и наречий",
            grammarFocus = "Сравнение", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_14", orderIndex = 14,
            title = "Урок 14: Сложные союзы",
            subtitle = "Zato što, iako, dok, čim, pošto",
            grammarFocus = "Придаточные предложения", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_15", orderIndex = 15,
            title = "Урок 15: Пассив и безличные конструкции",
            subtitle = "Ovde se govori srpski / Treba da...",
            grammarFocus = "Безличные конструкции", level = "B1",
            vocabCount = 10, sentenceCount = 9
        ),
        LessonEntity(
            id = "lesson_16", orderIndex = 16,
            title = "Урок 16: Итоговый синтез (Полиглот)",
            subtitle = "Свободное комбинирование времен, падежей и вопросов",
            grammarFocus = "Комплексная матрица", level = "B1",
            vocabCount = 10, sentenceCount = 9
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

    private fun cell(
        formType: String,
        tense: String,
        formula: String,
        exampleLatin: String,
        exampleRussian: String,
        notes: String = ""
    ) = ConjugationCell(
        formType = formType,
        tense = tense,
        formula = formula,
        exampleSerbian = exampleLatin,
        exampleSerbianLatin = exampleLatin,
        exampleRussian = exampleRussian,
        notes = notes
    )

    private fun grammarGuide(
        idx: Int,
        title: String,
        summary: String,
        explanationMarkdown: String,
        verbInfinitive: String,
        verbTranslation: String,
        aspect: String = "Nesvršeni (Imperfective)",
        cells: List<ConjugationCell>
    ) = GrammarGuideEntity(
        id = "g_$idx",
        lessonId = "lesson_$idx",
        title = title,
        summary = summary,
        explanationMarkdown = explanationMarkdown.trimIndent(),
        matrixJson = matrixAdapter.toJson(
            ConjugationMatrixData(
                verbInfinitive = verbInfinitive,
                verbInfinitiveLatin = verbInfinitive,
                verbTranslation = verbTranslation,
                aspect = aspect,
                cells = cells
            )
        )
    )

    fun getInitialGrammarGuides(): List<GrammarGuideEntity> = listOf(
        grammarGuide(
            idx = 1,
            title = "Матрица Полиглота: Глагол 'Raditi' (3x3)",
            summary = "Фундаментальная сетка 3x3: 3 времени × 3 формы предложения",
            explanationMarkdown = """
                ### Базовая структура сербского глагола (Метод Полиглот 16)
                В сербском языке глагол спрягается по лицам, числам и временам.

                **Ключевые отличия от русского:**
                1. Личные местоимения (*ja, ti, on, mi, vi, oni*) часто опускаются.
                2. Краткие безударные связки (*sam, si, je...* и *ću, ćeš, će*) стоят строго на втором месте.
                3. Вопрос строится через «Da li» (*Da li radiš?*) или инверсией (*Radiš li?*).
            """,
            verbInfinitive = "Raditi",
            verbTranslation = "Работать / Делать",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Radio/Radila sam (ja)", "Ja sam juče dugo radio.", "Я вчера долго работал.", "Энклитика 'sam' ставится сразу после ударного первого слова."),
                cell("Affirmative (+)", "Present", "Radim, radiš, radi, radimo, radite, rade", "Ja radim u ovoj kompaniji.", "Я работаю в этой компании.", "Глаголы на -iti в презенте имеют окончания -im, -iš, -i..."),
                cell("Affirmative (+)", "Future", "Radiću ili Ja ću raditi", "Sutra ću raditi od kuće.", "Завтра я буду работать из дома.", "Если местоимение опущено: Radiću. Если стоит впереди: Ja ću raditi."),
                cell("Negative (-)", "Past", "Nisam radio/radila", "Nisam radio ništa loše.", "Я не делал ничего плохого.", "Отрицательные формы: nisam, nisi, nije, nismo, niste, nisu."),
                cell("Negative (-)", "Present", "Ne radim, ne radiš, ne radi...", "Danas ne radim, imam slobodan dan.", "Сегодня я не работаю, у меня выходной.", "Частица 'ne' пишется раздельно с глаголом (кроме neću, nemam, nisam, nemoj)."),
                cell("Negative (-)", "Future", "Neću raditi", "Neću raditi za vikend.", "Я не буду работать на выходных.", "Формы: neću, nećeš, neće, nećemo, nećete, neće + инфинитив."),
                cell("Interrogative (?)", "Past", "Da li si radio? / Jesi li radio?", "Da li si radio na ovom projektu?", "Ты работал над этим проектом?", "Вопрос строится через 'Da li + краткая связка' или полная форма + li."),
                cell("Interrogative (?)", "Present", "Da li radiš? / Radiš li?", "Da li radiš u Beogradu?", "Ты работаешь в Белграде?", "Две равноправные модели вопроса: Da li + глагол или Глагол + li."),
                cell("Interrogative (?)", "Future", "Da li ćeš raditi? / Hoćeš li raditi?", "Hoćeš li sutra raditi sa nama?", "Будешь ли ты завтра работать с нами?", "Вопрос: Hoćeš li raditi? или Da li ćeš raditi?")
            )
        ),
        grammarGuide(
            idx = 2,
            title = "Возвратные глаголы с 'SE'",
            summary = "Частица 'se' пишется раздельно и занимает строго второе место в предложении",
            explanationMarkdown = """
                ### Povratni glagoli u srpskom jeziku
                В сербском языке клитика **se** пишется отдельно от глагола:
                - *Ja **se** nadam* или *Nadam **se***.
                - В перфекте после *sam, si, smo, ste, su* ставится **se**: *Ja sam **se** smejao*.
                - Для 3-го лица ед. ч. в перфекте **je** перед **se** опускается: *On **se** seća* → *On **se** sećao* (без *je*!).
            """,
            verbInfinitive = "Sećati se",
            verbTranslation = "Помнить / Вспоминать",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Sećao/Sećala sam se", "Dobro sam se sećao tog dana.", "Я хорошо помнил тот день."),
                cell("Affirmative (+)", "Present", "Sećam se, sećaš se, seća se", "Dobro se sećam ovog lepog grada.", "Я хорошо помню этот красивый город."),
                cell("Affirmative (+)", "Future", "Sećaću se / Ja ću se sećati", "Uvek ću se sećati našeg putovanja.", "Я всегда буду помнить наше путешествие."),
                cell("Negative (-)", "Past", "Nisam se sećao/sećala", "Nisam se sećao njegovog imena.", "Я не помнил его имени."),
                cell("Negative (-)", "Present", "Ne sećam se", "Ne sećam se te adrese.", "Я не помню этого адреса."),
                cell("Negative (-)", "Future", "Neću se sećati", "Sutra se neću sećati detalja.", "Завтра я не вспомню деталей."),
                cell("Interrogative (?)", "Past", "Da li si se sećao?", "Da li si se sećao našeg razgovora?", "Ты помнил наш разговор?"),
                cell("Interrogative (?)", "Present", "Da li se sećaš?", "Kojim sportom se baviš?", "Каким спортом ты занимаешься?"),
                cell("Interrogative (?)", "Future", "Da li ćeš se sećati?", "Hoćeš li se sećati ovog pravila?", "Ты будешь помнить это правило?")
            )
        ),
        grammarGuide(
            idx = 3,
            title = "Модальные глаголы и конструкция 'DA + Презент'",
            summary = "Вместо инфинитива в сербском используется 'da + личная форма глагола'",
            explanationMarkdown = """
                ### Конструкция: Модальный глагол + DA + Презент
                - *Ja hoću **da radim**.* (Я хочу работать.)
                - *Ti moraš **da učiš**.* (Ты должен учиться.)
                - *Mi možemo **da pomognemo**.* (Мы можем помочь.)
            """,
            verbInfinitive = "Hteti / Moći / Morati",
            verbTranslation = "Хотеть / Мочь / Быть должным",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Hteo / Mogao / Morao sam da...", "Morao sam odmah da krenem.", "Я должен был сразу отправиться."),
                cell("Affirmative (+)", "Present", "Hoću / Mogu / Moram + da + prezent", "Ja hoću da govorim srpski tečno.", "Я хочу говорить по-сербски свободно."),
                cell("Affirmative (+)", "Future", "Moći ću / Moraću da...", "Sutra ću moći da pomognem.", "Завтра я смогу помочь."),
                cell("Negative (-)", "Past", "Nisam hteo / mogao / morao da...", "Nisam mogao da dođem na sastanak.", "Я не мог прийти на встречу."),
                cell("Negative (-)", "Present", "Neću / Ne mogu / Ne moram + da...", "Ne mogu sada da razgovaram.", "Я не могу сейчас разговаривать."),
                cell("Negative (-)", "Future", "Neću moći / Neću morati da...", "Neću moći da stignem na vreme.", "Я не смогу успеть вовремя."),
                cell("Interrogative (?)", "Past", "Da li si morao / mogao da...?", "Da li si morao da čekaš dugo?", "Тебе пришлось долго ждать?"),
                cell("Interrogative (?)", "Present", "Da li možeš / hoćeš da...?", "Možeš li da ponoviš pitanje?", "Можешь повторить вопрос?"),
                cell("Interrogative (?)", "Future", "Da li ćeš moći da...?", "Hoćeš li moći da dođeš sutra?", "Сможешь прийти завтра?")
            )
        ),
        grammarGuide(
            idx = 4,
            title = "Прошедшее время (Перфекат)",
            summary = "Связка 'sam, si, je, smo, ste, su' + причастие на -o / -la / -li",
            explanationMarkdown = """
                ### Perfekat (Prošlo vreme)
                1. Муж. р. ед. ч.: *radio, učio, kupio*; жен. р.: *radila, učila*; мн. ч.: *radili, radile*.
                2. Связка стоит на втором месте: *Juče **sam** kupio rečnik.*
                3. Отрицание: *nisam, nisi, nije, nismo, niste, nisu*.
            """,
            verbInfinitive = "Kupiti / Učiti",
            verbTranslation = "Купить / Учить",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Kupio/Kupila sam", "Juče sam kupio nov rečnik.", "Вчера я купил новый словарь."),
                cell("Affirmative (+)", "Present", "Kupujem / Učim", "Sada učim prošlo vreme.", "Сейчас я учу прошедшее время."),
                cell("Affirmative (+)", "Future", "Kupiću / Učiću", "Sutra ću kupiti novu knjigu.", "Завтра я куплю новую книгу."),
                cell("Negative (-)", "Past", "Nisam kupio/kupila", "Nisam zaboravio na tvoj poziv.", "Я не забыл о твоём звонке."),
                cell("Negative (-)", "Present", "Ne kupujem", "Ne kupujem novine svaki dan.", "Я не покупаю газеты каждый день."),
                cell("Negative (-)", "Future", "Neću kupiti", "Neću kupiti tu kartu danas.", "Я не куплю этот билет сегодня."),
                cell("Interrogative (?)", "Past", "Da li si kupio? / Jesi li kupio?", "Da li si posetio muzej juče?", "Ты посетил музей вчера?"),
                cell("Interrogative (?)", "Present", "Da li kupuješ?", "Da li slušaš profesora?", "Ты слушаешь профессора?"),
                cell("Interrogative (?)", "Future", "Da li ćeš kupiti?", "Hoćeš li završiti projekat?", "Ты закончишь проект?")
            )
        ),
        grammarGuide(
            idx = 5,
            title = "Будущее время I (Футур I)",
            summary = "Энклитика 'ću, ćeš, će, ćemo, ćete, će' + инфинитив или 'da + презент'",
            explanationMarkdown = """
                ### Futur I (Buduće vreme)
                1. С местоимением или другим словом в начале: *Sutra **ću putovati**.*
                2. Без первого слова глагол на -ti сливается со связкой: *Putova**ću** u Beograd.* (глаголы на -ći сохраняют -ći: *Stići ću*).
                3. Отрицание: *neću, nećeš, neće, nećemo, nećete, neće*.
            """,
            verbInfinitive = "Putovati",
            verbTranslation = "Путешествовать",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Putovao/Putovala sam", "Prošle godine sam putovao vozom.", "В прошлом году я путешествовал поездом."),
                cell("Affirmative (+)", "Present", "Putujem, putuješ, putuje", "Često putujem zbog posla.", "Я часто путешествую по работе."),
                cell("Affirmative (+)", "Future", "Putovaću / Ja ću putovati", "Sutra ću putovati u Novi Sad.", "Завтра я поеду в Нови-Сад."),
                cell("Negative (-)", "Past", "Nisam putovao", "Nisam putovao za vikend.", "Я не путешествовал на выходных."),
                cell("Negative (-)", "Present", "Ne putujem", "Ove nedelje ne putujem.", "На этой неделе я не путешествую."),
                cell("Negative (-)", "Future", "Neću putovati", "Neću putovati bez pasoša.", "Я не поеду без паспорта."),
                cell("Interrogative (?)", "Past", "Da li si putovao?", "Da li si putovao u Srbiju?", "Ты путешествовал в Сербию?"),
                cell("Interrogative (?)", "Present", "Da li putuješ?", "Da li često putuješ?", "Ты часто путешествуешь?"),
                cell("Interrogative (?)", "Future", "Da li ćeš putovati? / Hoćeš li...", "Hoćeš li putovati za odmor?", "Поедешь путешествовать в отпуск?")
            )
        ),
        grammarGuide(
            idx = 6,
            title = "Видовые пары глаголов (Аспект)",
            summary = "Несовершенный вид (процесс) vs Совершенный вид (результат)",
            explanationMarkdown = """
                ### Glagolski vid (Svršeni i nesvršeni)
                - Процесс / регулярность: *pisati, kupovati, čitati, učiti*.
                - Завершённый результат (в перфекте, футуре или после *da*): *napisati, kupiti, pročitati, naučiti*.
            """,
            verbInfinitive = "Pisati / Napisati",
            verbTranslation = "Писать / Написать",
            aspect = "Dvovidski par",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Pisao sam / Napisao sam", "Napisao sam ceo izveštaj juče.", "Я написал весь отчёт вчера."),
                cell("Affirmative (+)", "Present", "Pišem, pišeš, piše", "Svako jutro pišem kratke beleške.", "Каждое утро я пишу короткие заметки."),
                cell("Affirmative (+)", "Future", "Pisaću / Napisaću", "Sutra ću napisati pismo.", "Завтра я напишу письмо."),
                cell("Negative (-)", "Past", "Nisam napisao", "Još nisam pročitao taj članak.", "Я ещё не прочитал эту статью."),
                cell("Negative (-)", "Present", "Ne pišem", "Sada ne pišem poruke.", "Сейчас я не пишу сообщения."),
                cell("Negative (-)", "Future", "Neću napisati", "Neću kupovati ništa suvišno.", "Я не буду покупать ничего лишнего."),
                cell("Interrogative (?)", "Past", "Da li si napisao?", "Da li si naučio novo pravilo?", "Ты выучил новое правило?"),
                cell("Interrogative (?)", "Present", "Da li čitaš?", "Da li redovno ponavljaš reči?", "Ты регулярно повторяешь слова?"),
                cell("Interrogative (?)", "Future", "Da li ćeš pročitati?", "Hoćeš li otvoriti prozor?", "Откроешь окно?")
            )
        ),
        grammarGuide(
            idx = 7,
            title = "Повелительное наклонение (Императив)",
            summary = "Просьба и побуждение: radi!, radite!, hajde da..., nemoj da...",
            explanationMarkdown = """
                ### Imperativ
                - 2-е лицо ед. ч.: *radi! sačekaj! pomozi!*
                - 2-е лицо мн. ч. / вежливое: *radite! otvorite! ponovite!*
                - Запрет: *Ne radi!* или *Nemoj da radiš!* / *Nemojte da kasnite!*
            """,
            verbInfinitive = "Pomoći / Čekati",
            verbTranslation = "Помочь / Ждать",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Pomogao sam", "Juče sam odmah pomogao kolegi.", "Вчера я сразу помог коллеге."),
                cell("Affirmative (+)", "Present", "Pomozi! / Pomozite!", "Molim te, pomozi mi sa prevodom!", "Пожалуйста, помоги мне с переводом!"),
                cell("Affirmative (+)", "Future", "Krenimo! / Poslaću", "Krenimo odmah na stanicu!", "Пойдёмте сразу на остановку!"),
                cell("Negative (-)", "Past", "Nisam čekao", "Nisam dugo čekao ispred ulaza.", "Я недолго ждал перед входом."),
                cell("Negative (-)", "Present", "Nemoj da... / Nemojte da...", "Nemoj da zaboraviš adresu!", "Не забудь адрес!"),
                cell("Negative (-)", "Future", "Nećemo kasniti", "Nećemo kasniti na sastanak.", "Мы не опоздаем на встречу."),
                cell("Interrogative (?)", "Past", "Da li si poslao?", "Da li si poslao poruku?", "Ты отправил сообщение?"),
                cell("Interrogative (?)", "Present", "Možeš li da...?", "Možete li da otvorite knjigu?", "Можете открыть книгу?"),
                cell("Interrogative (?)", "Future", "Hoćeš li sačekati?", "Hoćeš li me sačekati ispred ulaza?", "Подождёшь меня перед входом?")
            )
        ),
        grammarGuide(
            idx = 8,
            title = "Условное наклонение (Потенциал)",
            summary = "Вежливые просьбы и желания: bih, bi, bismo, biste + причастие на -o/-la/-li",
            explanationMarkdown = """
                ### Potencijal (Kondicional)
                - Образуется из форм *bih, bi, bi, bismo, biste, bi* + л-причастие (*želeo, voleo, mogao*).
                - *Želeo **bih** da naručim kafu.* — Я хотел бы заказать кофе.
                - *Šta **biste** preporučili?* — Что бы вы порекомендовали?
            """,
            verbInfinitive = "Želeti / Voleti",
            verbTranslation = "Хотеть / Любить (бы)",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Želeo sam", "Juče sam želeo da razgovaram.", "Вчера я хотел поговорить."),
                cell("Affirmative (+)", "Present", "Želeo bih / Volela bih", "Želeo bih da naručim kafu.", "Я хотел бы заказать кофе."),
                cell("Affirmative (+)", "Future", "Iskoristiću / Rado bih došao", "Rado bih došao na proslavu.", "Я с удовольствием пришёл бы на праздник."),
                cell("Negative (-)", "Past", "Nisam izabrao", "Nisam izabrao drugu opciju.", "Я не выбрал другой вариант."),
                cell("Negative (-)", "Present", "Ne bih želeo / Ne bih preporučio", "Ne bih predložio taj hotel.", "Я бы не предложил этот отель."),
                cell("Negative (-)", "Future", "Neću odbiti", "Neću odbiti vaš dobar savet.", "Я не откажусь от вашего хорошего совета."),
                cell("Interrogative (?)", "Past", "Da li si predložio?", "Da li si predložio novi plan?", "Ты предложил новый план?"),
                cell("Interrogative (?)", "Present", "Da li biste... / Šta biste...?", "Šta biste nam preporučili?", "Что бы вы нам порекомендовали?"),
                cell("Interrogative (?)", "Future", "Hoćete li doći?", "Hoćete li iskoristiti ovu priliku?", "Вы воспользуетесь этой возможностью?")
            )
        ),
        grammarGuide(
            idx = 9,
            title = "Глаголы движения и предлоги",
            summary = "Ići, doći, otići, ući, izaći, vratiti se с предлогами u / na / iz / pored",
            explanationMarkdown = """
                ### Glagoli kretanja
                - Направление (куда? + винительный): *Idem **u grad**, dolazim **na stanicu**.*
                - Выход (откуда? + родительный): *Izlazim **iz zgrade**, vraćam se **sa posla**.*
            """,
            verbInfinitive = "Ići / Doći",
            verbTranslation = "Идти / Прийти",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Išao / Došao / Otišao sam", "Otišao je u Beograd jutros.", "Он уехал в Белград сегодня утром."),
                cell("Affirmative (+)", "Present", "Idem, ideš, ide, idemo", "Svako jutro idem peške na posao.", "Каждое утро я иду пешком на работу."),
                cell("Affirmative (+)", "Future", "Doći ću / Vratićemo se", "Doći ćemo kod tebe oko šest.", "Мы придём к тебе около шести."),
                cell("Negative (-)", "Past", "Nisam ušao / izašao", "Nisam izašao iz kuće juče.", "Я не выходил из дома вчера."),
                cell("Negative (-)", "Present", "Ne idem", "Danas ne idem na stanicu.", "Сегодня я не иду на вокзал."),
                cell("Negative (-)", "Future", "Neću otići", "Neću otići pre kraja sastanka.", "Я не уйду до конца встречи."),
                cell("Interrogative (?)", "Past", "Da li si prošao?", "Da li si prošao pored tvrđave?", "Ты прошёл мимо крепости?"),
                cell("Interrogative (?)", "Present", "Kuda ideš?", "Ideš li peške do centra?", "Ты идёшь пешком до центра?"),
                cell("Interrogative (?)", "Future", "Kada ćeš se vratiti?", "Kada ćeš se vratiti kući?", "Когда ты вернёшься домой?")
            )
        ),
        grammarGuide(
            idx = 10,
            title = "Дательный падеж и клитики",
            summary = "Кому / чему: mi, ti, mu, joj, nam, vam, im на втором месте",
            explanationMarkdown = """
                ### Dativ (Kome? Čemu?)
                - Краткие местоимения: *mi* (мне), *ti* (тебе), *mu* (ему), *joj* (ей), *nam* (нам), *vam* (вам), *im* (им).
                - Окончания существительных: муж. р. *-u* (*prijatelju*), жен. р. *-i* (*prijateljici*).
            """,
            verbInfinitive = "Dati / Reći / Objasniti",
            verbTranslation = "Дать / Сказать / Объяснить",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Dao / Rekao / Objasnio nam je", "Profesor nam je jasno objasnio pravilo.", "Профессор ясно объяснил нам правило."),
                cell("Affirmative (+)", "Present", "Dajem ti / Verujem vam", "Potpuno ti verujem na reč.", "Я полностью верю тебе на слово."),
                cell("Affirmative (+)", "Future", "Javiću ti se / Poslaću mu", "Javiću ti se čim završim.", "Я позвоню тебе, как только закончу."),
                cell("Negative (-)", "Past", "Nisam mu rekao", "Nisam joj poslao poklon juče.", "Я не отправил ей подарок вчера."),
                cell("Negative (-)", "Present", "Ne odgovara mi", "Ovaj termin mi ne odgovara.", "Это время мне не подходит."),
                cell("Negative (-)", "Future", "Neću im smetati", "Neću vam smetati tokom rada.", "Я не буду мешать вам во время работы."),
                cell("Interrogative (?)", "Past", "Šta si mu rekao?", "Da li si čestitao prijatelju?", "Ты поздравил друга?"),
                cell("Interrogative (?)", "Present", "Da li vam odgovara?", "Da li vam odgovara sreda?", "Вам подходит среда?"),
                cell("Interrogative (?)", "Future", "Hoćeš li mi dati?", "Hoćeš li mi dati olovku?", "Дашь мне ручку?")
            )
        ),
        grammarGuide(
            idx = 11,
            title = "Винительный и локатив (Куда vs Где)",
            summary = "Движение (u grad, na sto) vs Местоположение (u gradu, na stolu)",
            explanationMarkdown = """
                ### Akuzativ (Kuda?) vs Lokativ (Gde?)
                - Куда (движение): *Idem **u knjižaru**, stavljam **na sto**.*
                - Где (покой): *Nalazim se **u knjižari**, ključevi su **na stolu**.*
            """,
            verbInfinitive = "Ići / Nalaziti se",
            verbTranslation = "Идти / Находиться",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Bio sam u gradu / Išao sam u park", "Juče smo šetali po starom gradu.", "Вчера мы гуляли по старому городу."),
                cell("Affirmative (+)", "Present", "Sam u kancelariji / Idem na posao", "Kolege su već u kancelariji.", "Коллеги уже в офисе."),
                cell("Affirmative (+)", "Future", "Ići ćemo u pozorište", "Večeras ćemo ići u pozorište.", "Сегодня вечером мы пойдём в театр."),
                cell("Negative (-)", "Past", "Nisam bio na trgu", "Nisam bio na aerodromu jutros.", "Я не был в аэропорту сегодня утром."),
                cell("Negative (-)", "Present", "Nije na stolu", "Telefon nije na stolu.", "Телефона нет на столе."),
                cell("Negative (-)", "Future", "Nećemo ostati u stanu", "Sutra nećemo ostati u stanu.", "Завтра мы не останемся в квартире."),
                cell("Interrogative (?)", "Past", "Gde si bio juče?", "Da li si bio u knjižari?", "Ты был в книжном магазине?"),
                cell("Interrogative (?)", "Present", "Gde se nalazi...?", "Gde se nalazi glavni trg?", "Где находится главная площадь?"),
                cell("Interrogative (?)", "Future", "Kuda idete večeras?", "Hoćete li ići u park?", "Вы пойдёте в парк?")
            )
        ),
        grammarGuide(
            idx = 12,
            title = "Притяжательные местоимения",
            summary = "Согласование moj, tvoj, njegov, njen, naš, vaš, njihov по роду и числу",
            explanationMarkdown = """
                ### Prisvojne zamenice
                - Муж. р.: *moj, tvoj, naš, vaš*; жен. р.: *moja, tvoja, naša*; ср. р.: *moje, naše*.
                - Неизменяемая основа: *njegov* (его), *njen* (её), *njihov* (их) + окончание рода/падежа.
            """,
            verbInfinitive = "Imati / Živeti",
            verbTranslation = "Иметь / Жить",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Naš tim je završio...", "Njena ideja je bila veoma korisna.", "Её идея была очень полезной."),
                cell("Affirmative (+)", "Present", "Moj / Naš / Njihov...", "Naš tim radi na novom projektu.", "Наша команда работает над новым проектом."),
                cell("Affirmative (+)", "Future", "Naša porodica će...", "Moja porodica će posetiti grad.", "Моя семья посетит город."),
                cell("Negative (-)", "Past", "Nije bio moj...", "To nije bio njihov plan.", "Это не был их план."),
                cell("Negative (-)", "Present", "Nije moj / tvoj", "Ovo nije moj telefon na stolu.", "Это не мой телефон на столе."),
                cell("Negative (-)", "Future", "Nećemo menjati naš...", "Nećemo menjati naš raspored.", "Мы не будем менять наше расписание."),
                cell("Interrogative (?)", "Past", "Čiji je bio...?", "Da li je tvoj brat bio ovde?", "Твой брат был здесь?"),
                cell("Interrogative (?)", "Present", "Da li je ovo tvoj / vaš...?", "Da li je ovo tvoj telefon?", "Это твой телефон?"),
                cell("Interrogative (?)", "Future", "Hoće li vaš komšija...?", "Hoće li vaš tim doći sutra?", "Ваша команда придёт завтра?")
            )
        ),
        grammarGuide(
            idx = 13,
            title = "Степени сравнения прилагательных",
            summary = "Компаратив (bolji, veći, brži) и суперлатив (najbolji, najvažniji)",
            explanationMarkdown = """
                ### Poređenje prideva
                - Исключения: *dobar → bolji → najbolji*; *loš → gori → najgori*; *velik → veći*; *mali → manji*.
                - Сравнение с «чем»: **od** + родительный падеж (*brži **od** autobusa*) или **nego** (*bolje **nego** juče*).
            """,
            verbInfinitive = "Biti bolji / brži",
            verbTranslation = "Быть лучше / быстрее",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Bio je lakši / brži", "Prvi zadatak je bio lakši.", "Первое задание было легче."),
                cell("Affirmative (+)", "Present", "Bolji je od...", "Voz je brži od autobusa.", "Поезд быстрее автобуса."),
                cell("Affirmative (+)", "Future", "Biće još bolji", "Sledeći rezultat će biti bolji.", "Следующий результат будет лучше."),
                cell("Negative (-)", "Past", "Nije bio skuplji", "Taj hotel nije bio skuplji.", "Этот отель не был дороже."),
                cell("Negative (-)", "Present", "Nije gori nego juče", "Vreme danas nije gore nego juče.", "Погода сегодня не хуже, чем вчера."),
                cell("Negative (-)", "Future", "Neće biti teži", "Sledeći test neće biti teži.", "Следующий тест не будет сложнее."),
                cell("Interrogative (?)", "Past", "Koji je bio bolji?", "Koji metod je bio lakši?", "Какой метод был легче?"),
                cell("Interrogative (?)", "Present", "Da li je jeftiniji?", "Da li je voće jeftinije na pijaci?", "Фрукты дешевле на рынке?"),
                cell("Interrogative (?)", "Future", "Hoće li biti veći?", "Hoće li novi sto biti veći?", "Новый стол будет больше?")
            )
        ),
        grammarGuide(
            idx = 14,
            title = "Сложные предложения и союзы",
            summary = "Причина (zato što, pošto), уступка (iako), время (dok, čim, kada), цель (da bi)",
            explanationMarkdown = """
                ### Složene rečenice i veznici
                - Причина: **zato što** (потому что), **pošto** (так как).
                - Уступка: **iako** (хотя).
                - Время и условие: **dok** (пока), **čim** (как только), **ako** (если).
            """,
            verbInfinitive = "Ostati / Izaći",
            verbTranslation = "Остаться / Выйти",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Izašli smo iako...", "Izašli smo u šetnju iako je bilo hladno.", "Мы вышли на прогулку, хотя было холодно."),
                cell("Affirmative (+)", "Present", "Čitam dok pijem...", "Čitam vesti dok pijem kafu.", "Я читаю новости, пока пью кофе."),
                cell("Affirmative (+)", "Future", "Pozvaću te čim...", "Javiću se čim stignem u grad.", "Я позвоню, как только приеду в город."),
                cell("Negative (-)", "Past", "Nismo išli zato što...", "Nismo išli u park zato što je padala kiša.", "Мы не пошли в парк, потому что шёл дождь."),
                cell("Negative (-)", "Present", "Ne izlazim ako...", "Ne izlazim ako je loše vreme.", "Я не выхожу, если плохая погода."),
                cell("Negative (-)", "Future", "Nećemo krenuti pre nego što...", "Nećemo krenuti pre nego što proverimo kartu.", "Мы не отправимся, прежде чем проверим билет."),
                cell("Interrogative (?)", "Past", "Zašto nisi došao?", "Da li si ostao kod kuće zato što si radio?", "Ты остался дома, потому что работал?"),
                cell("Interrogative (?)", "Present", "Šta radiš dok...?", "Šta slušaš dok putuješ vozom?", "Что ты слушаешь, пока едешь поездом?"),
                cell("Interrogative (?)", "Future", "Hoćeš li javiti kada...?", "Hoćeš li se javiti kada stigneš?", "Позвонишь, когда приедешь?")
            )
        ),
        grammarGuide(
            idx = 15,
            title = "Пассив и безличные конструкции",
            summary = "Возвратно-безличные формы (govori se, čini se) и предикативы (treba, moguće je, zabranjeno)",
            explanationMarkdown = """
                ### Bezlične i pasivne konstrukcije
                - Безличный глагол + *se*: *Ovde **se govori** srpski.* — Здесь говорят по-сербски.
                - *Treba da* + презент (для всех лиц): ***Treba da** proverimo detalje.*
                - *Zabranjeno je / Dozvoljeno je / Otvoreno je* + инфинитив или существительное.
            """,
            verbInfinitive = "Trebati / Govoriti se",
            verbTranslation = "Следовать / Говорить (безл.)",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Bilo je potrebno / otvoreno", "Juče je muzej bio otvoren ceo dan.", "Вчера музей был открыт весь день."),
                cell("Affirmative (+)", "Present", "Ovde se govori / Treba da...", "Ovde se govori srpski jezik.", "Здесь говорят по-сербски."),
                cell("Affirmative (+)", "Future", "Biće moguće / otvoreno", "Sutra će biti moguće rezervisati sto.", "Завтра будет возможно забронировать столик."),
                cell("Negative (-)", "Past", "Nije bilo dozvoljeno", "Parkiranje nije bilo dozvoljeno ovde.", "Парковка здесь не была разрешена."),
                cell("Negative (-)", "Present", "Zabranjeno je / Nije moguće", "Ovde je zabranjeno fotografisanje.", "Здесь запрещено фотографировать."),
                cell("Negative (-)", "Future", "Neće biti otvoreno", "U nedelju banke neće biti otvorene.", "В воскресенье банки не будут открыты."),
                cell("Interrogative (?)", "Past", "Da li je bilo moguće?", "Da li je bilo potrebno poneti pasoš?", "Нужно было взять паспорт?"),
                cell("Interrogative (?)", "Present", "Da li je dozvoljeno / moguće?", "Da li je moguće rezervisati sto?", "Возможно забронировать столик?"),
                cell("Interrogative (?)", "Future", "Da li će biti otvoreno?", "Hoće li muzej biti otvoren u subotu?", "Музей будет открыт в субботу?")
            )
        ),
        grammarGuide(
            idx = 16,
            title = "Итоговый синтез (Полиглот)",
            summary = "Свободное комбинирование времён, модальности, падежей и сложных союзов",
            explanationMarkdown = """
                ### Završna sinteza (Poliglot B1)
                На этом этапе вы свободно соединяете все блоки:
                1. **Время и вид:** *Naučio sam* (прош.) / *Vežbam* (наст.) / *Govoriću tečno* (буд.).
                2. **Клитики и датив:** *Javiću **ti se** čim stignem.*
                3. **Сложные связи:** *Iako je gramatika bila teža, sada se lako sporazumevamo.*
            """,
            verbInfinitive = "Sporazumeti se",
            verbTranslation = "Договориться / Понять друг друга",
            aspect = "Svršeni (Perfective)",
            cells = listOf(
                cell("Affirmative (+)", "Past", "Sporazumeli smo se", "Lako smo se sporazumeli bez rečnika.", "Мы легко договорились без словаря."),
                cell("Affirmative (+)", "Present", "Vežbam i napredujem", "Svaki dan vežbam da bih govorio tečno.", "Каждый день я тренируюсь, чтобы говорить свободно."),
                cell("Affirmative (+)", "Future", "Govorićete tečno", "Posle redovne vežbe govorićete tečno.", "После регулярной практики вы будете говорить свободно."),
                cell("Negative (-)", "Past", "Nismo odustali", "Nismo odustali iako je početak bio težak.", "Мы не сдались, хотя начало было трудным."),
                cell("Negative (-)", "Present", "Ne sumnjam u uspeh", "Ne sumnjam u tvoj brz napredak.", "Я не сомневаюсь в твоём быстром прогрессе."),
                cell("Negative (-)", "Future", "Nećemo zaboraviti", "Nećemo zaboraviti ova korisna pravila.", "Мы не забудем эти полезные правила."),
                cell("Interrogative (?)", "Past", "Da li ste se sporazumeli?", "Da li ste se lako sporazumeli u gradu?", "Вы легко объяснились в городе?"),
                cell("Interrogative (?)", "Present", "Koji je vaš glavni cilj?", "Koji je vaš glavni cilj u učenju?", "Какова ваша главная цель в обучении?"),
                cell("Interrogative (?)", "Future", "Hoćete li nastaviti?", "Hoćete li nastaviti redovnu komunikaciju?", "Вы продолжите регулярное общение?")
            )
        )
    )

    private fun s(
        lessonIdx: Int,
        tense: String,
        form: String,
        personGroup: String,
        ru: String,
        latin: String,
        distractors: List<String>,
        hint: String
    ): SentenceEntity {
        val tokens = latin.split(" ")
            .map { it.trim().lowercase().replace(Regex("[\\p{P}\\p{S}]"), "") }
            .filter { it.isNotBlank() }
        val listAdapter = moshi.adapter<List<String>>(
            com.squareup.moshi.Types.newParameterizedType(List::class.java, String::class.java)
        )
        return SentenceEntity(
            id = "s_${lessonIdx}_${tense.lowercase()}_${form.lowercase()}",
            lessonId = "lesson_$lessonIdx",
            sourceSentence = ru,
            targetSentence = latin,
            targetSentenceLatin = latin,
            tokensJson = listAdapter.toJson(tokens),
            distractorTokensJson = listAdapter.toJson(distractors.take(2)),
            grammarHint = hint,
            matrixTense = tense,
            matrixForm = form,
            personGroup = personGroup
        )
    }

    fun getInitialSentences(): List<SentenceEntity> = listOf(
        // Lesson 1: Презент и базовая матрица глагола
        s(1, "PAST", "AFFIRMATIVE", "2SG_PL", "Мы вчера внимательно слушали профессора.", "Mi smo juče pažljivo slušali profesora.", listOf("ste", "slušamo"), "Perfekat (+): smo slušali"),
        s(1, "PAST", "NEGATIVE", "1SG", "Я вчера не работал в офисе.", "Ja juče nisam radio u kancelariji.", listOf("sam", "ne"), "Perfekat (-): nisam radio"),
        s(1, "PAST", "INTERROGATIVE", "2SG_PL", "Ты вчера читал эту книгу?", "Da li si juče čitao ovu knjigu?", listOf("sam", "čitaš"), "Perfekat (?): da li si čitao"),
        s(1, "PRESENT", "AFFIRMATIVE", "1SG", "Я учу новые слова каждый день.", "Ja učim nove reči svaki dan.", listOf("učiš", "učio"), "Prezent (+): učim"),
        s(1, "PRESENT", "NEGATIVE", "1SG", "Я не говорю по-английски быстро.", "Ja ne govorim engleski brzo.", listOf("govoriš", "nisam"), "Prezent (-): ne govorim"),
        s(1, "PRESENT", "INTERROGATIVE", "2SG_PL", "Ты работаешь сегодня в офисе?", "Da li danas radiš u kancelariji?", listOf("radim", "jesi"), "Prezent (?): da li radiš"),
        s(1, "FUTURE", "AFFIRMATIVE", "1SG", "Завтра я буду работать дома.", "Sutra ću raditi kod kuće.", listOf("ćeš", "biću"), "Futur I (+): ću raditi"),
        s(1, "FUTURE", "NEGATIVE", "1SG", "Завтра я не буду писать отчёт.", "Sutra neću pisati izveštaj.", listOf("ću", "nisam"), "Futur I (-): neću pisati"),
        s(1, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты будешь читать этот текст завтра?", "Hoćeš li sutra čitati ovaj tekst?", listOf("ću", "jesi"), "Futur I (?): hoćeš li čitati"),

        // Lesson 2: Возвратные глаголы с частицей 'se'
        s(2, "PAST", "AFFIRMATIVE", "1SG", "Я хорошо помнил тот красивый день.", "Dobro sam se sećao tog lepog dana.", listOf("si", "sećam"), "Perfekat + se: sam se sećao"),
        s(2, "PAST", "NEGATIVE", "1SG", "Я не боялся сложных вопросов.", "Nisam se plašio teških pitanja.", listOf("sam", "ne"), "Perfekat (-): nisam se plašio"),
        s(2, "PAST", "INTERROGATIVE", "2SG_PL", "Ты отдыхал после работы вчера?", "Da li si se odmarao posle posla?", listOf("sam", "odmaraš"), "Perfekat (?): da li si se odmarao"),
        s(2, "PRESENT", "AFFIRMATIVE", "1SG", "Я хорошо помню этот красивый город.", "Dobro se sećam ovog lepog grada.", listOf("sećaš", "si"), "Prezent + se: dobro se sećam"),
        s(2, "PRESENT", "NEGATIVE", "1SG", "Я не боюсь говорить по-сербски.", "Ne plašim se da govorim srpski.", listOf("plašiš", "nisam"), "Prezent (-): ne plašim se"),
        s(2, "PRESENT", "INTERROGATIVE", "2SG_PL", "Каким спортом ты занимаешься?", "Kojim sportom se baviš?", listOf("bavim", "bave"), "Prezent (?): se baviš"),
        s(2, "FUTURE", "AFFIRMATIVE", "1SG", "Я буду радоваться нашему успеху.", "Radovaću se našem uspehu.", listOf("ćeš", "radujem"), "Futur I + se: radovaću se"),
        s(2, "FUTURE", "NEGATIVE", "1SG", "Мы не будем смеяться над ошибкой.", "Nećemo se smejati grešci.", listOf("ćemo", "nismo"), "Futur I (-): nećemo se smejati"),
        s(2, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты будешь отдыхать на выходных?", "Da li ćeš se odmarati za vikend?", listOf("ću", "odmaraš"), "Futur I (?): da li ćeš se odmarati"),

        // Lesson 3: Модальные конструкции (hteti / moći / morati)
        s(3, "PAST", "AFFIRMATIVE", "1SG", "Я должен был сразу уйти.", "Morao sam odmah da krenem.", listOf("moram", "krenuo"), "Perfekat: morao sam da krenem"),
        s(3, "PAST", "NEGATIVE", "1SG", "Я не мог прийти на встречу.", "Nisam mogao da dođem na sastanak.", listOf("sam", "neću"), "Perfekat (-): nisam mogao da..."),
        s(3, "PAST", "INTERROGATIVE", "2SG_PL", "Ты должен был ждать долго?", "Da li si morao dugo da čekaš?", listOf("moram", "čekao"), "Perfekat (?): da li si morao da..."),
        s(3, "PRESENT", "AFFIRMATIVE", "1SG", "Я хочу говорить по-сербски свободно.", "Ja hoću da govorim srpski tečno.", listOf("neću", "govoriti"), "Prezent: hoću + da + prezent"),
        s(3, "PRESENT", "NEGATIVE", "1SG", "Здесь вы не смеете парковать машину.", "Ovde ne smete da parkirate auto.", listOf("smete", "parkirati"), "Prezent (-): ne smete da..."),
        s(3, "PRESENT", "INTERROGATIVE", "2SG_PL", "Ты можешь повторить это слово?", "Možeš li da ponoviš ovu reč?", listOf("mogu", "ponoviti"), "Prezent (?): možeš li da..."),
        s(3, "FUTURE", "AFFIRMATIVE", "1SG", "Завтра я смогу помочь команде.", "Sutra ću moći da pomognem timu.", listOf("mogu", "pomoći"), "Futur I: ću moći da..."),
        s(3, "FUTURE", "NEGATIVE", "1SG", "Я не смогу прийти на встречу.", "Neću moći da dođem na sastanak.", listOf("ću", "nisam"), "Futur I (-): neću moći da..."),
        s(3, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты сможешь закончить работу вовремя?", "Hoćeš li moći da završiš posao na vreme?", listOf("ću", "završiti"), "Futur I (?): hoćeš li moći da..."),

        // Lesson 4: Прошедшее время (Перфекат)
        s(4, "PAST", "AFFIRMATIVE", "1SG", "Вчера я купил новый словарь.", "Juče sam kupio nov rečnik.", listOf("si", "kupujem"), "Perfekat (+): sam kupio"),
        s(4, "PAST", "NEGATIVE", "1SG", "Я не забыл о твоём звонке.", "Nisam zaboravio na tvoj poziv.", listOf("sam", "zaboravljam"), "Perfekat (-): nisam zaboravio"),
        s(4, "PAST", "INTERROGATIVE", "2SG_PL", "Вы посетили музей на прошлой неделе?", "Da li ste posetili muzej prošle nedelje?", listOf("smo", "posetite"), "Perfekat (?): da li ste posetili"),
        s(4, "PRESENT", "AFFIRMATIVE", "1SG", "Сейчас я внимательно слушаю рассказ.", "Sada pažljivo slušam priču.", listOf("slušao", "slušaš"), "Prezent (+): slušam"),
        s(4, "PRESENT", "NEGATIVE", "1SG", "Я не забываю новые слова.", "Ne zaboravljam nove reči.", listOf("zaboravio", "nisam"), "Prezent (-): ne zaboravljam"),
        s(4, "PRESENT", "INTERROGATIVE", "2SG_PL", "Ты видишь нашего старого друга?", "Da li vidiš našeg starog prijatelja?", listOf("vidim", "video"), "Prezent (?): da li vidiš"),
        s(4, "FUTURE", "AFFIRMATIVE", "1SG", "Мы закончим проект до пятницы.", "Završićemo projekat pre petka.", listOf("će", "završili"), "Futur I (+): završićemo"),
        s(4, "FUTURE", "NEGATIVE", "1SG", "Я не забуду о важном событии.", "Neću zaboraviti na važan događaj.", listOf("ću", "nisam"), "Futur I (-): neću zaboraviti"),
        s(4, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты посетишь нас в этом месяце?", "Da li ćeš nas posetiti ovog meseca?", listOf("ću", "posetio"), "Futur I (?): da li ćeš posetiti"),

        // Lesson 5: Будущее время I (Футур I)
        s(5, "PAST", "AFFIRMATIVE", "1SG", "В прошлом году мы путешествовали поездом.", "Prošle godine smo putovali vozom.", listOf("ste", "putujemo"), "Perfekat (+): smo putovali"),
        s(5, "PAST", "NEGATIVE", "1SG", "Он не позвонил мне вчера вечером.", "Nije mi pozvao juče uveče.", listOf("je", "neće"), "Perfekat (-): nije pozvao"),
        s(5, "PAST", "INTERROGATIVE", "2SG_PL", "Вы успели на поезд вовремя?", "Da li ste stigli na voz na vreme?", listOf("smo", "stižete"), "Perfekat (?): da li ste stigli"),
        s(5, "PRESENT", "AFFIRMATIVE", "1SG", "Мы планируем летний отпуск в июле.", "Planiramo letnji odmor u julu.", listOf("planirao", "planirate"), "Prezent (+): planiramo"),
        s(5, "PRESENT", "NEGATIVE", "1SG", "На этой неделе я не путешествую.", "Ove nedelje ne putujem.", listOf("putovao", "neću"), "Prezent (-): ne putujem"),
        s(5, "PRESENT", "INTERROGATIVE", "2SG_PL", "Что ты планируешь на выходные?", "Šta planiraš za vikend?", listOf("planiram", "planirao"), "Prezent (?): šta planiraš"),
        s(5, "FUTURE", "AFFIRMATIVE", "1SG", "Завтра я поеду в Нови-Сад.", "Sutra ću putovati u Novi Sad.", listOf("ćeš", "putovao"), "Futur I (+): ću putovati"),
        s(5, "FUTURE", "NEGATIVE", "1SG", "Я не поеду без паспорта.", "Neću putovati bez pasoša.", listOf("ću", "nisam"), "Futur I (-): neću putovati"),
        s(5, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты позвонишь, как только прибудешь?", "Hoćeš li pozvati čim stigneš?", listOf("ću", "pozvao"), "Futur I (?): hoćeš li pozvati"),

        // Lesson 6: Видовые пары глаголов
        s(6, "PAST", "AFFIRMATIVE", "1SG", "Я прочитал всю статью за десять минут.", "Pročitao sam ceo članak za deset minuta.", listOf("čitao", "si"), "Совершенный вид (прош.): pročitao sam"),
        s(6, "PAST", "NEGATIVE", "1SG", "Я ещё не написал этот отчёт.", "Još nisam napisao ovaj izveštaj.", listOf("sam", "pisao"), "Совершенный вид (-): nisam napisao"),
        s(6, "PAST", "INTERROGATIVE", "2SG_PL", "Ты выучил все новые правила?", "Da li si naučio sva nova pravila?", listOf("učio", "sam"), "Совершенный вид (?): da li si naučio"),
        s(6, "PRESENT", "AFFIRMATIVE", "1SG", "По субботам я покупаю свежие фрукты.", "Subotom kupujem sveže voće.", listOf("kupim", "kupovao"), "Несовершенный вид (наст.): kupujem"),
        s(6, "PRESENT", "NEGATIVE", "1SG", "Сейчас я не открываю окно.", "Sada ne otvaram prozor.", listOf("otvorim", "nisam"), "Несовершенный вид (-): ne otvaram"),
        s(6, "PRESENT", "INTERROGATIVE", "2SG_PL", "Ты регулярно повторяешь старые уроки?", "Da li redovno ponavljaš stare lekcije?", listOf("ponoviš", "ponavljam"), "Несовершенный вид (?): da li ponavljaš"),
        s(6, "FUTURE", "AFFIRMATIVE", "1SG", "Мы сделаем отличную презентацию.", "Napravićemo odličnu prezentaciju.", listOf("pravimo", "napravili"), "Совершенный вид (буд.): napravićemo"),
        s(6, "FUTURE", "NEGATIVE", "1SG", "Я не буду покупать лишних вещей.", "Neću kupovati suvišne stvari.", listOf("ću", "kupiti"), "Несовершенный вид (-): neću kupovati"),
        s(6, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты откроешь окно в комнате?", "Hoćeš li otvoriti prozor u sobi?", listOf("otvaraš", "ću"), "Совершенный вид (?): hoćeš li otvoriti"),

        // Lesson 7: Повелительное наклонение (Императив)
        s(7, "PAST", "AFFIRMATIVE", "1SG", "Вчера я помог другу с переводом.", "Juče sam pomogao prijatelju sa prevodom.", listOf("pomognem", "si"), "Perfekat: sam pomogao"),
        s(7, "PAST", "NEGATIVE", "1SG", "Я не стал ждать перед входом.", "Nisam čekao ispred ulaza.", listOf("sam", "čekaj"), "Perfekat (-): nisam čekao"),
        s(7, "PAST", "INTERROGATIVE", "2SG_PL", "Ты отправил адрес сообщением?", "Da li si poslao adresu porukom?", listOf("pošalji", "sam"), "Perfekat (?): da li si poslao"),
        s(7, "PRESENT", "AFFIRMATIVE", "2SG_PL", "Пожалуйста, помоги мне с этим заданием!", "Molim te, pomozi mi sa ovim zadatkom!", listOf("pomažeš", "pomogao"), "Imperativ: pomozi!"),
        s(7, "PRESENT", "NEGATIVE", "2SG_PL", "Не забывай закрывать дверь!", "Nemoj da zaboravljaš da zatvoriš vrata!", listOf("nemojte", "zaboravio"), "Imperativ (-): nemoj da..."),
        s(7, "PRESENT", "INTERROGATIVE", "2SG_PL", "Можете открыть книги на этой странице?", "Možete li da otvorite knjige na ovoj strani?", listOf("otvori", "mogu"), "Вежливая просьба: možete li da..."),
        s(7, "FUTURE", "AFFIRMATIVE", "2SG_PL", "Давайте отправимся прямо сейчас!", "Krenimo odmah!", listOf("krenite", "krećem"), "Imperativ 1PL: krenimo!"),
        s(7, "FUTURE", "NEGATIVE", "1SG", "Мы не опоздаем на встречу.", "Nećemo kasniti na sastanak.", listOf("ćemo", "nismo"), "Futur I (-): nećemo kasniti"),
        s(7, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты подождёшь меня перед входом?", "Hoćeš li me sačekati ispred ulaza?", listOf("sačekaj", "ću"), "Futur I (?): hoćeš li sačekati"),

        // Lesson 8: Условное наклонение (Потенциал)
        s(8, "PAST", "AFFIRMATIVE", "1SG", "Я хотел посетить старый город.", "Želeo sam da posetim stari grad.", listOf("bih", "želim"), "Perfekat: želeo sam"),
        s(8, "PAST", "NEGATIVE", "1SG", "Мы не выбрали этот вариант.", "Nismo izabrali ovu opciju.", listOf("bismo", "smo"), "Perfekat (-): nismo izabrali"),
        s(8, "PAST", "INTERROGATIVE", "2SG_PL", "Вы предложили новый план вчера?", "Da li ste predložili novi plan juče?", listOf("biste", "predložite"), "Perfekat (?): da li ste predložili"),
        s(8, "PRESENT", "AFFIRMATIVE", "1SG", "Я хотел бы заказать домашний кофе.", "Želeo bih da naručim domaću kafu.", listOf("sam", "ću"), "Potencijal (+): želeo bih"),
        s(8, "PRESENT", "NEGATIVE", "1SG", "Я не стал бы предлагать это решение.", "Ne bih predložio to rešenje.", listOf("bih", "nisam"), "Potencijal (-): ne bih predložio"),
        s(8, "PRESENT", "INTERROGATIVE", "2SG_PL", "Что бы вы нам порекомендовали?", "Šta biste nam preporučili?", listOf("bih", "ste"), "Potencijal (?): šta biste preporučili"),
        s(8, "FUTURE", "AFFIRMATIVE", "1SG", "Я с удовольствием пришёл бы завтра.", "Rado bih došao sutra.", listOf("sam", "će"), "Potencijal (+): rado bih došao"),
        s(8, "FUTURE", "NEGATIVE", "1SG", "Я не откажусь от вашего совета.", "Neću odbiti vaš savet.", listOf("bih", "ću"), "Futur I (-): neću odbiti"),
        s(8, "FUTURE", "INTERROGATIVE", "2SG_PL", "Вы воспользовались бы этой возможностью?", "Da li biste iskoristili ovu priliku?", listOf("bismo", "ste"), "Potencijal (?): da li biste iskoristili"),

        // Lesson 9: Глаголы движения
        s(9, "PAST", "AFFIRMATIVE", "1SG", "Мы вышли из здания на свежий воздух.", "Izašli smo iz zgrade na svež vazduh.", listOf("ušli", "smo"), "Perfekat: izašli smo iz..."),
        s(9, "PAST", "NEGATIVE", "1SG", "Он не уехал из города вчера.", "Nije otišao iz grada juče.", listOf("je", "došao"), "Perfekat (-): nije otišao"),
        s(9, "PAST", "INTERROGATIVE", "2SG_PL", "Вы прошли мимо старой крепости?", "Da li ste prošli pored stare tvrđave?", listOf("smo", "prođete"), "Perfekat (?): da li ste prošli"),
        s(9, "PRESENT", "AFFIRMATIVE", "1SG", "Каждое утро я иду пешком на работу.", "Svako jutro idem peške na posao.", listOf("ideš", "išao"), "Prezent (+): idem peške na..."),
        s(9, "PRESENT", "NEGATIVE", "1SG", "Сейчас я не вхожу в зал.", "Sada ne ulazim u salu.", listOf("izlazim", "nisam"), "Prezent (-): ne ulazim"),
        s(9, "PRESENT", "INTERROGATIVE", "2SG_PL", "Куда ты идёшь так быстро?", "Kuda ideš tako brzo?", listOf("idem", "išao"), "Prezent (?): kuda ideš"),
        s(9, "FUTURE", "AFFIRMATIVE", "2SG_PL", "Мы вернёмся домой поздно вечером.", "Vratićemo se kući kasno uveče.", listOf("ćete", "vratili"), "Futur I (+): vratićemo se"),
        s(9, "FUTURE", "NEGATIVE", "1SG", "Я не уйду до конца встречи.", "Neću otići pre kraja sastanka.", listOf("ću", "doći"), "Futur I (-): neću otići"),
        s(9, "FUTURE", "INTERROGATIVE", "2SG_PL", "Когда ты придёшь на остановку?", "Kada ćeš doći na stanicu?", listOf("ću", "došao"), "Futur I (?): kada ćeš doći"),

        // Lesson 10: Дательный падеж и клитики
        s(10, "PAST", "AFFIRMATIVE", "1SG", "Профессор ясно объяснил нам правило.", "Profesor nam je jasno objasnio pravilo.", listOf("vam", "smo"), "Dativ клитика: nam je objasnio"),
        s(10, "PAST", "NEGATIVE", "1SG", "Я не сказал ему адрес вчера.", "Nisam mu rekao adresu juče.", listOf("joj", "sam"), "Dativ (-): nisam mu rekao"),
        s(10, "PAST", "INTERROGATIVE", "2SG_PL", "Ты поздравил своего друга?", "Da li si čestitao svom prijatelju?", listOf("sam", "čestitaš"), "Dativ (?): čestitao prijatelju"),
        s(10, "PRESENT", "AFFIRMATIVE", "1SG", "Я полностью верю тебе на слово.", "Potpuno ti verujem na reč.", listOf("mi", "veruješ"), "Dativ клитика: ti verujem"),
        s(10, "PRESENT", "NEGATIVE", "1SG", "Это время мне не подходит.", "Ovaj termin mi ne odgovara.", listOf("ti", "odgovaram"), "Dativ (-): mi ne odgovara"),
        s(10, "PRESENT", "INTERROGATIVE", "2SG_PL", "Вам подходит встреча в среду?", "Da li vam odgovara sastanak u sredu?", listOf("nam", "odgovaram"), "Dativ (?): da li vam odgovara"),
        s(10, "FUTURE", "AFFIRMATIVE", "1SG", "Я позвоню тебе сразу после работы.", "Javiću ti se odmah posle posla.", listOf("mi", "javio"), "Dativ клитика: javiću ti se"),
        s(10, "FUTURE", "NEGATIVE", "1SG", "Я не буду мешать вам сегодня.", "Neću vam smetati danas.", listOf("nam", "ću"), "Futur I (-): neću vam smetati"),
        s(10, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты дашь мне синюю ручку?", "Hoćeš li mi dati plavu olovku?", listOf("ti", "ću"), "Dativ (?): hoćeš li mi dati"),

        // Lesson 11: Винительный и локатив
        s(11, "PAST", "AFFIRMATIVE", "2SG_PL", "Вчера мы гуляли по старому городу.", "Juče smo šetali po starom gradu.", listOf("grad", "ste"), "Lokativ (где): po starom gradu"),
        s(11, "PAST", "NEGATIVE", "1SG", "Утром я не был в аэропорту.", "Jutros nisam bio na aerodromu.", listOf("aerodrom", "sam"), "Lokativ (-): na aerodromu"),
        s(11, "PAST", "INTERROGATIVE", "2SG_PL", "Ты ходил в книжный магазин вчера?", "Da li si išao u knjižaru juče?", listOf("knjižari", "sam"), "Akuzativ (куда): u knjižaru"),
        s(11, "PRESENT", "AFFIRMATIVE", "2SG_PL", "Коллеги уже находятся в офисе.", "Kolege se već nalaze u kancelariji.", listOf("kancelariju", "nalazim"), "Lokativ (где): u kancelariji"),
        s(11, "PRESENT", "NEGATIVE", "1SG", "Ключей нет на большом столе.", "Ključevi nisu na velikom stolu.", listOf("sto", "su"), "Lokativ (-): na stolu"),
        s(11, "PRESENT", "INTERROGATIVE", "2SG_PL", "Где находится главная площадь?", "Gde se nalazi glavni trg?", listOf("trgu", "kuda"), "Lokativ (?): gde se nalazi"),
        s(11, "FUTURE", "AFFIRMATIVE", "2SG_PL", "Вечером мы пойдём в театр.", "Večeras ćemo ići u pozorište.", listOf("pozorištu", "idemo"), "Akuzativ (куда): u pozorište"),
        s(11, "FUTURE", "NEGATIVE", "1SG", "Завтра мы не останемся в квартире.", "Sutra nećemo ostati u stanu.", listOf("stan", "ćemo"), "Lokativ (-): u stanu"),
        s(11, "FUTURE", "INTERROGATIVE", "2SG_PL", "Вы пойдёте в городской парк?", "Hoćete li ići u gradski park?", listOf("parku", "ćemo"), "Akuzativ (?): u gradski park"),

        // Lesson 12: Притяжательные местоимения
        s(12, "PAST", "AFFIRMATIVE", "1SG", "Её идея была очень полезной.", "Njena ideja je bila veoma korisna.", listOf("njegova", "bilo"), "Притяжательное: njena (ж. р.)"),
        s(12, "PAST", "NEGATIVE", "1SG", "Это не был их план.", "To nije bio njihov plan.", listOf("naš", "bila"), "Притяжательное (-): njihov"),
        s(12, "PAST", "INTERROGATIVE", "2SG_PL", "Твой брат был вчера в офисе?", "Da li je tvoj brat bio juče u kancelariji?", listOf("moj", "bila"), "Притяжательное (?): tvoj brat"),
        s(12, "PRESENT", "AFFIRMATIVE", "2SG_PL", "Наша команда работает над новым проектом.", "Naš tim radi na novom projektu.", listOf("vaš", "radimo"), "Притяжательное: naš tim"),
        s(12, "PRESENT", "NEGATIVE", "1SG", "Это не мой телефон на столе.", "Ovo nije moj telefon na stolu.", listOf("tvoj", "je"), "Притяжательное (-): nije moj"),
        s(12, "PRESENT", "INTERROGATIVE", "2SG_PL", "Это ваш новый сосед?", "Da li je ovo vaš novi komšija?", listOf("naš", "vaša"), "Притяжательное (?): vaš komšija"),
        s(12, "FUTURE", "AFFIRMATIVE", "1SG", "Моя семья посетит Белград летом.", "Moja porodica će posetiti Beograd na leto.", listOf("moj", "ću"), "Притяжательное: moja porodica"),
        s(12, "FUTURE", "NEGATIVE", "1SG", "Мы не будем менять наше расписание.", "Nećemo menjati naš raspored.", listOf("naša", "ćemo"), "Притяжательное (-): naš raspored"),
        s(12, "FUTURE", "INTERROGATIVE", "2SG_PL", "Его брат придёт к нам завтра?", "Hoće li njegov brat doći kod nas sutra?", listOf("njen", "došao"), "Притяжательное (?): njegov brat"),

        // Lesson 13: Степени сравнения
        s(13, "PAST", "AFFIRMATIVE", "1SG", "Второе задание было легче первого.", "Drugi zadatak je bio lakši od prvog.", listOf("teži", "lako"), "Komparativ: lakši od..."),
        s(13, "PAST", "NEGATIVE", "1SG", "Этот отель не был дороже.", "Taj hotel nije bio skuplji.", listOf("jeftiniji", "je"), "Komparativ (-): nije bio skuplji"),
        s(13, "PAST", "INTERROGATIVE", "2SG_PL", "Какой метод был лучше для вас?", "Koji metod je bio bolji za vas?", listOf("gori", "dobar"), "Komparativ (?): bio bolji"),
        s(13, "PRESENT", "AFFIRMATIVE", "1SG", "Поезд быстрее автобуса на этой линии.", "Voz je brži od autobusa na ovoj liniji.", listOf("sporiji", "nego"), "Komparativ: brži od..."),
        s(13, "PRESENT", "NEGATIVE", "1SG", "Погода сегодня не хуже, чем вчера.", "Vreme danas nije gore nego juče.", listOf("bolje", "od"), "Komparativ (-): nije gore nego..."),
        s(13, "PRESENT", "INTERROGATIVE", "2SG_PL", "Фрукты дешевле на рынке?", "Da li je voće jeftinije na pijaci?", listOf("skuplje", "jeftino"), "Komparativ (?): jeftinije"),
        s(13, "FUTURE", "AFFIRMATIVE", "1SG", "Следующий шаг будет самым важным.", "Sledeći korak će biti najvažniji.", listOf("važniji", "bio"), "Superlativ: najvažniji"),
        s(13, "FUTURE", "NEGATIVE", "1SG", "Следующий тест не будет сложнее.", "Sledeći test neće biti teži.", listOf("lakši", "će"), "Komparativ (-): neće biti teži"),
        s(13, "FUTURE", "INTERROGATIVE", "2SG_PL", "Новый стол будет больше старого?", "Hoće li novi sto biti veći od starog?", listOf("manji", "će"), "Komparativ (?): veći od..."),

        // Lesson 14: Сложные союзы
        s(14, "PAST", "AFFIRMATIVE", "2SG_PL", "Мы вышли на прогулку, хотя было холодно.", "Izašli smo u šetnju iako je bilo hladno.", listOf("zato", "dok"), "Уступка: iako"),
        s(14, "PAST", "NEGATIVE", "2SG_PL", "Мы не пошли в парк, потому что шёл дождь.", "Nismo išli u park zato što je padala kiša.", listOf("iako", "čim"), "Причина: zato što"),
        s(14, "PAST", "INTERROGATIVE", "2SG_PL", "Почему ты ушёл, когда начался дождь?", "Zašto si otišao kada je počela kiša?", listOf("iako", "dok"), "Время: kada"),
        s(14, "PRESENT", "AFFIRMATIVE", "1SG", "Я читаю новости, пока пью кофе.", "Čitam vesti dok pijem kafu.", listOf("čim", "iako"), "Время: dok"),
        s(14, "PRESENT", "NEGATIVE", "1SG", "Я не выхожу, если плохая погода.", "Ne izlazim ako je loše vreme.", listOf("zato", "iako"), "Условие: ako"),
        s(14, "PRESENT", "INTERROGATIVE", "2SG_PL", "Что ты слушаешь, пока едешь на работу?", "Šta slušaš dok ideš na posao?", listOf("ako", "zato"), "Время (?): dok ideš"),
        s(14, "FUTURE", "AFFIRMATIVE", "1SG", "Я позвоню, как только закончу работу.", "Pozvaću te čim završim posao.", listOf("dok", "iako"), "Время: čim"),
        s(14, "FUTURE", "NEGATIVE", "2SG_PL", "Мы не отправимся, прежде чем проверим билет.", "Nećemo krenuti pre nego što proverimo kartu.", listOf("čim", "pošto"), "Время: pre nego što"),
        s(14, "FUTURE", "INTERROGATIVE", "2SG_PL", "Ты позвонишь, когда приедешь в город?", "Hoćeš li se javiti kada stigneš u grad?", listOf("dok", "iako"), "Время (?): kada stigneš"),

        // Lesson 15: Пассив и безличные конструкции
        s(15, "PAST", "AFFIRMATIVE", "1SG", "Вчера музей был открыт весь день.", "Juče je muzej bio otvoren ceo dan.", listOf("zatvoren", "bilo"), "Причастие: bio otvoren"),
        s(15, "PAST", "NEGATIVE", "1SG", "Парковка здесь не была разрешена.", "Parkiranje ovde nije bilo dozvoljeno.", listOf("zabranjeno", "je"), "Безличное (-): nije bilo dozvoljeno"),
        s(15, "PAST", "INTERROGATIVE", "2SG_PL", "Нужно было взять паспорт?", "Da li je bilo potrebno poneti pasoš?", listOf("moguće", "bila"), "Безличное (?): bilo potrebno"),
        s(15, "PRESENT", "AFFIRMATIVE", "1SG", "Здесь говорят по-сербски.", "Ovde se govori srpski jezik.", listOf("govore", "je"), "Безлично-возвратное: se govori"),
        s(15, "PRESENT", "NEGATIVE", "1SG", "Здесь запрещено фотографировать.", "Ovde je zabranjeno fotografisanje.", listOf("dozvoljeno", "nije"), "Предикатив: zabranjeno je"),
        s(15, "PRESENT", "INTERROGATIVE", "2SG_PL", "Возможно забронировать столик на вечер?", "Da li je moguće rezervisati sto za veče?", listOf("zabranjeno", "su"), "Безличное (?): da li je moguće"),
        s(15, "FUTURE", "AFFIRMATIVE", "1SG", "Важно, чтобы мы проверили детали.", "Važno je da proverimo detalje.", listOf("dosadno", "proverili"), "Безличное: važno je da..."),
        s(15, "FUTURE", "NEGATIVE", "1SG", "В воскресенье банки не будут открыты.", "U nedelju banke neće biti otvorene.", listOf("zatvorene", "će"), "Будущее (-): neće biti otvorene"),
        s(15, "FUTURE", "INTERROGATIVE", "2SG_PL", "Музей будет открыт завтра?", "Hoće li muzej biti otvoren sutra?", listOf("zatvoren", "bio"), "Будущее (?): hoće li biti otvoren"),

        // Lesson 16: Итоговый синтез (Полиглот)
        s(16, "PAST", "AFFIRMATIVE", "2SG_PL", "Мы легко договорились без словаря.", "Lako smo se sporazumeli bez rečnika.", listOf("ste", "sporazumemo"), "Синтез (прош.): smo se sporazumeli"),
        s(16, "PAST", "NEGATIVE", "2SG_PL", "Мы не забыли эти важные слова.", "Nismo zaboravili ove važne reči.", listOf("smo", "zaboravljamo"), "Синтез (-): nismo zaboravili"),
        s(16, "PAST", "INTERROGATIVE", "2SG_PL", "Вы легко объяснились в Белграде?", "Da li ste se lako sporazumeli u Beogradu?", listOf("smo", "sporazumete"), "Синтез (?): da li ste se sporazumeli"),
        s(16, "PRESENT", "AFFIRMATIVE", "1SG", "Я тренируюсь каждый день, чтобы говорить свободно.", "Vežbam svaki dan da bih govorio tečno.", listOf("bih", "govorim"), "Синтез (наст.): vežbam da bih govorio"),
        s(16, "PRESENT", "NEGATIVE", "1SG", "Я не сомневаюсь в твоём успехе.", "Ne sumnjam u tvoj uspeh.", listOf("sumnjaš", "nisam"), "Синтез (-): ne sumnjam"),
        s(16, "PRESENT", "INTERROGATIVE", "2SG_PL", "Какова ваша главная цель в обучении?", "Koji je vaš glavni cilj u učenju?", listOf("naš", "su"), "Синтез (?): koji je vaš cilj"),
        s(16, "FUTURE", "AFFIRMATIVE", "2SG_PL", "После практики вы будете говорить свободно.", "Posle vežbe govorićete tečno.", listOf("govorite", "govorili"), "Синтез (буд.): govorićete tečno"),
        s(16, "FUTURE", "NEGATIVE", "1SG", "Мы не забудем эти полезные правила.", "Nećemo zaboraviti ova korisna pravila.", listOf("ćemo", "nismo"), "Синтез (-): nećemo zaboraviti"),
        s(16, "FUTURE", "INTERROGATIVE", "2SG_PL", "Вы продолжите ежедневное общение?", "Hoćete li nastaviti svakodnevnu komunikaciju?", listOf("ćemo", "nastavili"), "Синтез (?): hoćete li nastaviti")
    )
}
