package com.example.recipeapp

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeapp.viewmodel.RecipeViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipeapp.data.entities.*
import com.example.recipeapp.data.entities.Unit
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllIngredientsScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val ingredients by recipeViewModel.allIngredients.observeAsState(emptyList())
    var showEditIngredientDialog by remember { mutableStateOf(false) }
    var ingredientToEdit by remember { mutableStateOf(Ingredient()) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        LazyColumn {
            items(ingredients) { ingredient ->
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${ingredient.name} - ${ingredient.quantity} ${ingredient.unit}",
                        style = MaterialTheme.typography.body1
                    )
                    IconButton(onClick = {
                        // Wywołaj funkcję edycji składnika
                        showEditIngredientDialog = true
                        ingredientToEdit = ingredient
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Ingredient")
                    }
                    IconButton(onClick = { recipeViewModel.deleteIngredient(ingredient) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Ingredient")
                    }
                }
            }
        }
    }

    if (showEditIngredientDialog) {
        EditIngredientDialog(
            ingredient = ingredientToEdit,
            viewModel = recipeViewModel,
            onEdit = {
                showEditIngredientDialog = false
            },
            onDismiss = { showEditIngredientDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllTagsScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val tags by recipeViewModel.allTags.observeAsState(emptyList())
    var expanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf(Tag()) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    val recipesByTag by recipeViewModel.getRecipesForTag(selectedTag).observeAsState(emptyList())

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        Column {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedTag.name,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Tag") },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                        .clickable { expanded = !expanded }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tags.forEach { tag ->
                        DropdownMenuItem(
                            onClick = {
                                selectedTag = tag
                                expanded = false
                            }
                        ) {
                            Text(text = tag.name)
                        }
                    }
                }
            }
            if (selectedTag.name.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { showEditTagDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Tag")
                    }
                    IconButton(onClick = { recipeViewModel.deleteTagFromAllRecipes(selectedTag) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Tag")
                    }
                }
                LazyColumn {
                    items(recipesByTag) { recipe ->
                        Text(text = recipe.recipe.title)
                    }
                }
            }
        }
    }

    if (showEditTagDialog) {
        EditTagDialog(
            tag = selectedTag,
            viewModel = recipeViewModel,
            onEdit = {
                showEditTagDialog = false
            },
            onDismiss = { showEditTagDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditIngredientDialog(
    ingredient: Ingredient,
    viewModel: RecipeViewModel,
    onEdit: () -> kotlin.Unit,
    onDismiss: () -> kotlin.Unit
) {
    var ingredientName by remember { mutableStateOf(ingredient.name) }
    var ingredientQuantity by remember { mutableStateOf(ingredient.quantity.toString()) }
    var selectedUnit by remember { mutableStateOf(ingredient.unit) }
    var expanded by remember { mutableStateOf(false) }
    val units = Unit.values()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Ingredient") },
        text = {
            Column {
                OutlinedTextField(
                    value = ingredientName,
                    onValueChange = { ingredientName = it },
                    label = { Text("Ingredient Name") }
                )
                OutlinedTextField(
                    value = ingredientQuantity,
                    onValueChange = { ingredientQuantity = it },
                    label = { Text("Ingredient Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedUnit.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                            .clickable { expanded = !expanded }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        units.forEach { unit ->
                            androidx.compose.material.DropdownMenuItem(
                                onClick = {
                                    selectedUnit = unit
                                    expanded = false
                                }
                            ) {
                                Text(text = unit.name)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val editedIngredient = ingredient.copy(
                    name = ingredientName,
                    quantity = ingredientQuantity.toFloat(),
                    unit = selectedUnit
                )
                viewModel.updateIngredient(editedIngredient)
                onEdit()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditTagDialog(
    tag: Tag,
    viewModel: RecipeViewModel,
    onEdit: () -> kotlin.Unit,
    onDismiss: () -> kotlin.Unit
) {
    var tagName by remember { mutableStateOf(tag.name) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Tag") },
        text = {
            Column {
                OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Tag Name") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val editedTag = tag.copy(name = tagName)
                viewModel.updateTag(editedTag)
                onEdit()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}