package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.MainApplication
import com.example.recipeapp.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val recipeDao = MainApplication.database.recipeDao()

    val recipeList: LiveData<List<RecipeWithDetails>> = recipeDao.getAllRecipesWithDetails()
    val allIngredients: LiveData<List<Ingredient>> = recipeDao.getAllIngredients()
    val allTags: LiveData<List<Tag>> = recipeDao.getAllTags()
    val showErrorDialog = MutableLiveData(false)

    fun addRecipeWithIngredientsAndTags(title: String, instructions: String, multiplier: Float, ingredients: List<Ingredient>, tags: List<Tag>) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipeId = recipeDao.insertRecipe(Recipe(title = title, instructions = instructions, multiplier = multiplier)).toInt()

            ingredients.forEach { ingredient ->
                val existingIngredients = recipeDao.getIngredientsByName(ingredient.name)
                val matchingIngredient = existingIngredients.find { it.isSameAs(ingredient) }
                val ingredientId = matchingIngredient?.id ?: recipeDao.insertIngredient(ingredient).toInt()
                recipeDao.insertRecipeIngredientCrossRef(RecipeIngredientCrossRef(recipeId, ingredientId))
            }

            tags.forEach { tag ->
                val existingTag = recipeDao.getTagByName(tag.name)
                val tagId = existingTag?.id ?: recipeDao.insertTag(tag).toInt()
                recipeDao.insertRecipeTagCrossRef(RecipeTagCrossRef(recipeId, tagId))
            }
        }
    }

    fun addTag(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingTags = allTags.value ?: emptyList()
            if (existingTags.none { it.name == tag.name }) {
                recipeDao.insertTag(tag)
            } else {
                showErrorDialog.postValue(true)
            }
        }
    }
    fun updateTag(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateTag(tag)
        }
    }

    fun updateRecipeWithIngredientsAndTags(recipe: Recipe, ingredients: List<Ingredient>, tags: List<Tag>) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateRecipe(recipe)
            recipeDao.deleteRecipeIngredients(recipe.id)
            recipeDao.deleteRecipeTags(recipe.id)

            ingredients.forEach { ingredient ->
                val existingIngredients = recipeDao.getIngredientsByName(ingredient.name)
                val matchingIngredient = existingIngredients.find { it.isSameAs(ingredient) }
                val ingredientId = matchingIngredient?.id ?: recipeDao.insertIngredient(ingredient).toInt()
                recipeDao.insertRecipeIngredientCrossRef(RecipeIngredientCrossRef(recipe.id, ingredientId))
            }

            tags.forEach { tag ->
                val existingTag = recipeDao.getTagByName(tag.name)
                val tagId = existingTag?.id ?: recipeDao.insertTag(tag).toInt()
                recipeDao.insertRecipeTagCrossRef(RecipeTagCrossRef(recipe.id, tagId))
            }
        }
    }


    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.deleteRecipeIngredients(recipe.id)
            recipeDao.deleteRecipeTags(recipe.id)
            recipeDao.deleteRecipe(recipe)
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingIngredients = allIngredients.value ?: emptyList()
            if (existingIngredients.none { it.isSameAs(ingredient) }) {
                recipeDao.insertIngredient(ingredient)
            } else {
                showErrorDialog.postValue(true)
            }
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.deleteIngredientFromAllRecipes(ingredient.id)
            recipeDao.deleteIngredient(ingredient)
        }
    }

    fun getRecipesForTag(tag: Tag): LiveData<List<RecipeWithDetails>>{
        return recipeDao.getRecipesForTag(tag.id)
    }

    fun deleteTagFromAllRecipes(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.deleteTagFromAllRecipes(tag.id)
            recipeDao.deleteTag(tag)
        }
    }
}
