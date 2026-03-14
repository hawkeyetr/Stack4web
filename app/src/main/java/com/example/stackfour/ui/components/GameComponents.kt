package com.example.stackfour.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.stackfour.game.GameState
import com.example.stackfour.game.Player
import com.example.stackfour.ui.theme.GameTheme

@Composable
fun GameBoard2D(
    gameState: GameState,
    theme: GameTheme,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(theme.gridColor)
            .padding(8.dp)
    ) {
        // Render 2D grid
        for (y in (gameState.height - 1) downTo 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (x in 0 until gameState.width) {
                    val player = gameState.board[x][y][0]
                    Cell2D(
                        player = player,
                        theme = theme,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clickable { onMove(x, 0) } // z is always 0 for 2D
                    )
                }
            }
        }
    }
}

@Composable
fun Cell2D(player: Player, theme: GameTheme, modifier: Modifier = Modifier) {
    val color = when (player) {
        Player.PLAYER_ONE -> theme.p1Color
        Player.PLAYER_TWO -> theme.p2Color
        Player.NONE -> theme.emptyColor
    }

    Canvas(modifier = modifier) {
        drawCircle(color = color, radius = size.minDimension / 2.2f)
        if (player == Player.NONE) {
            drawCircle(color = Color.Black.copy(alpha = 0.2f), radius = size.minDimension / 2.2f, style = Stroke(width = 2.dp.toPx()))
        }
    }
}

@Composable
fun GameBoard3D(
    gameState: GameState,
    theme: GameTheme,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // A simplified 2.5D view. We display multiple grids (layers of depth)
    // with an isometric-like stagger or just side-by-side.
    // Let's do a top-down layer selector or side-by-side for simplicity.
    Column(
        modifier = modifier
            .background(theme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("3D Board (Z Layers)", color = theme.p1Color, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Since it's 3D, we need a way to visualize the height (Y).
        // A common way for "Stack 4" is to look from the top down and see the highest piece.
        // But for clarity, we can just render the "pegs" as columns.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (z in 0 until gameState.depth) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Row $z", color = Color.White)
                    Column(
                        modifier = Modifier
                            .background(theme.gridColor)
                            .padding(4.dp)
                    ) {
                        for (y in (gameState.height - 1) downTo 0) {
                            Row {
                                for (x in 0 until gameState.width) {
                                    val player = gameState.board[x][y][z]
                                    Cell2D(
                                        player = player,
                                        theme = theme,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .padding(2.dp)
                                            .clickable { onMove(x, z) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
