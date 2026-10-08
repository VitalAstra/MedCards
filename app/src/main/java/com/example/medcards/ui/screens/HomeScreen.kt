package com.example.medcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.ui.components.DeckCard
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.LightColdGrey
import com.example.medcards.ui.theme.MintGreen
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted
import com.example.medcards.ui.theme.WarningAmber

private data class DeckSummary(
    val subject: String,
    val reviewedCount: Int,
    val totalCount: Int
)

private val deckSummaries = listOf(
    DeckSummary(subject = "Anatomía", reviewedCount = 12, totalCount = 40),
    DeckSummary(subject = "Farmacología", reviewedCount = 8, totalCount = 32),
    DeckSummary(subject = "Histología", reviewedCount = 5, totalCount = 24)
)

@Composable
fun HomeScreen(
    onStartReviewClick: () -> Unit,
    onDeckClick: (subject: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightColdGrey),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            HomeHeader()
        }

        item {
            DailyGoalCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                onStartReviewClick = onStartReviewClick
            )
        }

        item {
            Text(
                text = "Mis Mazos",
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        items(deckSummaries, key = { it.subject }) { deck ->
            DeckCard(
                subject = deck.subject,
                reviewedCount = deck.reviewedCount,
                totalCount = deck.totalCount,
                onClick = { onDeckClick(deck.subject) }
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CobaltBlue)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "¡Hola, Marcelo! 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CardWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "¿Qué repasamos hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = CardWhite
            )
        }

        Card(
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = WarningAmber),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "🔥 5 días",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}

@Composable
private fun DailyGoalCard(
    modifier: Modifier = Modifier,
    onStartReviewClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Meta Diaria",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Llevas 8 de 20 tarjetas repasadas hoy",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStartReviewClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
            ) {
                Text(
                    text = "Iniciar Repaso",
                    color = CardWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}