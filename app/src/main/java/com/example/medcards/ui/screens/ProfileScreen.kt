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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.QuizStats
import com.example.medcards.data.repository.ReviewStats
import com.example.medcards.ui.components.IllustrationType
import com.example.medcards.ui.components.ProfileAvatar

@Composable
fun ProfileScreen(
    displayName: String,
    photoPath: String?,
    reviewStats: ReviewStats,
    quizStats: QuizStats,
    totalCards: Int,
    onSaveName: (String) -> Unit,
    onPickPhoto: () -> Unit
) {
    var editedName by rememberSaveable(displayName) { mutableStateOf(displayName) }
    StudyPage(
        title = "Mi perfil",
        subtitle = "Tu actividad de estudio y tus preferencias de perfil.",
        illustration = IllustrationType.Learning
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileAvatar(displayName = displayName, photoPath = photoPath, size = 76.dp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Estudiante", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(onClick = onPickPhoto) {
                        Text("Cambiar foto")
                    }
                }
            }
        }
        OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre") },
            singleLine = true
        )
        Button(
            onClick = { onSaveName(editedName) },
            modifier = Modifier.fillMaxWidth(),
            enabled = editedName.trim().isNotEmpty() && editedName.trim() != displayName
        ) {
            Text("Guardar nombre")
        }
        Text(
            "Resumen de estudio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileStat("Tarjetas", totalCards.toString(), Modifier.weight(1f))
            ProfileStat("Repasos", reviewStats.totalReviews.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileStat("Dominadas", reviewStats.masteredCards.toString(), Modifier.weight(1f))
            ProfileStat("Racha", "${reviewStats.streakDays} días", Modifier.weight(1f))
        }
        ProfileStat(
            "Tests",
            "${quizStats.accuracyPercent}% aciertos · ${quizStats.totalAnswers} respuestas",
            Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
