package com.example.recipeapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.recipeapp.data.entities.*

@Dao
interface RecipeDao {

    // RECEPIE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    // RECEPIE WITH DETAILS
    @Transaction
    @Query("SELECT * FROM Recipe")
    fun getAllRecipesWithDetails(): LiveData<List<RecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM Recipe WHERE id = :recipeId")
    fun getRecipeWithDetailsById(recipeId: Int): RecipeWithDetails

    // INGREDIENT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    // TAG
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    // CROSS REF
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredientCrossRef(ref: RecipeIngredientCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeTagCrossRef(ref: RecipeTagCrossRef)


}
