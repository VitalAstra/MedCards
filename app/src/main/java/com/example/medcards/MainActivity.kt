package com.example.medcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.medcards.data.model.Flashcard
import com.example.medcards.ui.screens.FlashcardScreen
import com.example.medcards.ui.theme.MedCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedCardsTheme {
                val sampleCard = Flashcard(
                    id = "1",
                    subject = "Anatomía",
                    question = "¿Cuál es el hueso más largo del cuerpo humano?",
                    answer = "El Fémur"
                )
                FlashcardScreen(card = sampleCard, onAnswered = { rating ->
                    // Aquí procesaremos la lógica de repetición espaciada
                })
            }
        }
    }
}