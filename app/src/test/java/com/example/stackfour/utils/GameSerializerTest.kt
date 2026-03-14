package com.example.stackfour.utils

import com.example.stackfour.game.GameState
import com.example.stackfour.game.Player
import org.junit.Assert.*
import org.junit.Test

class GameSerializerTest {

    @Test
    fun testSerializationAndDeserialization() {
        val originalState = GameState(4, 4, 2, 4)

        // Make some moves
        originalState.makeMove(0, 0) // P1
        originalState.makeMove(1, 0) // P2
        originalState.makeMove(0, 0) // P1

        val serializedString = GameSerializer.serialize(originalState)
        val deserializedState = GameSerializer.deserialize(serializedString)

        assertNotNull(deserializedState)

        if (deserializedState != null) {
            assertEquals(originalState.width, deserializedState.width)
            assertEquals(originalState.height, deserializedState.height)
            assertEquals(originalState.depth, deserializedState.depth)
            assertEquals(originalState.winCondition, deserializedState.winCondition)
            assertEquals(originalState.currentPlayer, deserializedState.currentPlayer)
            assertEquals(originalState.winner, deserializedState.winner)
            assertEquals(originalState.isDraw, deserializedState.isDraw)

            // Verify board
            for (x in 0 until originalState.width) {
                for (y in 0 until originalState.height) {
                    for (z in 0 until originalState.depth) {
                        assertEquals(
                            "Mismatch at $x, $y, $z",
                            originalState.board[x][y][z],
                            deserializedState.board[x][y][z]
                        )
                    }
                }
            }
        }
    }
}
