package com.example.medcards.data.repository

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewStreakTest {
    private val today = LocalDate.of(2026, 10, 9)

    @Test
    fun countsConsecutiveDaysWhenLastReviewWasYesterday() {
        val reviewedDates = setOf(today.minusDays(1), today.minusDays(2))

        assertEquals(2, calculateCurrentStreakDays(reviewedDates, today))
    }

    @Test
    fun returnsZeroWhenLastReviewWasMoreThanOneDayAgo() {
        val reviewedDates = setOf(today.minusDays(2))

        assertEquals(0, calculateCurrentStreakDays(reviewedDates, today))
    }
}
