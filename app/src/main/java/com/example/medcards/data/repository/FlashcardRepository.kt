package com.example.medcards.data.repository

import com.example.medcards.data.model.Flashcard

interface FlashcardRepository {
    fun getFlashcardsBySubject(subject: String): List<Flashcard>

    fun updateCardProgress(card: Flashcard)
}

class MockFlashcardRepositoryImpl : FlashcardRepository {
    private val flashcards = mutableListOf(
        Flashcard(
            id = "anatomy-1",
            subject = "Anatomía",
            question = "¿Cuál es el hueso más largo del cuerpo humano?",
            answer = "El fémur"
        ),
        Flashcard(
            id = "anatomy-2",
            subject = "Anatomía",
            question = "¿Qué órgano bombea la sangre?",
            answer = "El corazón"
        ),
        Flashcard(
            id = "pharmacology-1",
            subject = "Farmacología",
            question = "¿A qué grupo farmacológico pertenece el ibuprofeno?",
            answer = "Antiinflamatorios no esteroideos (AINE)"
        ),
        Flashcard(
            id = "pharmacology-2",
            subject = "Farmacología",
            question = "¿Qué medicamento se utiliza para revertir una sobredosis de opioides?",
            answer = "Naloxona"
        )
    )

    override fun getFlashcardsBySubject(subject: String): List<Flashcard> =
        flashcards.filter { it.subject == subject }

    override fun updateCardProgress(card: Flashcard) {
        val index = flashcards.indexOfFirst { it.id == card.id }
        require(index >= 0) { "No flashcard found with id '${card.id}'." }
        flashcards[index] = card
    }
}
