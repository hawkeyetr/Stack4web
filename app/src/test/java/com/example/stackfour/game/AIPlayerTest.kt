package com.example.stackfour.game

import org.junit.Assert.*
import org.junit.Test

class AIPlayerTest {

    @Test
    fun testAIBlocksImmediateWin() {
        // A 3x3 tic-tac-toe like board
        val game = GameState(3, 3, 1, 3)
        val ai = AIPlayer(difficultyLevel = 2)

        // P1 plays (0,0)
        game.makeMove(0, 0)
        // P2 plays (1,0)
        game.makeMove(1, 0)
        // P1 plays (0,0) (which goes to (0,1) since it's stacked)
        game.makeMove(0, 0)

        // Board currently:
        // P1 (0,1)
        // P1 (0,0)  P2 (1,0)

        // It is P2's turn. P1 is about to win if they place at (0,0) -> gets (0,2).
        // Wait, if P1 places at (0,0), P1 gets 3 in a row.
        // Let's ensure P2 blocks this.
        val move = ai.getNextMove(game)

        // P2 should play at x=0 to block the vertical win
        assertNotNull(move)
        assertEquals(0, move!!.first)
        assertEquals(0, move.second)
    }

    @Test
    fun testAITakesImmediateWin() {
        val game = GameState(3, 3, 1, 3)
        val ai = AIPlayer(difficultyLevel = 2)

        // P1: (0,0)
        game.makeMove(0, 0)
        // P2: (1,0)
        game.makeMove(1, 0)
        // P1: (0,0) -> (0,1)
        game.makeMove(0, 0)
        // P2: (1,0) -> (1,1)
        game.makeMove(1, 0)
        // P1: (2,0)
        game.makeMove(2, 0)

        // P2's turn. P2 has (1,0) and (1,1). If P2 plays x=1, they win vertically.
        val move = ai.getNextMove(game)

        assertNotNull(move)
        assertEquals(1, move!!.first)
        assertEquals(0, move.second)
    }
}
