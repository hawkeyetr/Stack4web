package com.example.stackfour.utils

import com.example.stackfour.game.GameState
import com.example.stackfour.game.Player

object GameSerializer {
    fun serialize(gameState: GameState): String {
        val sb = java.lang.StringBuilder()
        sb.append("${gameState.width},${gameState.height},${gameState.depth},${gameState.winCondition}|")
        sb.append("${gameState.currentPlayer.name}|")
        sb.append("${gameState.winner.name}|")
        sb.append("${gameState.isDraw}|")

        for (x in 0 until gameState.width) {
            for (y in 0 until gameState.height) {
                for (z in 0 until gameState.depth) {
                    val p = when(gameState.board[x][y][z]) {
                        Player.NONE -> "0"
                        Player.PLAYER_ONE -> "1"
                        Player.PLAYER_TWO -> "2"
                    }
                    sb.append(p)
                }
            }
        }
        return sb.toString()
    }

    fun deserialize(data: String): GameState? {
        try {
            val parts = data.split("|")
            if (parts.size != 5) return null

            val dims = parts[0].split(",")
            val width = dims[0].toInt()
            val height = dims[1].toInt()
            val depth = dims[2].toInt()
            val winCondition = dims[3].toInt()

            val gameState = GameState(width, height, depth, winCondition)

            val currentPlayer = Player.valueOf(parts[1])
            val winner = Player.valueOf(parts[2])
            val isDraw = parts[3].toBoolean()

            gameState.loadState(currentPlayer, winner, isDraw, parts[4])
            return gameState
        } catch (e: Exception) {
            return null
        }
    }
}
