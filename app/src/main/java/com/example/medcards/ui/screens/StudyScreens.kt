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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.repository.ReviewStats
import com.example.medcards.data.repository.SubjectStats
import com.example.medcards.ui.components.DeckCard
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.CobaltBlueContainer
import com.example.medcards.ui.theme.LightColdGrey
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted

@Composable
fun MetricsScreen(
    reviewStats: ReviewStats,
    subjectStats: List<SubjectStats>,
    totalCards: Int
) {
    StudyPage(
        title = "Métricas",
        subtitle = "Actividad calculada a partir de tus datos locales."
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
        Text("Progreso por mazo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        subjectStats.forEach { deck ->
            Text(
                "${deck.subject}: ${deck.reviewed} de ${deck.total} con al menos un repaso",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (subjectStats.isEmpty()) {
            Text("No hay datos de mazos para mostrar.", color = TextMuted)
        }
    }
}

@Composable
fun DeckCatalogScreen(
    subjectStats: List<SubjectStats>,
    onDeckClick: (String) -> Unit
) {
    StudyPage(
        title = "Mis mazos",
        subtitle = "Selecciona un mazo para comenzar una sesión de repaso."
    ) {
        if (subjectStats.isEmpty()) {
            InfoCard("Sin mazos", "Todavía no hay mazos guardados en este dispositivo.")
        } else {
            subjectStats.forEach { deck ->
                DeckCard(
                    subject = deck.subject,
                    reviewedCount = deck.reviewed,
                    totalCount = deck.total,
                    onClick = { onDeckClick(deck.subject) }
                )
            }
        }
    }
}

@Composable
fun MemoryStatisticsScreen(
    reviewStats: ReviewStats,
    subjectStats: List<SubjectStats>
) {
    StudyPage(
        title = "Estadísticas de memoria",
        subtitle = "Resumen descriptivo de la actividad guardada; no es una evaluación clínica."
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
        subjectStats.forEach { deck ->
            val percentage = if (deck.total == 0) 0 else deck.reviewed * 100 / deck.total
            InfoCard(
                title = deck.subject,
                text = "$percentage % de tarjetas con al menos un repaso " +
                    "(${deck.reviewed}/${deck.total})."
            )
        }
        if (subjectStats.isEmpty()) {
            Text("Aún no hay mazos para calcular estadísticas.", color = TextMuted)
        }
    }
}

@Composable
fun CalendarInfoScreen(reviewStats: ReviewStats) {
    StudyPage(
        title = "Calendario de repasos",
        subtitle = "Una vista de orientación para planificar tu estudio."
    ) {
        InfoCard(
            title = "Actividad registrada",
            text = "${reviewStats.reviewedToday} repasos hoy y una racha actual de " +
                "${reviewStats.streakDays} días."
        )
        InfoCard(
            title = "Fechas de vencimiento",
            text = "El repositorio conserva el intervalo de cada tarjeta, pero no registra " +
                "fechas de vencimiento ni una agenda diaria. Por eso esta versión no muestra " +
                "eventos de calendario inventados."
        )
    }
}

@Composable
fun WhatsAppInfoScreen() {
    StudyPage(
        title = "Atención por WhatsApp",
        subtitle = "Información de disponibilidad"
    ) {
        InfoCard(
            title = "Canal no configurado",
            text = "Esta versión no tiene un número de atención ni una integración de WhatsApp. " +
                "No se enviará ningún mensaje desde la aplicación."
        )
    }
}

@Composable
fun ConfigurationInfoScreen() {
    StudyPage(
        title = "Configuración",
        subtitle = "Estado de esta versión de MedCards"
    ) {
        InfoCard(
            title = "Almacenamiento",
            text = "Las tarjetas y el historial de repaso se guardan localmente en SQLite."
        )
        InfoCard(
            title = "Repetición espaciada",
            text = "El cálculo utiliza el algoritmo SM-2 integrado. Los intervalos se basan " +
                "en la calificación y el historial de cada tarjeta."
        )
        InfoCard(
            title = "Preferencias",
            text = "Aún no hay preferencias de notificaciones o cuenta conectadas en esta versión."
        )
    }
}

@Composable
fun ProDemoScreen() {
    var showBenefits by rememberSaveable { mutableStateOf(false) }
    StudyPage(
        title = "MedCards Pro",
        subtitle = "Vista demostrativa: no hay compras ni suscripciones activas."
    ) {
        InfoCard(
            title = "Explora la propuesta Pro",
            text = "Esta pantalla presenta una idea de producto. No representa una oferta " +
                "disponible ni consulta servicios externos."
        )
        Button(
            onClick = { showBenefits = !showBenefits },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showBenefits) "Ocultar detalles" else "Ver detalles de demostración")
        }
        if (showBenefits) {
            InfoCard(
                title = "Beneficios ilustrativos",
                text = "Organización de mazos, análisis de estudio y opciones de personalización " +
                    "son ejemplos de funcionalidades posibles; no están habilitadas por una compra."
            )
        }
        Text(
            text = "Sin facturación, cobros ni integración con Google Play.",
            color = TextMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun EmptyReviewScreen(onBrowseDecks: () -> Unit) {
    StudyPage(
        title = "Repaso",
        subtitle = "No hay tarjetas disponibles en el mazo seleccionado."
    ) {
        Button(onClick = onBrowseDecks, modifier = Modifier.fillMaxWidth()) {
            Text("Ver mazos")
        }
    }
}

@Composable
private fun StudyPage(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightColdGrey)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
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
        color = CobaltBlueContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = CobaltBlue, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextDark)
            Text(text, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        }
    }
}
