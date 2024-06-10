package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.MainApplication
import com.example.recipeapp.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val recipeDao = MainApplication.database.recipeDao()

    val recipeList: LiveData<List<RecipeWithDetails>> = recipeDao.getAllRecipesWithDetails()

    fun addRecipeWithIngredients(title: String, instructions: String, multiplier: Float, ingredients: List<Ingredient>) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipeId = recipeDao.insertRecipe(Recipe(title = title, instructions = instructions, multiplier = multiplier)).toInt()
            ingredients.forEach { ingredient ->
                val ingredientId = recipeDao.insertIngredient(ingredient).toInt()
                recipeDao.insertRecipeIngredientCrossRef(RecipeIngredientCrossRef(recipeId, ingredientId))
            }
        }
    }

    // TAG
    fun addTag(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.insertTag(tag)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.deleteTag(tag)
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateTag(tag)
        }
    }

    fun addTagToRecipe(recipeId: Int, tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            val tagId = recipeDao.insertTag(tag).toInt()
            recipeDao.insertRecipeTagCrossRef(RecipeTagCrossRef(recipeId, tagId))
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.deleteRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateRecipe(recipe)
        }
    }

    // INGREDIENT
    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.insertIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.deleteIngredient(ingredient)
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateIngredient(ingredient)
        }
    }

}
