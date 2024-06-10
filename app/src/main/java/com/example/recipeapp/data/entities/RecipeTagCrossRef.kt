package com.example.recipeapp.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["recipeId", "tagId"])
data class RecipeTagCrossRef(
    val recipeId: Int,
    val tagId: Int
)
