package com.example.recipeapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
                val ingredientId = recipeDao.insertIngredient(ingredient).toInt()
                recipeDao.insertRecipeIngredientCrossRef(RecipeIngredientCrossRef(recipeId, ingredientId))
            }
            tags.forEach { tag ->
                val existingTag = recipeDao.getTagByName(tag.name)
                val tagId = if (existingTag != null) {
                    existingTag.id
                } else {
                    recipeDao.insertTag(tag).toInt()
                }
                recipeDao.insertRecipeTagCrossRef(RecipeTagCrossRef(recipeId, tagId))
            }
        }
    }

    // TAG
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
            val existingTagsForRecipe = recipeDao.getTagsForRecipe(recipeId)
            val tagId = recipeDao.getTagByName(tag.name)?.id ?: recipeDao.insertTag(tag).toInt()

            if (existingTagsForRecipe.any { it.name == tag.name }) {
                showErrorDialog.postValue(true)
            } else {
                recipeDao.insertRecipeTagCrossRef(RecipeTagCrossRef(recipeId, tagId))
            }
        }
    }


    // RECIPE
    fun updateRecipe(recipe: Recipe, ingredients: List<Ingredient>, tags: List<Tag>) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeDao.updateRecipe(recipe)
            recipeDao.deleteRecipeIngredients(recipe.id)
            recipeDao.deleteRecipeTags(recipe.id)
            ingredients.forEach { ingredient ->
                val ingredientId = recipeDao.insertIngredient(ingredient).toInt()
                recipeDao.insertRecipeIngredientCrossRef(RecipeIngredientCrossRef(recipe.id, ingredientId))
            }
            tags.forEach { tag ->
                val tagId = recipeDao.insertTag(tag).toInt()
                recipeDao.insertRecipeTagCrossRef(RecipeTagCrossRef(recipe.id, tagId))
            }
        }
    }

//    fun Ingredient.isSameAs(other: Ingredient): Boolean {
//        return this.name == other.name && this.quantity == other.quantity && this.unit == other.unit
//    }

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
            val existingIngredients = allIngredients.value ?: emptyList()
            if (existingIngredients.none { it.isSameAs(ingredient) }) {
                recipeDao.insertIngredient(ingredient)
            } else {
                showErrorDialog.postValue(true)
            }
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

    fun getRecipeWithDetails(recipeId: Int): LiveData<RecipeWithDetails> {
        return recipeDao.getRecipeWithDetailsById(recipeId)
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
