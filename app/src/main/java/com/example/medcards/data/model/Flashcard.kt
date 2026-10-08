package com.example.medcards.data.model

data class Flashcard(
    val id: String,
    val subject: String,
    val question: String,
    val answer: String,
    val repetitions: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Double = 2.5,
    val imageUrl: String? = null
) {
    val isNew: Boolean
        get() = repetitions == 0
}