package com.example.medcards.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.TextMuted

private data class NavigationDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val destinations = listOf(
    NavigationDestination(route = "home", label = "Inicio", icon = Icons.Filled.Home),
    NavigationDestination(route = "decks", label = "Mazos", icon = Icons.Filled.Style),
    NavigationDestination(route = "progress", label = "Progreso", icon = Icons.Filled.Analytics),
    NavigationDestination(route = "profile", label = "Perfil", icon = Icons.Filled.Person)
)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (route: String) -> Unit
) {
    NavigationBar(containerColor = CardWhite) {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CardWhite,
                    selectedTextColor = CobaltBlue,
                    indicatorColor = CobaltBlue,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
