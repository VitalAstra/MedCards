package com.example.medcards.domain

import com.example.medcards.data.model.Flashcard
import kotlin.math.roundToInt

enum class Rating {
    EASY,
    GOOD,
    HARD,
    MASTERED
}

class SpacedRepetitionCalculator {
    fun calculateNextReview(card: Flashcard, rating: Rating): Flashcard {
        val quality = when (rating) {
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

        val updatedRepetitions = card.repetitions + 1
        val intervalDays = when (updatedRepetitions) {
            1 -> 1
            2 -> 6
            else -> (card.intervalDays * updatedEaseFactor).roundToInt()
        }
        val updatedIntervalDays = if (rating == Rating.MASTERED) {
            intervalDays.coerceAtLeast(MASTERED_MIN_INTERVAL_DAYS)
        } else {
            intervalDays
        }

        return card.copy(
            repetitions = updatedRepetitions,
            intervalDays = updatedIntervalDays,
            easeFactor = updatedEaseFactor
        )
    }

    private companion object {
        const val MIN_EASE_FACTOR = 1.3
        const val MASTERED_MIN_INTERVAL_DAYS = 7
    }
}
