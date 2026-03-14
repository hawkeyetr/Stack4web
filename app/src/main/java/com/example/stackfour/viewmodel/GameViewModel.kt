package com.example.stackfour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackfour.data.ProgressionRepository
import com.example.stackfour.game.AIPlayer
import com.example.stackfour.game.GameState
import com.example.stackfour.game.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class GameViewModel(private val repository: ProgressionRepository) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _isAITurn = MutableStateFlow(false)
    val isAITurn: StateFlow<Boolean> = _isAITurn.asStateFlow()

    private val _gameMode = MutableStateFlow(GameMode.VS_COMPUTER)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private var aiPlayer: AIPlayer = AIPlayer(1)

    enum class GameMode {
        PASS_AND_PLAY,
        VS_COMPUTER
    }

    init {
        // Initialize AI difficulty from data store when viewmodel is created
        viewModelScope.launch {
            val difficulty = repository.aiDifficultyFlow.first()
            aiPlayer = AIPlayer(difficulty)
        }
    }

    fun startNewGame(mode: GameMode, level: Int) {
        _gameMode.value = mode
        val state = when (level) {
            1 -> GameState(3, 3, 1, 3) // 3x3 Tic Tac Toe
            2 -> GameState(4, 4, 1, 4) // 4x4 Connect 4 (2D)
            3 -> GameState(3, 3, 3, 3) // 3x3x3 3D Tic Tac Toe
            4 -> GameState(4, 4, 4, 4) // 4x4x4 Stack Four
            else -> GameState(4, 4, 4, 4)
        }
        _gameState.value = state
        _isAITurn.value = false

        viewModelScope.launch {
             val diff = repository.aiDifficultyFlow.first()
             aiPlayer.difficultyLevel = diff
        }
    }

    fun clearGame() {
        _gameState.value = null
    }

    fun handleMove(x: Int, z: Int) {
        if (_isAITurn.value) return // Block input during AI turn

        val currentState = _gameState.value ?: return
        if (currentState.winner != Player.NONE || currentState.isDraw) return

        if (currentState.makeMove(x, z)) {
            // Trigger recomposition
            _gameState.value = currentState.copy()

            checkPostMove(currentState)
        }
    }

    private fun checkPostMove(currentState: GameState) {
        if (currentState.winner != Player.NONE) {
            if (currentState.winner == Player.PLAYER_ONE) {
                // Steven won! Give him points.
                viewModelScope.launch {
                    val currentScore = repository.scoreFlow.first()
                    repository.updateScore(currentScore + 1)
                }
            } else {
                // Computer won.
            }
        } else if (!currentState.isDraw && _gameMode.value == GameMode.VS_COMPUTER && currentState.currentPlayer == Player.PLAYER_TWO) {
            // It's the AI's turn
            _isAITurn.value = true
            viewModelScope.launch {
                delay(500) // Small delay for better UX
                playAITurn(currentState)
            }
        }
    }

    private fun playAITurn(currentState: GameState) {
        val move = aiPlayer.getNextMove(currentState)
        if (move != null) {
            currentState.makeMove(move.first, move.second)
            _gameState.value = currentState.copy()
            checkPostMove(currentState)
        }
        _isAITurn.value = false
    }

    fun setTheme(themeName: String) {
        viewModelScope.launch {
            repository.setTheme(themeName)
        }
    }
}
