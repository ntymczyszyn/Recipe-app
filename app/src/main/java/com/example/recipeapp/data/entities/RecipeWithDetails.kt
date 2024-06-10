package com.example.recipeapp.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RecipeWithDetails(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RecipeIngredientCrossRef::class,
            parentColumn = "recipeId",
            entityColumn = "ingredientId"
        )
    )
    val ingredients: List<Ingredient>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RecipeTagCrossRef::class,
            parentColumn = "recipeId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)