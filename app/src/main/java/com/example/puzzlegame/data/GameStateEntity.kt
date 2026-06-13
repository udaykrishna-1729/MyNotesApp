package com.example.puzzlegame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 1, // Only ever need one row to store the current state
    val currentScore: Int,
    val highScore: Int,
    val currentLevel: Int,
    // Store grid as a JSON string
    val gridJson: String,
    // Store available pieces as a JSON string
    val availablePiecesJson: String
)
