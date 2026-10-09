package com.example.medcards.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.medcards.data.model.Flashcard
import com.example.medcards.domain.Rating
import java.time.LocalDate
import java.time.ZoneId

data class SubjectStats(
    val subject: String,
    val total: Int,
    val reviewed: Int
)

data class ReviewStats(
    val reviewedToday: Int,
    val totalReviews: Int,
    val masteredCards: Int,
    val streakDays: Int
)

interface FlashcardRepository {
    fun getAllFlashcards(): List<Flashcard>

    fun getFlashcardsBySubject(subject: String): List<Flashcard>

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

    override fun getAllFlashcards(): List<Flashcard> = flashcards.toList()

    override fun getFlashcardsBySubject(subject: String): List<Flashcard> =
        flashcards.filter { it.subject == subject }

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
        val today = LocalDate.now()
        val reviewedDays = reviewDates.toSet()

        return ReviewStats(
            reviewedToday = reviewDates.count { it == today },
            totalReviews = reviewDates.size,
            masteredCards = flashcards.count { it.repetitions >= 3 },
            streakDays = calculateCurrentStreakDays(reviewedDays, today)
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
        val startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
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
        val reviewedDates = database.rawQuery(
            "SELECT DISTINCT date(reviewed_at / 1000, 'unixepoch', 'localtime') " +
                "FROM review_history ORDER BY 1 DESC",
            null
        ).use { cursor ->
            buildSet {
                while (cursor.moveToNext()) add(LocalDate.parse(cursor.getString(0)))
            }
        }
        val streakDays = calculateCurrentStreakDays(reviewedDates, LocalDate.now())
        return ReviewStats(reviewedToday, totalReviews, masteredCards, streakDays)
    }

    override fun updateCardProgress(card: Flashcard, rating: Rating) {
        val writableDatabase = database.writableDatabase
        val values = ContentValues().apply {
            put("repetitions", card.repetitions)
            put("interval_days", card.intervalDays)
            put("ease_factor", card.easeFactor)
        }
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
    }

    private class FlashcardDatabase(context: Context) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
        override fun onCreate(database: SQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE flashcards (" +
                    "id TEXT PRIMARY KEY, subject TEXT NOT NULL, question TEXT NOT NULL, " +
                    "answer TEXT NOT NULL, repetitions INTEGER NOT NULL DEFAULT 0, " +
                    "interval_days INTEGER NOT NULL DEFAULT 1, ease_factor REAL NOT NULL DEFAULT 2.5)"
            )
            database.execSQL(
                "CREATE TABLE review_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, card_id TEXT NOT NULL, " +
                    "rating TEXT NOT NULL, reviewed_at INTEGER NOT NULL)"
            )
            seedCards(database)
        }

        override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

        private fun seedCards(database: SQLiteDatabase) {
            database.beginTransaction()
            try {
                seedFlashcards.forEach { card ->
                    database.insertOrThrow(
                        "flashcards",
                        null,
                        ContentValues().apply {
                            put("id", card.id)
                            put("subject", card.subject)
                            put("question", card.question)
                            put("answer", card.answer)
                        }
                    )
                }
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }

    private companion object {
        const val DATABASE_NAME = "medcards.db"
        const val DATABASE_VERSION = 1
        val CARD_COLUMNS = arrayOf(
            "id", "subject", "question", "answer", "repetitions", "interval_days", "ease_factor"
        )

        val seedFlashcards = listOf(
            Flashcard("anatomy-1", "Anatomía", "¿Cuál es el hueso más largo del cuerpo humano?", "El fémur"),
            Flashcard("anatomy-2", "Anatomía", "¿Qué órgano bombea la sangre por el sistema circulatorio?", "El corazón"),
            Flashcard("anatomy-3", "Anatomía", "¿Qué estructura conecta el músculo con el hueso?", "El tendón"),
            Flashcard("anatomy-4", "Anatomía", "¿En qué cavidad se encuentran los pulmones?", "En la cavidad torácica"),
            Flashcard("anatomy-5", "Anatomía", "¿Cuál es la unidad funcional del riñón?", "La nefrona"),
            Flashcard("anatomy-6", "Anatomía", "¿Qué tipo de articulación es la rodilla?", "Una articulación sinovial de tipo bisagra"),
            Flashcard("pharmacology-1", "Farmacología", "¿A qué grupo pertenece el ibuprofeno?", "Antiinflamatorios no esteroideos (AINE)"),
            Flashcard("pharmacology-2", "Farmacología", "¿Qué fármaco revierte una intoxicación por opioides?", "Naloxona"),
            Flashcard("pharmacology-3", "Farmacología", "¿Cuál es el mecanismo principal del paracetamol?", "Analgesia y antipiresis por acción central"),
            Flashcard("pharmacology-4", "Farmacología", "¿Qué receptor bloquea el salbutamol?", "Es agonista beta-2 adrenérgico"),
            Flashcard("pharmacology-5", "Farmacología", "¿Qué efecto tienen los antibióticos sobre los virus?", "No son eficaces contra infecciones virales"),
            Flashcard("pharmacology-6", "Farmacología", "¿Qué enzima inhiben principalmente los AINE?", "La ciclooxigenasa (COX)"),
            Flashcard("histology-1", "Histología", "¿Qué célula produce anticuerpos?", "El plasmocito"),
            Flashcard("histology-2", "Histología", "¿Qué epitelio reviste los alvéolos pulmonares?", "Epitelio plano simple"),
            Flashcard("histology-3", "Histología", "¿Qué célula forma mielina en el sistema nervioso central?", "El oligodendrocito"),
            Flashcard("histology-4", "Histología", "¿Qué proteína almacena oxígeno en el músculo?", "La mioglobina"),
            Flashcard("histology-5", "Histología", "¿Qué estructura celular sintetiza proteínas?", "El ribosoma"),
            Flashcard("histology-6", "Histología", "¿Qué células forman la matriz ósea?", "Los osteoblastos")
        )
    }
}

private fun android.database.Cursor.toFlashcard() = Flashcard(
    id = getString(0),
    subject = getString(1),
    question = getString(2),
    answer = getString(3),
    repetitions = getInt(4),
    intervalDays = getInt(5),
    easeFactor = getDouble(6)
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
