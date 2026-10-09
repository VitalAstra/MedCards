package com.example.medcards.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medcards.data.model.Flashcard
import com.example.medcards.domain.Rating
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.DifficultyMastered
import com.example.medcards.ui.theme.ErrorRed
import com.example.medcards.ui.theme.MintGreen
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted
import com.example.medcards.ui.theme.WarningAmber

private data class FeedbackOption(
    val label: String,
    val rating: Rating,
    val color: Color
)

@Composable
fun SM2FeedbackSelector(
    card: Flashcard,
    calculator: SpacedRepetitionCalculator,
    onSelect: (Rating) -> Unit
) {
    val rows = listOf(
        listOf(
            FeedbackOption("Difícil", Rating.HARD, ErrorRed),
            FeedbackOption("Bien", Rating.GOOD, WarningAmber)
        ),
        listOf(
            FeedbackOption("Fácil", Rating.EASY, MintGreen),
            FeedbackOption("Dominado", Rating.MASTERED, DifficultyMastered)
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "¿Cómo te resultó?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { option ->
                    val interval = calculator.calculateNextReview(card, option.rating).intervalDays
                    Surface(
                        onClick = { onSelect(option.rating) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = option.color.copy(alpha = 0.13f),
                        contentColor = TextDark
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 13.dp)
                        ) {
                            Text(
                                text = option.label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (interval == 1) "En 1 día" else "En $interval días",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
        Text(
            text = "Los intervalos se calculan con SM-2; «Dominado» programa al menos 7 días.",
            color = TextMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
