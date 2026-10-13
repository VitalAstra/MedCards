package com.example.medcards.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.medcards.R
import com.example.medcards.data.model.Flashcard
import com.example.medcards.data.model.MediPassPlan
import com.example.medcards.data.model.QuizQuestion
import com.example.medcards.data.repository.FlashcardRepository
import com.example.medcards.data.repository.ProfilePreferencesRepository
import com.example.medcards.data.repository.SubscriptionRepository
import com.example.medcards.domain.Rating
import com.example.medcards.domain.SpacedRepetitionCalculator
import com.example.medcards.navigation.Screen
import com.example.medcards.ui.components.BottomNavigationBar
import com.example.medcards.ui.components.MedCardsDrawerSheet
import com.example.medcards.ui.screens.CalendarInfoScreen
import com.example.medcards.ui.screens.ConfigurationInfoScreen
import com.example.medcards.ui.screens.DeckCatalogScreen
import com.example.medcards.ui.screens.EmptyReviewScreen
import com.example.medcards.ui.screens.FlashcardScreen
import com.example.medcards.ui.screens.HomeScreen
import com.example.medcards.ui.screens.MemoryStatisticsScreen
import com.example.medcards.ui.screens.MetricsScreen
import com.example.medcards.ui.screens.MediPassProScreen
import com.example.medcards.ui.screens.PreUniversityScreen
import com.example.medcards.ui.screens.QuizScreen
import com.example.medcards.ui.screens.ReviewCompleteScreen
import com.example.medcards.ui.screens.ReviewArea
import com.example.medcards.ui.screens.ReviewHubScreen
import com.example.medcards.ui.screens.ProfileScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMedCardsApp(
    repository: FlashcardRepository,
    subscriptionRepository: SubscriptionRepository,
    profilePreferences: ProfilePreferencesRepository,
    calculator: SpacedRepetitionCalculator,
    darkThemeEnabled: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Inicio.route
    val cards: SnapshotStateList<Flashcard> = remember(repository) {
        androidx.compose.runtime.mutableStateListOf<Flashcard>().apply {
            addAll(repository.getAllFlashcards())
        }

    }
    var refreshKey by rememberSaveable { mutableIntStateOf(0) }
    var quizRunId by rememberSaveable { mutableIntStateOf(0) }
    val reviewQueueIds = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    var reviewIndex by rememberSaveable { mutableIntStateOf(0) }
    val reviewedCardIds = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    val quizSession = remember { androidx.compose.runtime.mutableStateListOf<QuizQuestion>() }
    var activePlanName by rememberSaveable {
        mutableStateOf(subscriptionRepository.activePlan.name)
    }
    var profileName by rememberSaveable { mutableStateOf(profilePreferences.displayName) }
    var profilePhotoPath by rememberSaveable { mutableStateOf(profilePreferences.photoPath) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val savedPath = withContext(Dispatchers.IO) {
                        profilePreferences.saveProfilePhoto(uri)
                        profilePreferences.photoPath
                    }
                    profilePhotoPath = savedPath
                } catch (error: IOException) {
                    Toast.makeText(context, error.message ?: "No se pudo guardar la foto.", Toast.LENGTH_LONG).show()
                } catch (error: IllegalStateException) {
                    Toast.makeText(context, error.message ?: "No se pudo guardar la foto.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    val activePlan = MediPassPlan.valueOf(activePlanName)
    val subjectStats = remember(repository, refreshKey) { repository.getSubjectStats() }
    val preUniversitySubjects = subjectStats.filter { isPreUniversitySubject(it.subject) }
    val medicalSubjectStats = subjectStats.filterNot { isPreUniversitySubject(it.subject) }
    val reviewStats = remember(repository, refreshKey) { repository.getReviewStats() }
    val quizStats = remember(repository, refreshKey) { repository.getQuizStats() }

    fun navigate(screen: Screen) {
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun startReview(subject: String?, dueOnly: Boolean = false) {
        val dueIds = repository.getDueFlashcards(subject).map { it.id }
        val practiceIds = if (dueOnly) dueIds else {
            repository.getAllFlashcards()
                .filter { subject == null || it.subject == subject }
                .map { it.id }
        }
        reviewQueueIds.clear()
        reviewQueueIds.addAll(practiceIds)
        reviewedCardIds.clear()
        reviewIndex = 0
        navigate(Screen.SesionRepaso)
    }

    fun startQuiz(subject: String? = null) {
        quizSession.clear()
        val dueQuestions = repository.getDueQuizQuestions()
            .filter { subject == null || it.subject == subject }
        val questions = repository.getQuizQuestions()
            .filter { subject == null || it.subject == subject }
        quizSession.addAll(dueQuestions.ifEmpty { questions })
        quizRunId++
        navigate(Screen.Pruebas)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MedCardsDrawerSheet(
                currentRoute = currentRoute,
                deckCount = medicalSubjectStats.size,
                profileName = profileName,
                profilePhotoPath = profilePhotoPath,
                onProfileClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navigate(Screen.Perfil)
                    }
                },
                onNavigate = { screen ->
                    coroutineScope.launch {
                        drawerState.close()
                        if (screen == Screen.Pruebas) startQuiz() else navigate(screen)
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
                        Image(
                            painter = painterResource(R.drawable.medisynap_wordmark),
                            contentDescription = "MediSynap",
                            modifier = Modifier
                                .width(180.dp)
                                .height(44.dp),
                            contentScale = ContentScale.Fit
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
                        containerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                if (currentRoute != Screen.SesionRepaso.route) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onItemSelected = { route ->
                            navigate(Screen.bottomNavigation.first { it.route == route })
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Inicio.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 14 }
                },
                exitTransition = {
                    fadeOut(tween(140)) + slideOutHorizontally(tween(140)) { -it / 24 }
                },
                popEnterTransition = {
                    fadeIn(tween(220)) + slideInHorizontally(tween(220)) { -it / 14 }
                },
                popExitTransition = {
                    fadeOut(tween(140)) + slideOutHorizontally(tween(140)) { it / 24 }
                }
            ) {
                composable(Screen.Inicio.route) {
                    HomeScreen(
                        subjectStats = medicalSubjectStats,
                        reviewStats = reviewStats,
                        quizStats = quizStats,
                        displayName = profileName,
                        preUniversityCardCount = preUniversitySubjects.sumOf { it.total },
                        onStartReviewClick = { navigate(Screen.Repaso) },
                        onStartQuizClick = { startQuiz() },
                        onDeckClick = { subject -> startReview(subject) },
                        onPreUniversityClick = { navigate(Screen.Preuniversitario) },
                        onMediPassClick = { navigate(Screen.Pro) }
                    )
                }
                composable(Screen.Repaso.route) {
                    val reviewAreas = medicalSubjectStats.map { stats ->
                        ReviewArea(
                            subject = stats.subject,
                            totalCards = stats.total,
                            dueCards = repository.getDueFlashcards(stats.subject).size
                        )
                    }
                    ReviewHubScreen(
                        reviewStats = reviewStats,
                        areas = reviewAreas,
                        onStartReview = { subject, dueOnly -> startReview(subject, dueOnly) }
                    )
                }
                composable(Screen.SesionRepaso.route) {
                    val card = reviewQueueIds.getOrNull(reviewIndex)?.let { cardId ->
                        cards.firstOrNull { it.id == cardId }
                    }
                    if (reviewQueueIds.isNotEmpty() && reviewedCardIds.size == reviewQueueIds.size) {
                        ReviewCompleteScreen(
                            reviewedCount = reviewedCardIds.size,
                            onBrowseDecks = { navigate(Screen.Mazos) },
                            onRestart = { startReview(null, false) }
                        )
                    } else if (card == null) {
                        EmptyReviewScreen(onBrowseDecks = { navigate(Screen.Repaso) })
                    } else {
                        FlashcardScreen(
                            card = card,
                            calculator = calculator,
                            position = reviewIndex + 1,
                            totalCards = reviewQueueIds.size,
                            completedCount = reviewedCardIds.size,
                            isCompleted = card.id in reviewedCardIds,
                            onPrevious = { reviewIndex = (reviewIndex - 1).coerceAtLeast(0) },
                            onNext = {
                                reviewIndex = (reviewIndex + 1).coerceAtMost(reviewQueueIds.lastIndex)
                            },
                            onRated = { rating: Rating ->
                                val updatedCard = calculator.calculateNextReview(
                                    card,
                                    rating,
                                    System.currentTimeMillis()
                                )
                                repository.updateCardProgress(updatedCard, rating)
                                val cardIndex = cards.indexOfFirst { it.id == updatedCard.id }
                                check(cardIndex >= 0) {
                                    "No se encontró la tarjeta '${updatedCard.id}' en la lista de estudio."
                                }
                                cards[cardIndex] = updatedCard
                                if (card.id !in reviewedCardIds) reviewedCardIds.add(card.id)
                                refreshKey++
                                val nextIndex = reviewQueueIds.indices.firstOrNull { index ->
                                    index > reviewIndex && reviewQueueIds[index] !in reviewedCardIds
                                } ?: reviewQueueIds.indices.firstOrNull { index ->
                                    reviewQueueIds[index] !in reviewedCardIds
                                }
                                if (nextIndex != null) reviewIndex = nextIndex
                            }
                        )
                    }
                }
                composable(Screen.Pruebas.route) {
                    val questions = quizSession.ifEmpty {
                        repository.getDueQuizQuestions().ifEmpty { repository.getQuizQuestions() }
                    }
                    QuizScreen(
                        sessionId = quizRunId,
                        questions = questions,
                        onAnswer = { question, isCorrect ->
                            val scheduledCard = calculator.calculateNextReview(
                                Flashcard(
                                    id = question.id,
                                    subject = question.subject,
                                    question = question.question,
                                    answer = question.options[question.correctOptionIndex],
                                    repetitions = question.repetitions,
                                    intervalDays = question.intervalDays,
                                    easeFactor = question.easeFactor,
                                    dueAtEpochMillis = question.dueAtEpochMillis
                                ),
                                if (isCorrect) Rating.GOOD else Rating.AGAIN,
                                System.currentTimeMillis()
                            )
                            repository.updateQuizQuestionProgress(
                                question.copy(
                                    repetitions = scheduledCard.repetitions,
                                    intervalDays = scheduledCard.intervalDays,
                                    easeFactor = scheduledCard.easeFactor,
                                    dueAtEpochMillis = scheduledCard.dueAtEpochMillis
                                ),
                                isCorrect
                            )
                            refreshKey++
                        },
                        onFinish = { navigate(Screen.Inicio) }
                    )
                }
                composable(Screen.Metricas.route) {
                    MetricsScreen(reviewStats, subjectStats, cards.size, quizStats)
                }
                composable(Screen.Pro.route) {
                    MediPassProScreen(
                        isPremiumActive = subscriptionRepository.isPremiumActive,
                        activePlan = activePlan,
                        onSelectPlan = { plan ->
                            subscriptionRepository.selectPlan(plan)
                            activePlanName = plan.name
                        }
                    )
                }
                composable(Screen.Mazos.route) {
                    DeckCatalogScreen(
                        subjectStats = medicalSubjectStats,
                        onDeckClick = { subject -> startReview(subject) }
                    )
                }
                composable(Screen.Preuniversitario.route) {
                    val preUniversityQuestions = repository.getQuizQuestions().filter { question ->
                        isPreUniversitySubject(question.subject)
                    }
                    PreUniversityScreen(
                        subjects = preUniversitySubjects,
                        quizQuestions = preUniversityQuestions,
                        onStudyCards = { subject -> startReview(subject) },
                        onStartQuiz = { subject -> startQuiz(subject) }
                    )
                }
                composable(Screen.Calendario.route) {
                    CalendarInfoScreen(reviewStats, cards.toList())
                }
                composable(Screen.Estadisticas.route) {
                    MemoryStatisticsScreen(reviewStats, subjectStats, quizStats)
                }
                composable(Screen.Configuracion.route) {
                    ConfigurationInfoScreen(
                        darkThemeEnabled = darkThemeEnabled,
                        onDarkThemeChange = onDarkThemeChange
                    )
                }
                composable(Screen.Perfil.route) {
                    ProfileScreen(
                        displayName = profileName,
                        photoPath = profilePhotoPath,
                        reviewStats = reviewStats,
                        quizStats = quizStats,
                        totalCards = cards.size,
                        onPickPhoto = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        onSaveName = { name ->
                            try {
                                profilePreferences.saveDisplayName(name)
                                profileName = name.trim()
                                Toast.makeText(context, "Nombre actualizado.", Toast.LENGTH_SHORT).show()
                            } catch (error: IllegalArgumentException) {
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                            } catch (error: IllegalStateException) {
                                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }

        }
    }
}

private fun isPreUniversitySubject(subject: String): Boolean = when {
    subject.contains("biolog", ignoreCase = true) -> true
    subject.contains("quím", ignoreCase = true) || subject.contains("quim", ignoreCase = true) -> true
    subject.contains("fís", ignoreCase = true) || subject.contains("fis", ignoreCase = true) -> true
    subject.contains("pre-medic", ignoreCase = true) || subject.contains("pre medic", ignoreCase = true) -> true
    else -> false
}
