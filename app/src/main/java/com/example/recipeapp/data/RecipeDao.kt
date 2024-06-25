package com.example.recipeapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.recipeapp.data.entities.*

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Transaction
    @Query("SELECT * FROM Recipe")
    fun getAllRecipesWithDetails(): LiveData<List<RecipeWithDetails>>

    @Transaction
    @Query("SELECT * FROM Recipe WHERE id = :recipeId")
    fun getRecipeWithDetailsById(recipeId: Int): LiveData<RecipeWithDetails>
    @Insert
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Insert
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("SELECT * FROM Tag WHERE id = :tagId")
    suspend fun getTagById(tagId: Int): Tag?

    @Insert
    suspend fun insertRecipeIngredientCrossRef(ref: RecipeIngredientCrossRef)

    @Insert
    suspend fun insertRecipeTagCrossRef(ref: RecipeTagCrossRef)

    @Query("DELETE FROM RecipeIngredientCrossRef WHERE recipeId = :recipeId")
    suspend fun deleteRecipeIngredients(recipeId: Int)

    @Query("DELETE FROM RecipeTagCrossRef WHERE recipeId = :recipeId")
    suspend fun deleteRecipeTags(recipeId: Int)

    @Query("SELECT * FROM Ingredient")
    fun getAllIngredients(): LiveData<List<Ingredient>>

    @Query("SELECT * FROM Tag")
    fun getAllTags(): LiveData<List<Tag>>
    @Transaction
    @Query("SELECT * FROM Recipe WHERE id IN (SELECT recipeId FROM RecipeTagCrossRef WHERE tagId = :id)")
    fun getRecipesForTag(id: Int): LiveData<List<RecipeWithDetails>>
    @Query("DELETE FROM RecipeTagCrossRef WHERE tagId = :tagId")
    suspend fun deleteTagFromAllRecipes(tagId: Int)
    @Query("SELECT * FROM Tag INNER JOIN RecipeTagCrossRef ON Tag.id = RecipeTagCrossRef.tagId WHERE RecipeTagCrossRef.recipeId = :recipeId")
    suspend fun getTagsForRecipe(recipeId: Int): List<Tag>

    @Query("SELECT * FROM Tag WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?
    @Delete
    suspend fun deleteRecipeTagCrossRef(recipeTagCrossRef: RecipeTagCrossRef)
    @Delete
    suspend fun deleteRecipeIngredientCrossRef(recipeIngredientCrossRef: RecipeIngredientCrossRef)
    @Transaction
    @Query("SELECT * FROM Ingredient WHERE id IN (SELECT ingredientId FROM RecipeIngredientCrossRef WHERE recipeId = :recipeId)")
    suspend fun getIngredientsForRecipe(recipeId: Int): List<Ingredient>

    @Query("SELECT * FROM Ingredient WHERE name = :name")
    suspend fun getIngredientsByName(name: String): List<Ingredient>
    @Query("DELETE FROM RecipeIngredientCrossRef WHERE ingredientId = :ingredientId")
    suspend fun deleteIngredientFromAllRecipes(ingredientId: Int)
}
