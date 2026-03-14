package com.example.stackfour.game

import org.junit.Assert.*
import org.junit.Test

class GameStateTest {

    @Test
    fun testHorizontalWin2D() {
        val game = GameState(7, 6, 1, 4)

        game.makeMove(0, 0) // P1 (0,0)
        game.makeMove(0, 0) // P2 (0,1)
        game.makeMove(1, 0) // P1 (1,0)
        game.makeMove(1, 0) // P2 (1,1)
        game.makeMove(2, 0) // P1 (2,0)
        game.makeMove(2, 0) // P2 (2,1)

        assertFalse(game.winner == Player.PLAYER_ONE)

        game.makeMove(3, 0) // P1 (3,0) -> Connect 4!

        assertEquals(Player.PLAYER_ONE, game.winner)
        assertTrue(game.isDraw == false)
    }

    @Test
    fun testVerticalWin2D() {
        val game = GameState(7, 6, 1, 4)

        game.makeMove(0, 0) // P1 (0,0)
        game.makeMove(1, 0) // P2 (1,0)
        game.makeMove(0, 0) // P1 (0,1)
        game.makeMove(1, 0) // P2 (1,1)
        game.makeMove(0, 0) // P1 (0,2)
        game.makeMove(1, 0) // P2 (1,2)
        game.makeMove(0, 0) // P1 (0,3) -> Connect 4!

        assertEquals(Player.PLAYER_ONE, game.winner)
    }

    @Test
    fun testDiagonalWin2D() {
        val game = GameState(7, 6, 1, 4)
        /* Board setup for P1 diagonal (/)
           . . . P1
           . . P1 P2
           . P1 P1 P1
           P1 P2 P2 P2
        */
        // Row 0
        game.makeMove(0, 0) // P1 (0,0)
        game.makeMove(1, 0) // P2 (1,0)
        game.makeMove(2, 0) // P1 (2,0)
        game.makeMove(3, 0) // P2 (3,0)

        // Row 1
        game.makeMove(1, 0) // P1 (1,1)
        game.makeMove(2, 0) // P2 (2,1)
        game.makeMove(4, 0) // P1 (4,0) - Just a throw away move to pass turn
        game.makeMove(3, 0) // P2 (3,1)

        // Row 2
        game.makeMove(2, 0) // P1 (2,2)
        game.makeMove(3, 0) // P2 (3,2)

        // Row 3
        game.makeMove(3, 0) // P1 (3,3) -> Connect 4!

        assertEquals(Player.PLAYER_ONE, game.winner)
    }

    @Test
    fun test3DWin() {
        val game = GameState(4, 4, 4, 4)

        game.makeMove(0, 0) // P1 (0, 0, 0)
        game.makeMove(1, 0) // P2 (1, 0, 0)
        game.makeMove(0, 1) // P1 (0, 0, 1)
        game.makeMove(1, 1) // P2 (1, 0, 1)
        game.makeMove(0, 2) // P1 (0, 0, 2)
        game.makeMove(1, 2) // P2 (1, 0, 2)
        game.makeMove(0, 3) // P1 (0, 0, 3) -> Connect 4 along Z axis!

        assertEquals(Player.PLAYER_ONE, game.winner)
    }
}
