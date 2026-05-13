package com.nallanudi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nallanudi.app.theme.NallaNudiTheme
import com.nallanudi.app.ui.flashcard.FlashcardScreen
import com.nallanudi.app.ui.home.HomeScreen
import com.nallanudi.app.ui.mylist.MyListScreen
import com.nallanudi.app.ui.quiz.QuizScreen
import com.nallanudi.app.ui.search.SearchScreen
import com.nallanudi.app.ui.search.TermDetailScreen
import com.nallanudi.app.viewmodel.DictionaryViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DictionaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initTts()
        setContent {
            NallaNudiTheme {
                NallaNudiApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NallaNudiApp(viewModel: DictionaryViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomRoutes = listOf("home", "search", "mylist", "quiz")
    val showBottomBar = currentRoute in bottomRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, "Home") },
                        label = { Text("ಮನೆ") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Search, "Search") },
                        label = { Text("ಹುಡುಕಿ") },
                        selected = currentRoute == "search",
                        onClick = {
                            navController.navigate("search") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Bookmark, "My List") },
                        label = { Text("ಪಟ್ಟಿ") },
                        selected = currentRoute == "mylist",
                        onClick = {
                            navController.navigate("mylist") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Star, "Quiz") },
                        label = { Text("Quiz") },
                        selected = currentRoute == "quiz",
                        onClick = {
                            navController.navigate("quiz") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onTermClick = { navController.navigate("detail/$it") },
                    onSearchClick = { navController.navigate("search") }
                )
            }
            composable("search") {
                SearchScreen(
                    viewModel = viewModel,
                    onTermClick = { navController.navigate("detail/$it") }
                )
            }
            composable("mylist") {
                MyListScreen(
                    viewModel = viewModel,
                    onTermClick = { navController.navigate("detail/$it") },
                    onFlashcardsClick = { navController.navigate("flashcard") }
                )
            }
            composable("quiz") {
                QuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("detail/{termId}") { backStackEntry ->
                val termId = backStackEntry.arguments
                    ?.getString("termId")?.toIntOrNull() ?: return@composable
                TermDetailScreen(
                    termId = termId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("flashcard") {
                FlashcardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}