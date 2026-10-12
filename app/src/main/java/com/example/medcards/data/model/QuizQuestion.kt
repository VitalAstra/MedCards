package com.example.medcards.data.model

data class QuizQuestion(
    val id: String,
    val subject: String,
    val topic: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val repetitions: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Double = 2.5,
    val dueAtEpochMillis: Long = 0L
)

data class QuizStats(
    val dueQuestions: Int,
    val totalAnswers: Int,
    val correctAnswers: Int,
    val answersToday: Int
) {
    val accuracyPercent: Int
        get() = if (totalAnswers == 0) 0 else correctAnswers * 100 / totalAnswers
}