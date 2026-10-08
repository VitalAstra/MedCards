package com.example.medcards.domain

import com.example.medcards.data.model.Flashcard
import org.junit.Assert.assertEquals
import org.junit.Test

class SpacedRepetitionCalculatorTest {
    private val calculator = SpacedRepetitionCalculator()

    @Test
    fun firstSuccessfulReviewSetsIntervalToOneDay() {
        val card = newCard()

        val result = calculator.calculateNextReview(card, Rating.GOOD)

        assertEquals(1, result.repetitions)
        assertEquals(1, result.intervalDays)
        assertEquals(2.5, result.easeFactor, 0.0)
        assertEquals(0, card.repetitions)
    }

    @Test
    fun secondSuccessfulReviewSetsIntervalToSixDays() {
        val card = newCard().copy(repetitions = 1, intervalDays = 1)

        val result = calculator.calculateNextReview(card, Rating.HARD)

        assertEquals(2, result.repetitions)
        assertEquals(6, result.intervalDays)
        assertEquals(2.36, result.easeFactor, 0.0001)
    }

    @Test
    fun subsequentReviewMultipliesIntervalByUpdatedEaseFactor() {
        val card = newCard().copy(repetitions = 2, intervalDays = 6)

        val result = calculator.calculateNextReview(card, Rating.EASY)

        assertEquals(3, result.repetitions)
        assertEquals(16, result.intervalDays)
        assertEquals(2.6, result.easeFactor, 0.0001)
    }

    @Test
    fun easeFactorDoesNotFallBelowMinimum() {
        val card = newCard().copy(easeFactor = 1.3)

        val result = calculator.calculateNextReview(card, Rating.HARD)

        assertEquals(1.3, result.easeFactor, 0.0)
    }

    private fun newCard() = Flashcard(
        id = "card-1",
        subject = "Anatomía",
        question = "Pregunta",
        answer = "Respuesta"
    )
}
