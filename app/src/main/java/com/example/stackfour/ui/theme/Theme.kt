package com.example.stackfour.ui.theme

import androidx.compose.ui.graphics.Color

val SpaceBackground = Color(0xFF0F172A)
val SpaceGrid = Color(0xFF1E293B)
val SpaceP1 = Color(0xFF38BDF8)
val SpaceP2 = Color(0xFFF43F5E)

val JungleBackground = Color(0xFF064E3B)
val JungleGrid = Color(0xFF047857)
val JungleP1 = Color(0xFFFDE047)
val JungleP2 = Color(0xFFEA580C)

val OceanBackground = Color(0xFF0C4A6E)
val OceanGrid = Color(0xFF0284C7)
val OceanP1 = Color(0xFF34D399)
val OceanP2 = Color(0xFFFDE047)

val ClassicBackground = Color(0xFFF1F5F9)
val ClassicGrid = Color(0xFF3B82F6)
val ClassicP1 = Color(0xFFEF4444)
val ClassicP2 = Color(0xFFEAB308)

data class GameTheme(
    val name: String,
    val background: Color,
    val gridColor: Color,
    val p1Color: Color,
    val p2Color: Color,
    val emptyColor: Color = Color.DarkGray
)

fun getThemeByName(name: String): GameTheme {
    return when (name) {
        "Space Explorer" -> GameTheme("Space Explorer", SpaceBackground, SpaceGrid, SpaceP1, SpaceP2, Color.Black)
        "Jungle Safari" -> GameTheme("Jungle Safari", JungleBackground, JungleGrid, JungleP1, JungleP2, Color.Black)
        "Ocean Adventure" -> GameTheme("Ocean Adventure", OceanBackground, OceanGrid, OceanP1, OceanP2, Color.LightGray)
        else -> GameTheme("Classic", ClassicBackground, ClassicGrid, ClassicP1, ClassicP2, Color.White)
    }
}
