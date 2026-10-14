package com.example.medcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.MediPassPlan
import com.example.medcards.ui.components.IllustrationType
import com.example.medcards.ui.components.MedicalIllustration
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.CobaltBlueDark
import com.example.medcards.ui.theme.GoldBadgeEnd
import com.example.medcards.ui.theme.GoldBadgeStart
import com.example.medcards.ui.theme.GoldBadgeText
import com.example.medcards.ui.theme.MintGreen

@Composable
fun MediPassProScreen(
    isPremiumActive: Boolean,
    activePlan: MediPassPlan,
    onSelectPlan: (MediPassPlan) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ProHero(isPremiumActive)
        Text(
            text = "Tu espacio clínico, sin límites",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BenefitRow(Icons.Default.LocalLibrary, "Mazos y repasos ilimitados")
            BenefitRow(Icons.Default.Insights, "Métricas de aprendizaje detalladas")
            BenefitRow(Icons.Default.NotificationsActive, "Planifica tus sesiones de estudio")
            BenefitRow(Icons.Default.CloudDone, "Acceso a funciones exclusivas")
        }
        Text(
            text = "Tu plan",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        PlanOption(
            title = "Mensual",
            detail = "Flexibilidad mes a mes",
            selected = activePlan == MediPassPlan.MONTHLY,
            onClick = { onSelectPlan(MediPassPlan.MONTHLY) }
        )
        PlanOption(
            title = "Semestral",
            detail = "Ahorra frente al pago mensual",
            badge = "MEJOR VALOR",
            selected = activePlan == MediPassPlan.SEMIANNUAL,
            onClick = { onSelectPlan(MediPassPlan.SEMIANNUAL) }
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = if (isPremiumActive) "MediPass Pro está activo." else
                    "Plan seleccionado: ${if (activePlan == MediPassPlan.MONTHLY) "Mensual" else "Semestral"}.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ProHero(isPremiumActive: Boolean) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CobaltBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(CobaltBlue, CobaltBlueDark)))
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.62f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(GoldBadgeStart, GoldBadgeEnd)))
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldBadgeText
                    )
                    Text(
                        text = if (isPremiumActive) "MEMBRESÍA ACTIVA" else "MEDIPASS PRO",
                        color = GoldBadgeText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = "Estudia con\nmás enfoque.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CardWhite,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Tu ritmo. Tu progreso. Tu futuro clínico.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CardWhite.copy(alpha = 0.85f)
                )
            }
            MedicalIllustration(
                type = IllustrationType.Premium,
                modifier = Modifier.align(Alignment.BottomEnd),
                size = 104.dp
            )
        }
    }
}

@Composable
private fun BenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PlanOption(
    title: String,
    detail: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (selected) 2.dp else 1.dp,
            brush = Brush.linearGradient(
                if (selected) listOf(CobaltBlue, MintGreen)
                else listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.surfaceVariant
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = if (selected) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                badge?.let {
                    Surface(color = GoldBadgeEnd, shape = RoundedCornerShape(50)) {
                        Text(
                            it,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            color = GoldBadgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
