package com.example.medcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.Flashcard
import com.example.medcards.domain.Rating
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.ui.components.SM2FeedbackSelector
import com.example.medcards.ui.components.illustrationTypeForSubject
import com.example.medcards.ui.components.MedicalIllustration
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = card.subject.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted
            )
            MedicalIllustration(
                type = illustrationTypeForSubject(card.subject),
                size = 54.dp
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 410.dp)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CobaltBlueContainer,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(188.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MedicalIllustration(
                            type = illustrationTypeForSubject(card.subject),
                            size = 164.dp
                        )
                    }
                }
                Text(
                    text = if (isRevealed) "RESPUESTA" else "PREGUNTA",
                    style = MaterialTheme.typography.labelMedium,
                    color = CobaltBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isRevealed) card.answer else card.question,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isRevealed) CobaltBlue else TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "MEDISYNAP  ·  REPASO ACTIVO",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
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