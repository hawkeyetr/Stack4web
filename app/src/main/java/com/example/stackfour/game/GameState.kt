package com.example.stackfour.game

/**
 * Represents the players in the game.
 */
enum class Player {
    NONE,
    PLAYER_ONE, // Steven or human
    PLAYER_TWO  // AI or second human
}

/**
 * Defines a 3D coordinate on the game board.
 */
data class Position(val x: Int, val y: Int, val z: Int)

/**
 * The core game state and logic.
 * It uses a 3D array internally to represent the board, allowing both 2D and 3D play.
 * For 2D games, depth will be 1.
 */
class GameState(
    val width: Int,  // Columns (x)
    val height: Int, // Rows (y)
    val depth: Int,  // Layers (z)
    val winCondition: Int // Number in a row needed to win
) {
    // 3D array: [x][y][z]
    val board: Array<Array<Array<Player>>> = Array(width) {
        Array(height) {
            Array(depth) { Player.NONE }
        }
    }

    var currentPlayer = Player.PLAYER_ONE
        private set

    var winner: Player = Player.NONE
        private set

    var isDraw: Boolean = false
        private set

    /**
     * Checks if a move is valid. A move is only valid if the slot isn't completely full
     * for games that stack. Wait, in tic-tac-toe (2D, depth=1) you can place anywhere empty.
     * In Connect 4, you drop it in a column and it falls to the lowest y.
     * In Stack Four (3D), you choose (x, z) and it falls to the lowest empty y.
     */
    fun isValidMove(x: Int, z: Int): Boolean {
        if (x < 0 || x >= width || z < 0 || z >= depth) return false
        if (winner != Player.NONE || isDraw) return false

        // Check if there is any empty spot in the column (x, z)
        return getLowestEmptyY(x, z) != -1
    }

    /**
     * Finds the lowest empty y coordinate for a given (x, z) position.
     * Returns -1 if the column is full.
     */
    private fun getLowestEmptyY(x: Int, z: Int): Int {
        for (y in 0 until height) {
            if (board[x][y][z] == Player.NONE) {
                return y
            }
        }
        return -1
    }

    /**
     * Applies a move by the current player.
     * @return true if the move was successful, false otherwise.
     */
    fun makeMove(x: Int, z: Int): Boolean {
        if (!isValidMove(x, z)) return false

        val y = getLowestEmptyY(x, z)
        if (y != -1) {
            board[x][y][z] = currentPlayer
            if (checkWin(x, y, z, currentPlayer)) {
                winner = currentPlayer
            } else if (checkDraw()) {
                isDraw = true
            } else {
                currentPlayer = if (currentPlayer == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE
            }
            return true
        }
        return false
    }

    /**
     * A helper for AI or tests to place at a specific (x, y, z).
     * Bypasses the gravity check. Used for pure 2D games where gravity doesn't apply (like tic-tac-toe)
     * if we ever wanted to support non-gravity modes. But for now, let's keep gravity.
     */
    internal fun setCell(x: Int, y: Int, z: Int, player: Player) {
        board[x][y][z] = player
    }

    /**
     * Reverts a move (useful for AI Minimax).
     */
    fun undoMove(x: Int, y: Int, z: Int) {
        board[x][y][z] = Player.NONE
        winner = Player.NONE
        isDraw = false
        currentPlayer = if (currentPlayer == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE
    }

    private fun checkDraw(): Boolean {
        for (x in 0 until width) {
            for (z in 0 until depth) {
                if (getLowestEmptyY(x, z) != -1) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Checks if the player who just played at (lastX, lastY, lastZ) has won.
     */
    private fun checkWin(lastX: Int, lastY: Int, lastZ: Int, player: Player): Boolean {
        // All 26 possible 3D directions (13 pairs of opposite directions)
        val directions = listOf(
            // 2D directions (z = 0)
            Triple(1, 0, 0), Triple(0, 1, 0), Triple(1, 1, 0), Triple(1, -1, 0),
            // 3D directions (z = 1)
            Triple(0, 0, 1), Triple(1, 0, 1), Triple(-1, 0, 1),
            Triple(0, 1, 1), Triple(0, -1, 1),
            Triple(1, 1, 1), Triple(-1, -1, 1),
            Triple(1, -1, 1), Triple(-1, 1, 1)
        )

        for (dir in directions) {
            val count = 1 + countInDirection(lastX, lastY, lastZ, dir.first, dir.second, dir.third, player) +
                            countInDirection(lastX, lastY, lastZ, -dir.first, -dir.second, -dir.third, player)
            if (count >= winCondition) {
                return true
            }
        }
        return false
    }

    private fun countInDirection(startX: Int, startY: Int, startZ: Int, dx: Int, dy: Int, dz: Int, player: Player): Int {
        var count = 0
        var x = startX + dx
        var y = startY + dy
        var z = startZ + dz

        while (x in 0 until width && y in 0 until height && z in 0 until depth && board[x][y][z] == player) {
            count++
            x += dx
            y += dy
            z += dz
        }
        return count
    }

    /**
     * Creates a deep copy of the game state, useful for AI simulation.
     */
    fun copy(): GameState {
        val newState = GameState(width, height, depth, winCondition)
        newState.currentPlayer = this.currentPlayer
        newState.winner = this.winner
        newState.isDraw = this.isDraw
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    newState.board[x][y][z] = this.board[x][y][z]
                }
            }
        }
        return newState
    }

    // For Minimax evaluate utility
    fun getScoreForPlayer(player: Player): Int {
        if (winner == player) return 1000
        if (winner != Player.NONE) return -1000
        return 0
    }

    /**
     * Used for AI simulation to check if a specific player's move would win.
     */
    internal fun forceCurrentPlayer(player: Player) {
        currentPlayer = player
    }

    internal fun loadState(
        currentPlayer: Player,
        winner: Player,
        isDraw: Boolean,
        boardData: String
    ) {
        this.currentPlayer = currentPlayer
        this.winner = winner
        this.isDraw = isDraw

        var index = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until depth) {
                    val p = boardData[index++]
                    board[x][y][z] = when (p) {
                        '1' -> Player.PLAYER_ONE
                        '2' -> Player.PLAYER_TWO
                        else -> Player.NONE
                    }
                }
            }
        }
    }
}
