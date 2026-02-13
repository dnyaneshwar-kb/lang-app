package com.example.hsklearner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.hsklearner.ui.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HskLearnerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = Destinations.HOME) {
                        composable(Destinations.HOME) {
                            HomeScreen(
                                onLevelClick = { levelId ->
                                    navController.navigate(Destinations.unitList(levelId))
                                }
                            )
                        }
                        composable(
                            Destinations.UNIT_LIST,
                            arguments = listOf(navArgument("levelId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val levelId = backStackEntry.arguments?.getInt("levelId") ?: return@composable
                            UnitListScreen(
                                levelId = levelId,
                                onUnitClick = { unitId ->
                                    navController.navigate(Destinations.lessonList(unitId))
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            Destinations.LESSON_LIST,
                            arguments = listOf(navArgument("unitId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val unitId = backStackEntry.arguments?.getInt("unitId") ?: return@composable
                            LessonListScreen(
                                unitId = unitId,
                                onLessonClick = { lessonId ->
                                    navController.navigate(Destinations.vocabList(lessonId))
                                },
                                onFlashcardClick = { lessonId ->
                                    navController.navigate(Destinations.flashcards(lessonId))
                                },
                                onQuizClick = { lessonId ->
                                    navController.navigate(Destinations.quiz(lessonId))
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            Destinations.VOCAB_LIST,
                            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
                            VocabularyScreen(
                                lessonId = lessonId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            Destinations.FLASHCARDS,
                            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
                            FlashcardScreen(
                                lessonId = lessonId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            Destinations.QUIZ,
                            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
                            QuizScreen(
                                lessonId = lessonId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
