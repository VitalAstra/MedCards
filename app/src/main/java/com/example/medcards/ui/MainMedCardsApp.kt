package com.example.medcards.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medcards.data.model.Flashcard
import com.example.medcards.data.repository.FlashcardRepository
import com.example.medcards.domain.Rating
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.navigation.Screen
import com.example.medcards.ui.components.MedCardsDrawerSheet
import com.example.medcards.ui.screens.CalendarInfoScreen
import com.example.medcards.ui.screens.ConfigurationInfoScreen
import com.example.medcards.ui.screens.DeckCatalogScreen
import com.example.medcards.ui.screens.EmptyReviewScreen
import com.example.medcards.ui.screens.FlashcardScreen
import com.example.medcards.ui.screens.HomeScreen
import com.example.medcards.ui.screens.MemoryStatisticsScreen
import com.example.medcards.ui.screens.MetricsScreen
import com.example.medcards.ui.screens.ProDemoScreen
import com.example.medcards.ui.screens.WhatsAppInfoScreen
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.CobaltBlueContainer
import com.example.medcards.ui.theme.TextMuted
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMedCardsApp(
    repository: FlashcardRepository,
    calculator: SpacedRepetitionCalculator
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Inicio.route
    val cards: SnapshotStateList<Flashcard> = remember(repository) {
        androidx.compose.runtime.mutableStateListOf<Flashcard>().apply {
            addAll(repository.getAllFlashcards())
        }
    }
    var refreshKey by rememberSaveable { mutableIntStateOf(0) }
    var selectedSubject by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCardId by rememberSaveable { mutableStateOf<String?>(null) }
    val subjectStats = remember(repository, refreshKey) { repository.getSubjectStats() }
    val reviewStats = remember(repository, refreshKey) { repository.getReviewStats() }
    val reviewCards = cards.filter { selectedSubject == null || it.subject == selectedSubject }

    fun navigate(screen: Screen) {
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun startReview(subject: String?) {
        selectedSubject = subject
        selectedCardId = cards.firstOrNull { subject == null || it.subject == subject }?.id
        navigate(Screen.Repaso)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MedCardsDrawerSheet(
                currentRoute = currentRoute,
                deckCount = subjectStats.size,
                onNavigate = { screen ->
                    coroutineScope.launch {
                        drawerState.close()
                        navigate(screen)
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "MedCards",
                            color = CobaltBlue,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CardWhite,
                        navigationIconContentColor = CobaltBlue
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = CardWhite,
                    tonalElevation = 4.dp
                ) {
                    Screen.bottomNavigation.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigate(screen) },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CobaltBlue,
                                selectedTextColor = CobaltBlue,
                                indicatorColor = CobaltBlueContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Inicio.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Inicio.route) {
                    HomeScreen(
                        subjectStats = subjectStats,
                        reviewStats = reviewStats,
                        onStartReviewClick = { startReview(null) },
                        onDeckClick = { subject -> startReview(subject) }
                    )
                }
                composable(Screen.Repaso.route) {
                    val card = reviewCards.firstOrNull { it.id == selectedCardId }
                        ?: reviewCards.firstOrNull()
                    if (card == null) {
                        EmptyReviewScreen(onBrowseDecks = { navigate(Screen.Mazos) })
                    } else {
                        FlashcardScreen(
                            card = card,
                            calculator = calculator,
                            onRated = { rating: Rating ->
                                val updatedCard = calculator.calculateNextReview(card, rating)
                                repository.updateCardProgress(updatedCard, rating)
                                val cardIndex = cards.indexOfFirst { it.id == updatedCard.id }
                                check(cardIndex >= 0) {
                                    "No se encontró la tarjeta '${updatedCard.id}' en la lista de estudio."
                                }
                                cards[cardIndex] = updatedCard
                                refreshKey++
                                val currentIndex = reviewCards.indexOfFirst { it.id == card.id }
                                selectedCardId = if (reviewCards.size > 1) {
                                    reviewCards[(currentIndex + 1).mod(reviewCards.size)].id
                                } else {
                                    updatedCard.id
                                }
                            }
                        )
                    }
                }
                composable(Screen.Metricas.route) {
                    MetricsScreen(reviewStats, subjectStats, cards.size)
                }
                composable(Screen.Pro.route) { ProDemoScreen() }
                composable(Screen.Mazos.route) {
                    DeckCatalogScreen(
                        subjectStats = subjectStats,
                        onDeckClick = { subject -> startReview(subject) }
                    )
                }
                composable(Screen.Calendario.route) { CalendarInfoScreen(reviewStats) }
                composable(Screen.Estadisticas.route) {
                    MemoryStatisticsScreen(reviewStats, subjectStats)
                }
                composable(Screen.WhatsApp.route) { WhatsAppInfoScreen() }
                composable(Screen.Configuracion.route) { ConfigurationInfoScreen() }
            }
        }
    }
}
