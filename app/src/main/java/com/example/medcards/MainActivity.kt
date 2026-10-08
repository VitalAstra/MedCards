package com.example.medcards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.medcards.data.repository.MockFlashcardRepositoryImpl
import com.example.medcards.domain.Rating
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.ui.components.BottomNavigationBar
import com.example.medcards.ui.screens.FlashcardScreen
import com.example.medcards.ui.screens.HomeScreen
import com.example.medcards.ui.theme.MedCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MockFlashcardRepositoryImpl()
        val calculator = SpacedRepetitionCalculator()
        val initialCard = requireNotNull(
            repository.getFlashcardsBySubject("Anatomía").firstOrNull()
        ) { "No hay tarjetas de Anatomía disponibles." }

        setContent {
            MedCardsTheme {
                var currentCard by remember { mutableStateOf(initialCard) }
                var showingReview by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            currentRoute = "home",
                            onItemSelected = { route ->
                                if (route != "home") {
                                    val section = when (route) {
                                        "decks" -> "Mazos"
                                        "progress" -> "Progreso"
                                        "profile" -> "Perfil"
                                        else -> route
                                    }
                                    Toast.makeText(
                                        this@MainActivity,
                                        "$section estará disponible próximamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    showingReview = false
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (showingReview) {
                            FlashcardScreen(
                                card = currentCard,
                                onRated = { rating: Rating ->
                                    val updatedCard =
                                        calculator.calculateNextReview(currentCard, rating)
                                    repository.updateCardProgress(updatedCard)
                                    currentCard = updatedCard
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Progreso guardado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        } else {
                            HomeScreen(
                                onStartReviewClick = { showingReview = true },
                                onDeckClick = { subject ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Mazo seleccionado: $subject",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}