package com.example.stackfour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.stackfour.data.ProgressionRepository
import com.example.stackfour.ui.screens.GameScreen
import com.example.stackfour.ui.screens.MainMenuScreen
import com.example.stackfour.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = ProgressionRepository(applicationContext)
        val viewModel = GameViewModel(repository)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StackFourApp(viewModel, repository)
                }
            }
        }
    }
}

@Composable
fun StackFourApp(viewModel: GameViewModel, repository: ProgressionRepository) {
    val gameState by viewModel.gameState.collectAsState()
    val score by repository.scoreFlow.collectAsState(initial = 0)
    val currentLevel by repository.currentLevelFlow.collectAsState(initial = 1)
    val selectedTheme by repository.selectedThemeFlow.collectAsState(initial = "Classic")

    if (gameState == null) {
        MainMenuScreen(
            score = score,
            currentLevel = currentLevel,
            selectedTheme = selectedTheme,
            onStartGame = { mode, level ->
                viewModel.startNewGame(mode, level)
            },
            onThemeSelect = { theme ->
                viewModel.setTheme(theme)
            }
        )
    } else {
        GameScreen(
            viewModel = viewModel,
            themeName = selectedTheme,
            onBackToMenu = {
                viewModel.clearGame()
            }
        )
    }
}
