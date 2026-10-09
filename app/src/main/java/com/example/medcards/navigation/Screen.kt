package com.example.medcards.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Inicio : Screen("inicio", "Inicio", Icons.Default.Home)
    data object Repaso : Screen("repaso", "Repaso", Icons.Default.Layers)
    data object Metricas : Screen("metricas", "Métricas", Icons.Default.Timeline)
    data object Pro : Screen("pro", "Pro", Icons.Default.WorkspacePremium)
    data object Mazos : Screen("mazos", "Mis mazos de estudio", Icons.Default.Folder)
    data object Calendario : Screen("calendario", "Calendario de repasos", Icons.Default.DateRange)
    data object Estadisticas : Screen("estadisticas", "Estadísticas de memoria", Icons.Default.Analytics)
    data object WhatsApp : Screen("whatsapp", "Atención por WhatsApp", Icons.Default.SupportAgent)
    data object Configuracion : Screen("configuracion", "Configuración", Icons.Default.Settings)

    companion object {
        val bottomNavigation: List<Screen>
            get() = listOf(Inicio, Repaso, Metricas, Pro)

        val drawerNavigation: List<Screen>
            get() = listOf(Mazos, Calendario, Estadisticas, WhatsApp, Configuracion)
    }
}
