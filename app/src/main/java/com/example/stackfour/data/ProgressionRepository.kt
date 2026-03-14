package com.example.stackfour.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ProgressionRepository(private val context: Context) {

    companion object {
        val SCORE = intPreferencesKey("score")
        val CURRENT_LEVEL = intPreferencesKey("current_level")
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val AI_DIFFICULTY = intPreferencesKey("ai_difficulty")
    }

    val scoreFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[SCORE] ?: 0
        }

    val currentLevelFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_LEVEL] ?: 1 // Default to Level 1
        }

    val selectedThemeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_THEME] ?: "Classic"
        }

    val aiDifficultyFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[AI_DIFFICULTY] ?: 1 // Default to AI Level 1
        }

    suspend fun updateScore(newScore: Int) {
        context.dataStore.edit { settings ->
            settings[SCORE] = newScore

            // Auto level up and scale AI difficulty based on score
            if (newScore >= 15) {
                settings[CURRENT_LEVEL] = 4 // Stack Four (3D)
                settings[AI_DIFFICULTY] = 4 // Hard Minimax
            } else if (newScore >= 10) {
                settings[CURRENT_LEVEL] = 3 // 3D Tic Tac Toe
                settings[AI_DIFFICULTY] = 3 // Easy Minimax
            } else if (newScore >= 5) {
                settings[CURRENT_LEVEL] = 2 // 4x4 2D
                settings[AI_DIFFICULTY] = 2 // Blocks & Wins
            } else {
                settings[CURRENT_LEVEL] = 1 // 3x3 2D
                settings[AI_DIFFICULTY] = 1 // Random
            }
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { settings ->
            settings[SELECTED_THEME] = theme
        }
    }
}
