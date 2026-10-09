package com.example.medcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.Flashcard
import com.example.medcards.domain.Rating
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.ui.components.SM2FeedbackSelector
import com.example.medcards.ui.theme.*

@Composable
fun FlashcardScreen(
    card: Flashcard,
    calculator: SpacedRepetitionCalculator,
    onRated: (Rating) -> Unit
) {
    var isRevealed by rememberSaveable(card.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightColdGrey)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = card.subject.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRevealed) "Respuesta" else "Pregunta",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (isRevealed) card.answer else card.question,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isRevealed) CobaltBlue else TextDark
                )
            }
        }

        if (isRevealed) {
            SM2FeedbackSelector(
                card = card,
                calculator = calculator,
                onSelect = onRated
            )
        } else {
            Button(
                onClick = { isRevealed = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Mostrar respuesta")
            }
            Text(
                text = "Intenta recordar la respuesta antes de revelarla.",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}