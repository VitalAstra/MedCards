package com.example.medcards.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.repository.ReviewStats
import com.example.medcards.data.repository.SubjectStats
import com.example.medcards.ui.components.IllustrationType
import com.example.medcards.ui.components.MedicalIllustration

data class ReviewArea(
    val subject: String,
    val totalCards: Int,
    val dueCards: Int
)

@Composable
fun ReviewHubScreen(
    reviewStats: ReviewStats,
    areas: List<ReviewArea>,
    onStartReview: (subject: String?, dueOnly: Boolean) -> Unit
) {
    StudyPage(
        title = "Repaso",
        subtitle = "Elige qué estudiar y cómo practicar. Las tarjetas pendientes se programan con repetición espaciada.",
        illustration = IllustrationType.Learning
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Tu sesión de hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${reviewStats.dueCards} tarjetas pendientes · ${reviewStats.reviewedToday} repasos completados hoy",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { onStartReview(null, true) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = reviewStats.dueCards > 0
                ) {
                    Text("Repasar pendientes (${reviewStats.dueCards})")
                }
                OutlinedButton(
                    onClick = { onStartReview(null, false) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = areas.isNotEmpty()
                ) {
                    Text("Practicar cualquier tarjeta")
                }
            }
        }

        Text(
            "Elegir un área",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (areas.isEmpty()) {
            Text(
                "No hay áreas con tarjetas disponibles todavía.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            areas.forEach { area ->
                ReviewAreaCard(
                    area = area,
                    onReviewDue = { onStartReview(area.subject, true) },
                    onPracticeAll = { onStartReview(area.subject, false) }
                )
            }
        }
    }
}

@Composable
private fun ReviewAreaCard(
    area: ReviewArea,
    onReviewDue: () -> Unit,
    onPracticeAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        area.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${area.totalCards} tarjetas · ${area.dueCards} pendientes",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MedicalIllustration(
                    type = com.example.medcards.ui.components.illustrationTypeForSubject(area.subject),
                    size = 52.dp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onReviewDue,
                    modifier = Modifier.weight(1f),
                    enabled = area.dueCards > 0
                ) {
                    Text("Pendientes")
                }
                OutlinedButton(
                    onClick = onPracticeAll,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Practicar todo")
                }
            }
        }
    }
}
