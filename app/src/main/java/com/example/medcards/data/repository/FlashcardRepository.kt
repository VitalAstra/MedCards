package com.example.medcards.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.medcards.data.model.Flashcard
import com.example.medcards.data.model.QuizQuestion
import com.example.medcards.data.model.QuizStats
import com.example.medcards.data.seed.StudyContent
import com.example.medcards.domain.Rating
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

data class SubjectStats(
    val subject: String,
    val total: Int,
    val reviewed: Int
)

data class ReviewStats(
    val reviewedToday: Int,
    val totalReviews: Int,
    val masteredCards: Int,
    val streakDays: Int,
    val dueCards: Int = 0
)

interface FlashcardRepository {
    fun getAllFlashcards(): List<Flashcard>

    fun getFlashcardsBySubject(subject: String): List<Flashcard>

    fun getDueFlashcards(subject: String? = null, nowEpochMillis: Long = System.currentTimeMillis()): List<Flashcard>

    fun getQuizQuestions(): List<QuizQuestion>

    fun getDueQuizQuestions(nowEpochMillis: Long = System.currentTimeMillis()): List<QuizQuestion>

    fun getQuizStats(nowEpochMillis: Long = System.currentTimeMillis()): QuizStats

    fun updateQuizQuestionProgress(question: QuizQuestion, isCorrect: Boolean)

    fun getSubjectStats(): List<SubjectStats>

    fun getReviewStats(): ReviewStats

    fun updateCardProgress(card: Flashcard, rating: Rating)
}

class MockFlashcardRepositoryImpl : FlashcardRepository {
    private val flashcards = mutableListOf(
        Flashcard(
            "anatomy-1",
            "Anatomía",
            "¿Cuál es el hueso más largo del cuerpo humano?",
            "El fémur"
        ),
        Flashcard(
            "pharmacology-1",
            "Farmacología",
            "¿A qué grupo pertenece el ibuprofeno?",
            "Antiinflamatorios no esteroideos (AINE)"
        ),
        Flashcard(
            "histology-1",
            "Histología",
            "¿Qué célula produce anticuerpos?",
            "El plasmocito"
        )
    )
    private val reviewDates = mutableListOf<LocalDate>()
    private val quizQuestions = StudyContent.quizQuestions.toMutableList()
    private val quizAnswers = mutableListOf<Pair<Boolean, Long>>()

    override fun getAllFlashcards(): List<Flashcard> = flashcards.toList()

    override fun getFlashcardsBySubject(subject: String): List<Flashcard> =
        flashcards.filter { it.subject == subject }

    override fun getDueFlashcards(subject: String?, nowEpochMillis: Long): List<Flashcard> =
        flashcards.filter {
            (subject == null || it.subject == subject) &&
                (it.dueAtEpochMillis == 0L || it.dueAtEpochMillis <= nowEpochMillis)
        }

    override fun getQuizQuestions(): List<QuizQuestion> = quizQuestions.toList()

    override fun getDueQuizQuestions(nowEpochMillis: Long): List<QuizQuestion> =
        quizQuestions.filter { it.dueAtEpochMillis == 0L || it.dueAtEpochMillis <= nowEpochMillis }

    override fun getQuizStats(nowEpochMillis: Long): QuizStats {
        val calendar = Calendar.getInstance().apply { timeInMillis = nowEpochMillis }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis
        return QuizStats(
            dueQuestions = getDueQuizQuestions(nowEpochMillis).size,
            totalAnswers = quizAnswers.size,
            correctAnswers = quizAnswers.count { it.first },
            answersToday = quizAnswers.count { it.second >= startOfToday }
        )
    }

    override fun updateQuizQuestionProgress(question: QuizQuestion, isCorrect: Boolean) {
        val index = quizQuestions.indexOfFirst { it.id == question.id }
        require(index >= 0) { "No se encontró la pregunta '${question.id}'." }
        quizQuestions[index] = question
        quizAnswers += isCorrect to System.currentTimeMillis()
    }

