package com.example.medcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.medcards.data.repository.SQLiteFlashcardRepository
import com.example.medcards.data.repository.SubscriptionRepository
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.ui.MainMedCardsApp
import com.example.medcards.ui.theme.MedCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = SQLiteFlashcardRepository(applicationContext)
        val subscriptionRepository = SubscriptionRepository(applicationContext)
        val calculator = SpacedRepetitionCalculator()
        setContent {
            MedCardsTheme {
                MainMedCardsApp(
                    repository = repository,
                    subscriptionRepository = subscriptionRepository,
                    calculator = calculator
                )
            }
        }
    }
}