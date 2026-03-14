package com.example.stackfour.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stackfour.viewmodel.GameViewModel
import com.example.stackfour.ui.theme.getThemeByName
import com.example.stackfour.data.ProgressionRepository
import com.example.stackfour.game.Player
import com.example.stackfour.ui.components.GameBoard2D
import com.example.stackfour.ui.components.GameBoard3D

@Composable
fun MainMenuScreen(
    score: Int,
    currentLevel: Int,
    selectedTheme: String,
    onStartGame: (GameViewModel.GameMode, Int) -> Unit,
    onThemeSelect: (String) -> Unit
) {
    val theme = getThemeByName(selectedTheme)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Stack Four", style = MaterialTheme.typography.displayLarge, color = theme.p1Color)
        Spacer(modifier = Modifier.height(32.dp))

        Text("Score: $score", style = MaterialTheme.typography.headlineMedium)
        Text("Level: $currentLevel", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStartGame(GameViewModel.GameMode.VS_COMPUTER, currentLevel) },
            colors = ButtonDefaults.buttonColors(containerColor = theme.p1Color)
        ) {
            Text("Play vs Computer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onStartGame(GameViewModel.GameMode.PASS_AND_PLAY, currentLevel) },
            colors = ButtonDefaults.buttonColors(containerColor = theme.p2Color)
        ) {
            Text("Pass and Play")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Theme: $selectedTheme", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            val themes = listOf("Classic", "Space Explorer", "Jungle Safari", "Ocean Adventure")
            themes.forEach { t ->
                Button(
                    onClick = { onThemeSelect(t) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (t == selectedTheme) theme.gridColor else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(t)
                }
            }
        }
    }
}

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    themeName: String,
    onBackToMenu: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val isAITurn by viewModel.isAITurn.collectAsState()
    val theme = getThemeByName(themeName)

    if (gameState == null) {
        // Handle loading or error
        return
    }

    val state = gameState!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackToMenu) {
                Text("Back")
            }
            Text(
                text = "Turn: " + if (state.currentPlayer == Player.PLAYER_ONE) "P1" else "P2",
                style = MaterialTheme.typography.headlineMedium,
                color = if (state.currentPlayer == Player.PLAYER_ONE) theme.p1Color else theme.p2Color
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.depth == 1) {
            GameBoard2D(
                gameState = state,
                theme = theme,
                onMove = { x, z -> viewModel.handleMove(x, z) },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        } else {
            GameBoard3D(
                gameState = state,
                theme = theme,
                onMove = { x, z -> viewModel.handleMove(x, z) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.winner != Player.NONE) {
            Text(
                "Winner: ${if (state.winner == Player.PLAYER_ONE) "Player 1" else "Player 2"}!",
                style = MaterialTheme.typography.headlineLarge,
                color = if (state.winner == Player.PLAYER_ONE) theme.p1Color else theme.p2Color
            )
            Button(onClick = onBackToMenu, modifier = Modifier.padding(top = 16.dp)) {
                Text("Return to Menu")
            }
        } else if (state.isDraw) {
            Text("It's a Draw!", style = MaterialTheme.typography.headlineLarge)
            Button(onClick = onBackToMenu, modifier = Modifier.padding(top = 16.dp)) {
                Text("Return to Menu")
            }
        } else if (isAITurn) {
            Text("Computer is thinking...", style = MaterialTheme.typography.titleMedium, color = theme.p2Color)
        }
    }
}