    override fun getSubjectStats(): List<SubjectStats> = flashcards
        .groupBy { it.subject }
        .map { (subject, cards) ->
            SubjectStats(
                subject = subject,
                total = cards.size,
                reviewed = cards.count { it.repetitions > 0 }
            )
        }

    override fun getReviewStats(): ReviewStats {
        val today = java.time.LocalDate.now()
        val reviewedDays = reviewDates.toSet()

        return ReviewStats(
            reviewedToday = reviewDates.count { it == today },
            totalReviews = reviewDates.size,
            masteredCards = flashcards.count { it.repetitions >= 3 },
            streakDays = calculateCurrentStreakDays(reviewedDays, today),
            dueCards = getDueFlashcards().size
        )
    }

    override fun updateCardProgress(card: Flashcard, rating: Rating) {
        val index = flashcards.indexOfFirst { it.id == card.id }
        require(index >= 0) { "No se encontró la tarjeta '${card.id}'." }
        flashcards[index] = card
        reviewDates += LocalDate.now()
    }
}

class SQLiteFlashcardRepository(context: Context) : FlashcardRepository {
    private val database = FlashcardDatabase(context.applicationContext)

    override fun getAllFlashcards(): List<Flashcard> = database.readableDatabase.query(
        "flashcards",
        CARD_COLUMNS,
        null,
        null,
        null,
        null,
        "subject, id"
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(cursor.toFlashcard())
        }
    }

    override fun getFlashcardsBySubject(subject: String): List<Flashcard> = database
        .readableDatabase
        .query(
            "flashcards",
            CARD_COLUMNS,
            "subject = ?",
            arrayOf(subject),
            null,
            null,
            "id"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.toFlashcard())
            }
        }

    override fun getDueFlashcards(subject: String?, nowEpochMillis: Long): List<Flashcard> {
        val whereSubject = if (subject == null) "" else "subject = ? AND "
        val arguments = if (subject == null) {
            arrayOf(nowEpochMillis.toString())
        } else {
            arrayOf(subject, nowEpochMillis.toString())
        }
        return database.readableDatabase.query(
            "flashcards",
            CARD_COLUMNS,
            "${whereSubject}(due_at_epoch_millis = 0 OR due_at_epoch_millis <= ?)",
            arguments,
            null,
            null,
            "due_at_epoch_millis, subject, id"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.toFlashcard())
            }
        }
    }

    override fun getQuizQuestions(): List<QuizQuestion> = database.readableDatabase.query(
        "quiz_questions",
        QUIZ_COLUMNS,
        null,
        null,
        null,
        null,
        "subject, topic, id"
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(cursor.toQuizQuestion())
        }
    }

    override fun getDueQuizQuestions(nowEpochMillis: Long): List<QuizQuestion> = database
        .readableDatabase
        .query(
            "quiz_questions",
            QUIZ_COLUMNS,
            "due_at_epoch_millis = 0 OR due_at_epoch_millis <= ?",
            arrayOf(nowEpochMillis.toString()),
            null,
            null,
            "due_at_epoch_millis, subject, topic"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.toQuizQuestion())
            }
        }

    override fun getQuizStats(nowEpochMillis: Long): QuizStats {
        val database = database.readableDatabase
        val calendar = Calendar.getInstance().apply { timeInMillis = nowEpochMillis }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis
        val totals = database.rawQuery(
            "SELECT COUNT(*), COALESCE(SUM(CASE WHEN is_correct = 1 THEN 1 ELSE 0 END), 0), " +
                "COALESCE(SUM(CASE WHEN answered_at >= ? THEN 1 ELSE 0 END), 0) FROM quiz_history",
            arrayOf(startOfToday.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) Triple(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2))
            else Triple(0, 0, 0)
        }
        val dueCount = database.rawQuery(
            "SELECT COUNT(*) FROM quiz_questions WHERE due_at_epoch_millis = 0 OR due_at_epoch_millis <= ?",
            arrayOf(nowEpochMillis.toString())
        ).use { cursor -> if (cursor.moveToFirst()) cursor.getInt(0) else 0 }
        return QuizStats(dueCount, totals.first, totals.second, totals.third)
    }

    override fun updateQuizQuestionProgress(question: QuizQuestion, isCorrect: Boolean) {
        val writableDatabase = database.writableDatabase
        writableDatabase.beginTransaction()
        try {
            val updated = writableDatabase.update(
                "quiz_questions",
                ContentValues().apply {
                    put("repetitions", question.repetitions)
                    put("interval_days", question.intervalDays)
                    put("ease_factor", question.easeFactor)
                    put("due_at_epoch_millis", question.dueAtEpochMillis)
                },
                "id = ?",
                arrayOf(question.id)
            )
            check(updated == 1) { "No se encontró la pregunta '${question.id}'." }
            writableDatabase.insertOrThrow(
                "quiz_history",
                null,
                ContentValues().apply {
                    put("question_id", question.id)
                    put("is_correct", if (isCorrect) 1 else 0)
                    put("answered_at", System.currentTimeMillis())
                }
            )
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }

    override fun getSubjectStats(): List<SubjectStats> {
        val database = database.readableDatabase
        return database.rawQuery(
            "SELECT subject, COUNT(*), SUM(CASE WHEN repetitions > 0 THEN 1 ELSE 0 END) " +
                "FROM flashcards GROUP BY subject ORDER BY subject",
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(SubjectStats(cursor.getString(0), cursor.getInt(1), cursor.getInt(2)))
                }
            }
        }
    }

    override fun getReviewStats(): ReviewStats {
        val database = database.readableDatabase
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis
        val reviewedToday = database.rawQuery(
            "SELECT COUNT(*) FROM review_history WHERE reviewed_at >= ?",
            arrayOf(startOfToday.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
        val totalReviews = database.rawQuery(
            "SELECT COUNT(*) FROM review_history",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
        val masteredCards = database.rawQuery(
            "SELECT COUNT(*) FROM flashcards WHERE repetitions >= 3",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
        val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val reviewedDates = database.rawQuery(
            "SELECT DISTINCT date(reviewed_at / 1000, 'unixepoch', 'localtime') " +
                "FROM review_history ORDER BY 1 DESC",
            null
        ).use { cursor ->
            buildSet {
                while (cursor.moveToNext()) {
                    val date = dateFormatter.parse(cursor.getString(0)) ?: continue
                    val day = Calendar.getInstance().apply { time = date }
                    add(
                        java.time.LocalDate.of(
                            day.get(Calendar.YEAR),
                            day.get(Calendar.MONTH) + 1,
                            day.get(Calendar.DAY_OF_MONTH)
                        )
                    )
                }
            }
        }
        val today = Calendar.getInstance()
        val streakDays = calculateCurrentStreakDays(
            reviewedDates,
            java.time.LocalDate.of(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.DAY_OF_MONTH)
            )
        )
        val dueCards = database.rawQuery(
            "SELECT COUNT(*) FROM flashcards WHERE due_at_epoch_millis = 0 OR due_at_epoch_millis <= ?",
            arrayOf(System.currentTimeMillis().toString())
        ).use { cursor -> if (cursor.moveToFirst()) cursor.getInt(0) else 0 }
        return ReviewStats(reviewedToday, totalReviews, masteredCards, streakDays, dueCards)
    }

    override fun updateCardProgress(card: Flashcard, rating: Rating) {
        val writableDatabase = database.writableDatabase
        val values = ContentValues().apply {
            put("repetitions", card.repetitions)
            put("interval_days", card.intervalDays)
            put("ease_factor", card.easeFactor)
            put("due_at_epoch_millis", card.dueAtEpochMillis)
        }
        writableDatabase.beginTransaction()
        try {
            check(writableDatabase.update("flashcards", values, "id = ?", arrayOf(card.id)) == 1) {
                "No se encontró la tarjeta '${card.id}'."
            }
            writableDatabase.insertOrThrow(
                "review_history",
                null,
                ContentValues().apply {
                    put("card_id", card.id)
                    put("rating", rating.name)
                    put("reviewed_at", System.currentTimeMillis())
                }
            )
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }

    private class FlashcardDatabase(context: Context) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
        init {
            installSeedDatabaseIfNeeded(context)
        }

        override fun onCreate(database: SQLiteDatabase) {
            database.execSQL(CREATE_FLASHCARDS_SQL)
            database.execSQL(CREATE_REVIEW_HISTORY_SQL)
            database.execSQL(CREATE_QUIZ_QUESTIONS_SQL)
            database.execSQL(CREATE_QUIZ_HISTORY_SQL)
            seedAdditionalContent(database)
        }

        override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                database.execSQL("ALTER TABLE flashcards ADD COLUMN image_url TEXT")
            }
            if (oldVersion < 3) {
                database.execSQL(
                    "ALTER TABLE flashcards ADD COLUMN due_at_epoch_millis INTEGER NOT NULL DEFAULT 0"
                )
            }
            if (oldVersion < 4) {
                database.execSQL(CREATE_QUIZ_QUESTIONS_SQL)
                database.execSQL(CREATE_QUIZ_HISTORY_SQL)
                seedAdditionalContent(database)
            }
        }

        private fun seedAdditionalContent(database: SQLiteDatabase) {
            StudyContent.additionalFlashcards.forEach { card ->
                database.insertWithOnConflict(
                    "flashcards",
                    null,
                    ContentValues().apply {
                        put("id", card.id)
                        put("subject", card.subject)
                        put("question", card.question)
                        put("answer", card.answer)
                        put("repetitions", card.repetitions)
                        put("interval_days", card.intervalDays)
                        put("ease_factor", card.easeFactor)
                        put("image_url", card.imageUrl)
                        put("due_at_epoch_millis", card.dueAtEpochMillis)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
            StudyContent.quizQuestions.forEach { question ->
                database.insertWithOnConflict(
                    "quiz_questions",
                    null,
                    ContentValues().apply {
                        put("id", question.id)
                        put("subject", question.subject)
                        put("topic", question.topic)
                        put("question", question.question)
                        put("option_a", question.options[0])
                        put("option_b", question.options[1])
                        put("option_c", question.options[2])
                        put("option_d", question.options[3])
                        put("correct_option_index", question.correctOptionIndex)
                        put("explanation", question.explanation)
                        put("repetitions", question.repetitions)
                        put("interval_days", question.intervalDays)
                        put("ease_factor", question.easeFactor)
                        put("due_at_epoch_millis", question.dueAtEpochMillis)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
        }

        private fun installSeedDatabaseIfNeeded(context: Context) {
            val databaseFile = context.getDatabasePath(DATABASE_NAME)
            if (databaseFile.exists()) return

            val databaseDirectory = databaseFile.parentFile
                ?: error("No se pudo determinar la carpeta de bases de datos de la app.")
            check(databaseDirectory.isDirectory || databaseDirectory.mkdirs()) {
                "No se pudo crear la carpeta de bases de datos de la app."
            }

            val temporaryFile = File(databaseDirectory, "$DATABASE_NAME.tmp")
            if (temporaryFile.exists()) {
                check(temporaryFile.delete()) {
                    "No se pudo limpiar la copia incompleta de la base de datos."
                }
            }

            try {
                context.assets.open(SEED_DATABASE_ASSET).use { seedDatabase ->
                    temporaryFile.outputStream().use { output ->
                        seedDatabase.copyTo(output)
                    }
                }
                if (!temporaryFile.renameTo(databaseFile)) {
                    throw IOException("No se pudo instalar la base de datos inicial.")
                }
            } catch (exception: IOException) {
                if (temporaryFile.exists() && !temporaryFile.delete()) {
                    exception.addSuppressed(
                        IOException("No se pudo limpiar la copia incompleta de la base de datos.")
                    )
                }
                throw exception
            }
        }
    }

    private companion object {
        const val DATABASE_NAME = "medcards.db"
        const val SEED_DATABASE_ASSET = "medcards.db"
        const val DATABASE_VERSION = 4
        val CARD_COLUMNS = arrayOf(
            "id", "subject", "question", "answer", "repetitions", "interval_days", "ease_factor",
            "image_url", "due_at_epoch_millis"
        )
        val QUIZ_COLUMNS = arrayOf(
            "id", "subject", "topic", "question", "option_a", "option_b", "option_c", "option_d",
            "correct_option_index", "explanation", "repetitions", "interval_days", "ease_factor",
            "due_at_epoch_millis"
        )
        const val CREATE_FLASHCARDS_SQL = """CREATE TABLE flashcards (
            id TEXT PRIMARY KEY, subject TEXT NOT NULL, question TEXT NOT NULL, answer TEXT NOT NULL,
            repetitions INTEGER NOT NULL DEFAULT 0, interval_days INTEGER NOT NULL DEFAULT 1,
            ease_factor REAL NOT NULL DEFAULT 2.5, image_url TEXT,
            due_at_epoch_millis INTEGER NOT NULL DEFAULT 0)"""
        const val CREATE_REVIEW_HISTORY_SQL = """CREATE TABLE review_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT, card_id TEXT NOT NULL,
            rating TEXT NOT NULL, reviewed_at INTEGER NOT NULL)"""
        const val CREATE_QUIZ_QUESTIONS_SQL = """CREATE TABLE quiz_questions (
            id TEXT PRIMARY KEY, subject TEXT NOT NULL, topic TEXT NOT NULL, question TEXT NOT NULL,
            option_a TEXT NOT NULL, option_b TEXT NOT NULL, option_c TEXT NOT NULL, option_d TEXT NOT NULL,
            correct_option_index INTEGER NOT NULL, explanation TEXT NOT NULL,
            repetitions INTEGER NOT NULL DEFAULT 0, interval_days INTEGER NOT NULL DEFAULT 1,
            ease_factor REAL NOT NULL DEFAULT 2.5, due_at_epoch_millis INTEGER NOT NULL DEFAULT 0)"""
        const val CREATE_QUIZ_HISTORY_SQL = """CREATE TABLE quiz_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT, question_id TEXT NOT NULL,
            is_correct INTEGER NOT NULL, answered_at INTEGER NOT NULL)"""
    }
}

private fun android.database.Cursor.toFlashcard() = Flashcard(
    id = getString(0),
    subject = getString(1),
    question = getString(2),
    answer = getString(3),
    repetitions = getInt(4),
    intervalDays = getInt(5),
    easeFactor = getDouble(6),
    imageUrl = if (isNull(7)) null else getString(7),
    dueAtEpochMillis = getLong(8)
)

private fun android.database.Cursor.toQuizQuestion() = QuizQuestion(
    id = getString(0),
    subject = getString(1),
    topic = getString(2),
    question = getString(3),
    options = listOf(getString(4), getString(5), getString(6), getString(7)),
    correctOptionIndex = getInt(8),
    explanation = getString(9),
    repetitions = getInt(10),
    intervalDays = getInt(11),
    easeFactor = getDouble(12),
    dueAtEpochMillis = getLong(13)
)

internal fun calculateCurrentStreakDays(
    reviewedDates: Set<LocalDate>,
    today: LocalDate
): Int {
    var streakDate = when {
        today in reviewedDates -> today
        today.minusDays(1) in reviewedDates -> today.minusDays(1)
        else -> return 0
    }
    var streakDays = 0
    while (streakDate in reviewedDates) {
        streakDays++
        streakDate = streakDate.minusDays(1)
    }
    return streakDays
}
