package com.example.medcards.data.model

data class Flashcard(
    val id: String,
    val subject: String,        // ej. "Anatomía"
    val question: String,       // ej. "¿Qué arteria irriga el ventrículo izquierdo?"
    val answer: String,         // ej. "Arteria coronaria izquierda"
    val difficulty: Int = 0,    // Nivel asignado por el algoritmo
    val imageUrl: String? = null // Para oclusión de imágenes
)