package com.example.medcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medcards.data.repository.AppearancePreferencesRepository
import com.example.medcards.data.repository.ProfilePreferencesRepository
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
        val appearancePreferences = AppearancePreferencesRepository(applicationContext)
        val profilePreferences = ProfilePreferencesRepository(applicationContext)
        val calculator = SpacedRepetitionCalculator()
        setContent {
            var darkThemeEnabled by remember {
                mutableStateOf(appearancePreferences.darkThemeEnabled)
            }
            MedCardsTheme(darkTheme = darkThemeEnabled) {
                MainMedCardsApp(
                    repository = repository,
                    subscriptionRepository = subscriptionRepository,
                    profilePreferences = profilePreferences,
                    calculator = calculator,
                    darkThemeEnabled = darkThemeEnabled,
                    onDarkThemeChange = { enabled ->
                        appearancePreferences.setDarkThemeEnabled(enabled)
                        darkThemeEnabled = enabled
                    }
                )
            }
        }
    }
}