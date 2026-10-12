package com.example.medcards.domain

import com.example.medcards.data.model.Flashcard
import kotlin.math.roundToInt

enum class Rating {
    AGAIN,
    EASY,
    GOOD,
    HARD,
    MASTERED
}

class SpacedRepetitionCalculator {
    fun calculateNextReview(
        card: Flashcard,
        rating: Rating,
        nowEpochMillis: Long = System.currentTimeMillis()
    ): Flashcard {
        val quality = when (rating) {
            Rating.AGAIN -> 1
            Rating.EASY -> 5
            Rating.GOOD -> 4
            Rating.HARD -> 3
            Rating.MASTERED -> 5
        }
        val qualityDifference = 5 - quality
        val updatedEaseFactor = (
            card.easeFactor +
                0.1 -
                qualityDifference * (0.08 + qualityDifference * 0.02)
            ).coerceAtLeast(MIN_EASE_FACTOR)

        val updatedRepetitions = if (rating == Rating.AGAIN) 0 else card.repetitions + 1
        val intervalDays = if (rating == Rating.AGAIN) {
            1
        } else {
            when (updatedRepetitions) {
                1 -> 1
                2 -> 6
                else -> (card.intervalDays * updatedEaseFactor).roundToInt()
            }
        }
        val updatedIntervalDays = if (rating == Rating.MASTERED) {
            intervalDays.coerceAtLeast(MASTERED_MIN_INTERVAL_DAYS)
        } else {
            intervalDays
        }
        val delayMillis = if (rating == Rating.AGAIN) {
            RETRY_DELAY_MILLIS
        } else {
            updatedIntervalDays.toLong() * MILLIS_PER_DAY
        }

        return card.copy(
            repetitions = updatedRepetitions,
            intervalDays = updatedIntervalDays,
            easeFactor = updatedEaseFactor,
            dueAtEpochMillis = nowEpochMillis + delayMillis
        )
    }

    companion object {
        const val MIN_EASE_FACTOR = 1.3
        const val MASTERED_MIN_INTERVAL_DAYS = 7
        const val RETRY_DELAY_MILLIS = 10 * 60 * 1000L
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }
}
