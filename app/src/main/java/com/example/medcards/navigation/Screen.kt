package com.example.medcards.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Inicio : Screen("inicio", "Inicio", Icons.Default.Home)
    data object Repaso : Screen("repaso", "Repaso", Icons.Default.Layers)
    data object Pruebas : Screen("pruebas", "Tests rápidos", Icons.AutoMirrored.Filled.Assignment)
    data object Metricas : Screen("metricas", "Métricas", Icons.Default.Timeline)
    data object Pro : Screen("pro", "MediPass Pro", Icons.Default.WorkspacePremium)
    data object Mazos : Screen("mazos", "Biblioteca", Icons.Default.LocalLibrary)
    data object Preuniversitario : Screen("preuniversitario", "Preuniversitario", Icons.Default.School)
    data object Calendario : Screen("calendario", "Calendario de repasos", Icons.Default.DateRange)
    data object Estadisticas : Screen("estadisticas", "Estadísticas de memoria", Icons.Default.Analytics)
    data object Configuracion : Screen("configuracion", "Configuración", Icons.Default.Settings)
    data object Perfil : Screen("perfil", "Mi perfil", Icons.Default.AccountCircle)
    data object SesionRepaso : Screen("sesion-repaso", "Sesión de repaso", Icons.Default.Layers)

    companion object {
        val bottomNavigation: List<Screen>
            get() = listOf(Inicio, Repaso, Mazos, Metricas)

        val drawerNavigation: List<Screen>
            get() = listOf(Pruebas, Pro, Preuniversitario, Calendario, Estadisticas, Configuracion)
    }
}
