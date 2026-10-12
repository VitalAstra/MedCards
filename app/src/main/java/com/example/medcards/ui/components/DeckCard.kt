package com.example.medcards.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.LightColdGrey
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted

@Composable
fun DeckCard(
    subject: String,
    reviewedCount: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalCount > 0) {
        (reviewedCount.toFloat() / totalCount).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$reviewedCount de $totalCount tarjetas repasadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = CobaltBlue,
                    trackColor = LightColdGrey
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            MedicalIllustration(
                type = illustrationTypeForSubject(subject),
                size = 64.dp
            )
        }
    }
}

fun illustrationTypeForSubject(subject: String): IllustrationType = when {
    subject.contains("anato", ignoreCase = true) -> IllustrationType.Anatomy
    subject.contains("farmac", ignoreCase = true) -> IllustrationType.Pharmacology
    subject.contains("histo", ignoreCase = true) -> IllustrationType.Histology
    subject.contains("biolog", ignoreCase = true) -> IllustrationType.Biology
    subject.contains("quím", ignoreCase = true) || subject.contains("quim", ignoreCase = true) -> IllustrationType.Chemistry
    subject.contains("fís", ignoreCase = true) || subject.contains("fis", ignoreCase = true) -> IllustrationType.Physics
    subject.contains("pre", ignoreCase = true) -> IllustrationType.PreMedicine
    else -> IllustrationType.Learning
}
