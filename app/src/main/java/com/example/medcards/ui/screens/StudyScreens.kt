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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.Flashcard
import com.example.medcards.data.model.QuizStats
import com.example.medcards.data.repository.ReviewStats
import com.example.medcards.data.repository.SubjectStats
import com.example.medcards.ui.components.DeckCard
import com.example.medcards.ui.components.IllustrationType
import com.example.medcards.ui.components.MedicalIllustration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MetricsScreen(
    reviewStats: ReviewStats,
    subjectStats: List<SubjectStats>,
    totalCards: Int,
    quizStats: QuizStats
) {
    StudyPage(
        title = "Métricas",
        subtitle = "Consulta tu actividad y progreso de estudio.",
        illustration = IllustrationType.Analytics
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Hoy", reviewStats.reviewedToday.toString(), Modifier.weight(1f))
            MetricCard("Repasos", reviewStats.totalReviews.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Dominadas", reviewStats.masteredCards.toString(), Modifier.weight(1f))
            MetricCard("Racha", "${reviewStats.streakDays} días", Modifier.weight(1f))
        }
        InfoCard(
            title = "Tarjetas y mazos",
            text = "$totalCards tarjetas en ${subjectStats.size} mazos."
        )
        InfoCard(
            title = "Tests espaciados",
            text = "${quizStats.totalAnswers} respuestas guardadas · " +
                "${quizStats.accuracyPercent}% de aciertos · ${quizStats.dueQuestions} preguntas pendientes."
        )
        Text("Progreso por mazo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        subjectStats.forEach { deck ->
            Text(
                "${deck.subject}: ${deck.reviewed} de ${deck.total} con al menos un repaso",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (subjectStats.isEmpty()) {
            Text("No hay datos de mazos para mostrar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DeckCatalogScreen(
    subjectStats: List<SubjectStats>,
    dueCardsBySubject: Map<String, Int>,
    onDeckClick: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(DeckFilter.ALL) }
    val totalCards = subjectStats.sumOf { it.total }
    val totalDueCards = subjectStats.sumOf { dueCardsBySubject[it.subject] ?: 0 }
    val matchingDecks = subjectStats.filter { deck ->
        val matchesSearch = deck.subject.contains(searchQuery.trim(), ignoreCase = true)
        val dueCount = dueCardsBySubject[deck.subject] ?: 0
        val matchesFilter = when (selectedFilter) {
            DeckFilter.ALL -> true
            DeckFilter.DUE -> dueCount > 0
            DeckFilter.STUDIED -> deck.reviewed > 0
        }
        matchesSearch && matchesFilter
    }

    StudyPage(
        title = "Biblioteca",
        subtitle = "Explora tus áreas, revisa tu progreso y empieza a estudiar.",
        illustration = IllustrationType.Learning
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Áreas", subjectStats.size.toString(), Modifier.weight(1f))
            MetricCard("Tarjetas", totalCards.toString(), Modifier.weight(1f))
            MetricCard("Pendientes", totalDueCards.toString(), Modifier.weight(1f))
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Buscar un área") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeckFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.label) }
                )
            }
        }
        if (subjectStats.isEmpty()) {
            MedicalIllustration(
                IllustrationType.Empty,
                modifier = Modifier.fillMaxWidth(),
                size = 160.dp
            )
            InfoCard("Biblioteca vacía", "Aún no hay áreas de estudio disponibles.")
        } else if (matchingDecks.isEmpty()) {
            InfoCard(
                "Sin resultados",
                if (searchQuery.isNotBlank()) {
                    "No encontramos áreas que coincidan con “${searchQuery.trim()}”."
                } else {
                    "No hay áreas que coincidan con este filtro."
                }
            )
        } else {
            matchingDecks.forEach { deck ->
                DeckCard(
                    subject = deck.subject,
                    reviewedCount = deck.reviewed,
                    totalCount = deck.total,
                    dueCount = dueCardsBySubject[deck.subject] ?: 0,
                    onClick = { onDeckClick(deck.subject) }
                )
            }
        }
    }
}

private enum class DeckFilter(val label: String) {
    ALL("Todas"),
    DUE("Pendientes"),
    STUDIED("Estudiadas")
}

@Composable
fun MemoryStatisticsScreen(
    reviewStats: ReviewStats,
    subjectStats: List<SubjectStats>,
    quizStats: QuizStats
) {
    StudyPage(
        title = "Estadísticas de memoria",
        subtitle = "Resumen descriptivo de la actividad guardada; no es una evaluación clínica.",
        illustration = IllustrationType.Analytics
    ) {
        InfoCard(
            title = "Repasos registrados",
            text = "${reviewStats.totalReviews} repasos en total; " +
                "${reviewStats.reviewedToday} durante el día de hoy."
        )
        InfoCard(
            title = "Tarjetas dominadas",
            text = "${reviewStats.masteredCards} tarjetas han alcanzado tres o más repasos."
        )
        InfoCard(
            title = "Constancia",
            text = "Racha actual registrada: ${reviewStats.streakDays} días."
        )
        InfoCard(
            title = "Evaluación activa",
            text = "${quizStats.correctAnswers} de ${quizStats.totalAnswers} respuestas correctas " +
                "(${quizStats.accuracyPercent}%). ${quizStats.dueQuestions} preguntas vencidas."
        )
        subjectStats.forEach { deck ->
            val percentage = if (deck.total == 0) 0 else deck.reviewed * 100 / deck.total
            InfoCard(
                title = deck.subject,
                text = "$percentage % de tarjetas con al menos un repaso " +
                    "(${deck.reviewed}/${deck.total})."
            )
        }
        if (subjectStats.isEmpty()) {
            Text("Aún no hay mazos para calcular estadísticas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CalendarInfoScreen(reviewStats: ReviewStats, cards: List<Flashcard>) {
    StudyPage(
        title = "Calendario de repasos",
        subtitle = "Consulta tus repasos próximos y tu actividad reciente.",
        illustration = IllustrationType.Calendar
    ) {
        InfoCard(
            title = "Actividad registrada",
            text = "${reviewStats.reviewedToday} repasos hoy y una racha actual de " +
                "${reviewStats.streakDays} días."
        )
        InfoCard("Tarjetas pendientes", "${reviewStats.dueCards} tarjetas están listas para repasar.")
        val upcoming = cards
            .filter { it.dueAtEpochMillis > System.currentTimeMillis() }
            .sortedBy { it.dueAtEpochMillis }
            .take(8)
        if (upcoming.isEmpty()) {
            InfoCard("Próximos repasos", "Aún no hay repasos futuros programados.")
        } else {
            Text("Próximos repasos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val formatter = SimpleDateFormat("EEE d MMM · HH:mm", Locale.getDefault())
            upcoming.forEach { card ->
                InfoCard(
                    title = card.subject,
                    text = "${formatter.format(Date(card.dueAtEpochMillis))} · ${card.question}"
                )
            }
        }
    }
}

@Composable
fun ConfigurationInfoScreen(
    darkThemeEnabled: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    StudyPage(
        title = "Configuración",
        subtitle = "Personaliza cómo se ve y se comporta tu espacio de estudio.",
        illustration = IllustrationType.Learning
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Modo oscuro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (darkThemeEnabled) "Activado" else "Desactivado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(
                    checked = darkThemeEnabled,
                    onCheckedChange = onDarkThemeChange
                )
            }
        }
        InfoCard(
            title = "Repetición espaciada",
            text = "SM-2 programa tarjetas y preguntas: los errores vuelven en 10 minutos y " +
                "los aciertos amplían progresivamente el intervalo."
        )
        InfoCard(
            title = "Sesiones de repaso",
            text = "Las tarjetas vencidas se muestran primero. Puedes ir adelante y atrás; " +
                "al calificarlas, su siguiente fecha se calcula con SM-2."
        )
    }
}

@Composable
fun EmptyReviewScreen(onBrowseDecks: () -> Unit) {
    StudyPage(
        title = "Repaso",
        subtitle = "No quedan repasos pendientes en esta sesión.",
        illustration = IllustrationType.Empty
    ) {
        Button(onClick = onBrowseDecks, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mazos y practicar")
        }
    }
}

@Composable
fun ReviewCompleteScreen(
    reviewedCount: Int,
    onBrowseDecks: () -> Unit,
    onRestart: () -> Unit
) {
    StudyPage(
        title = "¡Sesión completada!",
        subtitle = "Terminaste las tarjetas de esta sesión.",
        illustration = IllustrationType.Analytics
    ) {
        InfoCard(
            title = "Buen trabajo",
            text = "Calificaste $reviewedCount tarjetas. Sus próximos repasos quedaron programados."
        )
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
            Text("Empezar otra sesión")
        }
        Button(onClick = onBrowseDecks, modifier = Modifier.fillMaxWidth()) {
            Text("Explorar mazos")
        }
    }
}

@Composable
fun StudyPage(
    title: String,
    subtitle: String,
    illustration: IllustrationType? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            illustration?.let { type ->
                MedicalIllustration(type = type, size = 68.dp)
            }
        }
        content()
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
