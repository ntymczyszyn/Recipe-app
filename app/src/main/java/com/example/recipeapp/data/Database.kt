package com.example.recipeapp.data

import androidx.room.RoomDatabase
import androidx.room.Database
import com.example.recipeapp.data.entities.*

@Database(
    entities = [Recipe::class, Ingredient::class, Tag::class, RecipeIngredientCrossRef::class, RecipeTagCrossRef::class],
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        const val NAME = "recipe-database"
    }
}