package com.example.recipeapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val instructions: String,
    val multiplier: Float = 1.0f
)
