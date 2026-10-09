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
import com.example.medcards.data.repository.ReviewStats
import com.example.medcards.data.repository.SubjectStats
import com.example.medcards.ui.components.DeckCard
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.LightColdGrey
import com.example.medcards.ui.theme.MintGreen
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted
import com.example.medcards.ui.theme.WarningAmber

@Composable
fun HomeScreen(
    subjectStats: List<SubjectStats>,
    reviewStats: ReviewStats,
    onStartReviewClick: () -> Unit,
    onDeckClick: (subject: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(LightColdGrey),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { HomeHeader(streakDays = reviewStats.streakDays) }
        item {
            TodayCard(
                reviewStats = reviewStats,
                modifier = Modifier.padding(horizontal = 20.dp),
                onStartReviewClick = onStartReviewClick
            )
        }
        item {
            Text(
                text = "Mis mazos",
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
        if (subjectStats.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay mazos disponibles.",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = TextMuted
                )
            }
        } else {
            items(subjectStats, key = { it.subject }) { deck ->
                DeckCard(
                    subject = deck.subject,
                    reviewedCount = deck.reviewed,
                    totalCount = deck.total,
                    onClick = { onDeckClick(deck.subject) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun HomeHeader(streakDays: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CobaltBlue)
            .padding(horizontal = 20.dp, vertical = 26.dp),
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
            Spacer(Modifier.height(6.dp))
            Text(
                text = "¿Qué repasamos hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = CardWhite
            )
        }
        Card(
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = WarningAmber)
        ) {
            Text(
                text = "🔥 $streakDays días",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}

@Composable
private fun TodayCard(
    reviewStats: ReviewStats,
    modifier: Modifier = Modifier,
    onStartReviewClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = "Meta diaria",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${reviewStats.reviewedToday} repasos registrados hoy · " +
                    "${reviewStats.totalReviews} en total",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onStartReviewClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
            ) {
                Text("Iniciar repaso", color = CardWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}
