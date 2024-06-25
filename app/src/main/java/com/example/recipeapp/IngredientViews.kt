package com.example.recipeapp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Button
import androidx.compose.material.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipeapp.data.entities.*
import com.example.recipeapp.data.entities.Unit
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllIngredientsScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val ingredients by recipeViewModel.allIngredients.observeAsState(emptyList())
    var showEditIngredientDialog by remember { mutableStateOf(false) }
    var showDeleteIngredientDialog by remember { mutableStateOf(false) }
    var ingredientToModify by remember { mutableStateOf(Ingredient()) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        LazyColumn(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 70.dp
            )
        ) {
            items(ingredients) { ingredient ->
                var showButtons by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .combinedClickable(
                            onClick = { },
                            onLongClick = { showButtons = !showButtons }
                        ),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${ingredient.name} - ${ingredient.quantity} ${if (ingredient.unit == Unit.NONE) "" else " ${ingredient.unit}"}",
                            style = MaterialTheme.typography.body1
                        )
                        AnimatedVisibility(visible = showButtons) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    showEditIngredientDialog = true
                                    ingredientToModify = ingredient
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Ingredient")
                                }
                                IconButton(onClick = {
                                    showDeleteIngredientDialog = true
                                    ingredientToModify = ingredient
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Ingredient")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditIngredientDialog) {
        EditIngredientDialog(
            ingredient = ingredientToModify,
            viewModel = recipeViewModel,
            onEdit = {
                showEditIngredientDialog = false
            },
            onDismiss = { showEditIngredientDialog = false }
        )
    }
    if (showDeleteIngredientDialog) {
        DeleteIngredientDialog(
            ingredient = ingredientToModify,
            viewModel = recipeViewModel,
            onDismiss = { showDeleteIngredientDialog = false }
        )
    }
}

@Composable
fun DeleteIngredientDialog(
    ingredient: Ingredient,
    viewModel: RecipeViewModel,
    onDismiss: () -> kotlin.Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Delete Ingredient") },
        text = { Text("This action will permanent delete this ingredient form all recipes.") },
        confirmButton = {
            Button(onClick = {
                viewModel.deleteIngredient(ingredient)
                onDismiss()
            }) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
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

