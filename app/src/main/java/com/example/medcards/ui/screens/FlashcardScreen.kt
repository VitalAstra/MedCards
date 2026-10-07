package com.example.medcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medcards.data.model.Flashcard
import com.example.medcards.ui.theme.*

@Composable
fun FlashcardScreen(card: Flashcard, onAnswered: (String) -> Unit) {
    var isRevealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightColdGrey)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tarjeta principal (Card de Active Recall)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clickable { isRevealed = !isRevealed },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isRevealed) {
                    Text(text = card.question, fontSize = 20.sp, color = TextDark)
                } else {
                    Text(text = card.answer, fontSize = 18.sp, color = CobaltBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de autoevaluación (Spaced Repetition)
        if (isRevealed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onAnswered("HARD") },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Difícil") }

                Button(
                    onClick = { onAnswered("GOOD") },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                ) { Text("Bien") }

                Button(
                    onClick = { onAnswered("EASY") },
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                ) { Text("Fácil") }
            }
        } else {
            Text(text = "Toca la tarjeta para ver la respuesta", color = TextMuted)
        }
    }
}