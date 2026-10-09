package com.example.medcards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medcards.navigation.Screen
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.CobaltBlueContainer
import com.example.medcards.ui.theme.CobaltBlueDark
import com.example.medcards.ui.theme.GoldBadgeEnd
import com.example.medcards.ui.theme.GoldBadgeStart
import com.example.medcards.ui.theme.GoldBadgeText
import com.example.medcards.ui.theme.MintGreen
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted

@Composable
fun MedCardsDrawerSheet(
    currentRoute: String,
    deckCount: Int,
    onNavigate: (Screen) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = CardWhite
    ) {
        MedCardsDrawerHeader()
        Spacer(Modifier.height(8.dp))

        Screen.drawerNavigation.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = screen.title,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        if (screen == Screen.Mazos) {
                            Text(
                                text = deckCount.toString(),
                                color = CobaltBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(CobaltBlue.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                },
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = null,
                        tint = if (selected) CobaltBlue else TextMuted
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = CobaltBlueContainer,
                    selectedTextColor = CobaltBlue,
                    unselectedTextColor = TextDark
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }

        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(MintGreen))
            Text(
                text = "Datos locales en este dispositivo",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MedCardsDrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(CobaltBlue, CobaltBlueDark)))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("MC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
            Column {
                Text("MedCards", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(
                    "Tu espacio de estudio",
                    color = Color.White.copy(alpha = 0.86f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    "MedCards Pro · vista demo",
                    color = GoldBadgeText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(GoldBadgeStart, GoldBadgeEnd)))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
