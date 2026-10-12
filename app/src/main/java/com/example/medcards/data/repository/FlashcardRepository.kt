package com.example.medcards.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.medcards.data.model.Flashcard
import com.example.medcards.domain.Rating
import java.io.File
import java.io.IOException
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
        init {
            installSeedDatabaseIfNeeded(context)
        }

        override fun onCreate(database: SQLiteDatabase) {
            error("La base de datos semilla medcards.db no se instaló correctamente.")
        }

        override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                database.execSQL("ALTER TABLE flashcards ADD COLUMN image_url TEXT")
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
        const val DATABASE_VERSION = 2
        val CARD_COLUMNS = arrayOf(
            "id", "subject", "question", "answer", "repetitions", "interval_days", "ease_factor",
            "image_url"
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
    easeFactor = getDouble(6),
    imageUrl = if (isNull(7)) null else getString(7)
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
