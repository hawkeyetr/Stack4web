package com.example.stackfour.game

import kotlin.random.Random

/**
 * An AI agent for playing Stack Four.
 * Supports multiple difficulty levels that scale as the player gets better.
 */
class AIPlayer(var difficultyLevel: Int = 1) {

    /**
     * Determines the best move for the AI given the current GameState.
     * Returns a pair of (x, z) coordinates.
     */
    fun getNextMove(gameState: GameState): Pair<Int, Int>? {
        return when (difficultyLevel) {
            1 -> getRandomMove(gameState)
            2 -> getBlockingMove(gameState) ?: getRandomMove(gameState)
            else -> getMinimaxMove(gameState, depth = difficultyLevel - 1)
        }
    }

    private fun getRandomMove(gameState: GameState): Pair<Int, Int>? {
        val validMoves = getValidMoves(gameState)
        if (validMoves.isEmpty()) return null
        return validMoves[Random.nextInt(validMoves.size)]
    }

    /**
     * Level 2 AI: Plays a random move unless the opponent is about to win,
     * in which case it blocks the win. It also takes immediate wins for itself.
     */
    private fun getBlockingMove(gameState: GameState): Pair<Int, Int>? {
        val validMoves = getValidMoves(gameState)
        val aiPlayer = gameState.currentPlayer
        val opponentPlayer = if (aiPlayer == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE

        // 1. Can we win immediately?
        for (move in validMoves) {
            if (simulatesWin(gameState, move.first, move.second, aiPlayer)) {
                return move
            }
        }

        // 2. Must we block the opponent from winning?
        for (move in validMoves) {
            if (simulatesWin(gameState, move.first, move.second, opponentPlayer)) {
                return move
            }
        }

        return null
    }

    private fun getMinimaxMove(gameState: GameState, depth: Int): Pair<Int, Int>? {
        val validMoves = getValidMoves(gameState)
        if (validMoves.isEmpty()) return null

        var bestScore = Int.MIN_VALUE
        var bestMoves = mutableListOf<Pair<Int, Int>>()

        val aiPlayer = gameState.currentPlayer

        for (move in validMoves) {
            // Apply move
            val nextState = gameState.copy()
            nextState.makeMove(move.first, move.second)

            // If this move wins, just take it
            if (nextState.winner == aiPlayer) {
                return move
            }

            // Minimax evaluation
            val score = minimax(nextState, depth - 1, Int.MIN_VALUE, Int.MAX_VALUE, false, aiPlayer)

            if (score > bestScore) {
                bestScore = score
                bestMoves.clear()
                bestMoves.add(move)
            } else if (score == bestScore) {
                bestMoves.add(move)
            }
        }

        return if (bestMoves.isNotEmpty()) {
            bestMoves[Random.nextInt(bestMoves.size)] // Randomly pick one of the equally good moves
        } else {
            getRandomMove(gameState)
        }
    }

    private fun minimax(gameState: GameState, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean, aiPlayer: Player): Int {
        if (depth == 0 || gameState.winner != Player.NONE || gameState.isDraw) {
            return evaluateBoard(gameState, aiPlayer)
        }

        var currentAlpha = alpha
        var currentBeta = beta
        val validMoves = getValidMoves(gameState)

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (move in validMoves) {
                val nextState = gameState.copy()
                nextState.makeMove(move.first, move.second)
                val eval = minimax(nextState, depth - 1, currentAlpha, currentBeta, false, aiPlayer)
                maxEval = maxOf(maxEval, eval)
                currentAlpha = maxOf(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break // Beta cutoff
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in validMoves) {
                val nextState = gameState.copy()
                nextState.makeMove(move.first, move.second)
                val eval = minimax(nextState, depth - 1, currentAlpha, currentBeta, true, aiPlayer)
                minEval = minOf(minEval, eval)
                currentBeta = minOf(currentBeta, eval)
                if (currentBeta <= currentAlpha) break // Alpha cutoff
            }
            return minEval
        }
    }

    /**
     * Heuristic evaluation of the board.
     * Simple evaluation for now: 1000 for win, -1000 for loss.
     * To make it better, we could add small points for center control or 3-in-a-row.
     */
    private fun evaluateBoard(gameState: GameState, aiPlayer: Player): Int {
        val opponentPlayer = if (aiPlayer == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE
        if (gameState.winner == aiPlayer) return 1000
        if (gameState.winner == opponentPlayer) return -1000
        if (gameState.isDraw) return 0

        // Add basic heuristics for center control or partial matches here if needed
        return 0
    }

    private fun simulatesWin(gameState: GameState, x: Int, z: Int, player: Player): Boolean {
        // We use the copy to check if the move results in a win for the specified player.
        // We temporarily change the current player of the copy state if necessary.
        val simState = gameState.copy()
        // Hack: override the current player to test the hypothetical move.
        // This is safe because copy() creates a new instance.
        while(simState.currentPlayer != player) {
             // make a fake move to swap players or we can just reflection inject,
             // but making a dummy function in GameState is better.
             // Actually, the simplest way is to manually modify the board if we exposed it,
             // but `currentPlayer` drives the game rules.
             // Let's just create a custom method in GameState for test scenarios.
             simState.forceCurrentPlayer(player)
        }

        simState.makeMove(x, z)
        return simState.winner == player
    }

    private fun getValidMoves(gameState: GameState): List<Pair<Int, Int>> {
        val validMoves = mutableListOf<Pair<Int, Int>>()
        for (x in 0 until gameState.width) {
            for (z in 0 until gameState.depth) {
                if (gameState.isValidMove(x, z)) {
                    validMoves.add(Pair(x, z))
                }
            }
        }
        return validMoves
    }
}
