package com.example.recipeapp

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.recipeapp.data.entities.Ingredient
import com.example.recipeapp.data.entities.RecipeWithDetails
import com.example.recipeapp.data.entities.Tag
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.data.entities.Unit
import com.kanyidev.searchable_dropdown.SearchableExpandedDropDownMenu
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RecipeItem(recipeWithDetails: RecipeWithDetails, onClick: () -> kotlin.Unit, onEdit: () -> kotlin.Unit, onDelete: () -> kotlin.Unit){
    var showButtons by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { showButtons = !showButtons }
                )
            },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = recipeWithDetails.recipe.title,
                style = MaterialTheme.typography.h6
            )
            if (showButtons) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onEdit() }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDeleteDialog = true }) {
                    Text("Delete")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete this recipe?") },
            confirmButton = {
                Button(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecipeDetailScreen(recipeWithDetails: RecipeWithDetails, onBack: () -> kotlin.Unit) {
    var quantityMultiplier by remember { mutableStateOf(1f) }
    var expanded by remember { mutableStateOf(false) }
    val multipliers = listOf(0.5f,1f,1.5f, 2f, 2.5f, 3f, 3.5f, 4f, 4.5f, 5f)
    Column(modifier = Modifier.padding(16.dp)) {
        Text(recipeWithDetails.recipe.title)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = recipeWithDetails.recipe.instructions,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ingredients:",
            style = MaterialTheme.typography.h6
        )
        recipeWithDetails.ingredients.forEach { ingredient ->
            Text(
                text = "${ingredient.name} - ${ingredient.quantity * quantityMultiplier} ${ingredient.unit}",
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tags:",
            style = MaterialTheme.typography.h6
        )
        recipeWithDetails.tags.forEach { tag ->
            Text(
                text = tag.name,
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = quantityMultiplier.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Quantity Multiplier") },
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
                multipliers.forEach { multiplier ->
                    DropdownMenuItem(
                        onClick = {
                            quantityMultiplier = multiplier
                            expanded = false
                        }
                    ) {
                        Text(text = multiplier.toString())
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onBack() }) {
            Text("Back to list")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddRecipeScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    var title by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf(listOf<Ingredient>()) }
    var tags by remember { mutableStateOf(listOf<Tag>()) }
    var showAddIngredientDialog by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }

    val allIngredients by recipeViewModel.allIngredients.observeAsState(emptyList())
    val allTags by recipeViewModel.allTags.observeAsState(emptyList())

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") }
            )

            Text("Ingredients:")
            ingredients.forEachIndexed { index, ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${ingredient.name} - ${ingredient.quantity} ${ingredient.unit}")
                    IconButton(onClick = {
                        ingredients = ingredients.toMutableList().also { it.removeAt(index) }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Ingredient")
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { showAddIngredientDialog = true }) {
                    Text("Add new ingredient")
                }

                SearchableExpandedDropDownMenu(
                    listOfItems = allIngredients.map { "${it.name} - ${it.quantity} ${it.unit}" },
                    modifier = Modifier.fillMaxWidth(),
                    onDropDownItemSelected = { item ->
                        val selectedIngredient = allIngredients.firstOrNull {
                            "${it.name} - ${it.quantity} ${it.unit}" == item
                        }
                        if (selectedIngredient != null) {
                            if (ingredients.none { it.isSameAs(selectedIngredient) }) {
                                ingredients = ingredients + selectedIngredient
                            }
                        }
                    },
                    enable = true,
                    placeholder = "Select existing ingredient",
                    parentTextFieldCornerRadius = 12.dp,
                    colors = TextFieldDefaults.outlinedTextFieldColors(),
                    dropdownItem = { name ->
                        Text(name)
                    }
                )
            }

            Text("Tags:")
            tags.forEachIndexed { index, tag ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(tag.name)
                    IconButton(onClick = {
                        tags = tags.toMutableList().also { it.removeAt(index) }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Tag")
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { showAddTagDialog = true }) {
                    Text("Add Tag")
                }

                SearchableExpandedDropDownMenu(
                    listOfItems = allTags.map { it.name },
                    modifier = Modifier.fillMaxWidth(),
                    onDropDownItemSelected = { item ->
                        val selectedTag = allTags.firstOrNull { it.name == item }
                        if (selectedTag != null) {
                            if (tags.none { it.name == selectedTag.name }) {
                                tags = tags + selectedTag
                            }
                        }
                    },
                    enable = true,
                    placeholder = "Select existing tag",
                    parentTextFieldCornerRadius = 12.dp,
                    colors = TextFieldDefaults.outlinedTextFieldColors(),
                    dropdownItem = { name ->
                        Text(name)
                    }
                )
            }

            Button(onClick = {
                recipeViewModel.addRecipeWithIngredientsAndTags(
                    title,
                    instructions,
                    1.0f,
                    ingredients,
                    tags
                )
                navController.navigate(MainActivity.Screen.RecipeList.route)
            }) {
                Text("Add Recipe")
            }
        }
    }

    if (showAddIngredientDialog) {
        AddIngredientDialog(
            onAdd ={ ingredient ->
                if (ingredients.none { it.isSameAs(ingredient) } && allIngredients.none { it.isSameAs(ingredient) }) {
                    ingredients = ingredients + ingredient
                }
                showAddIngredientDialog = false
            },
            onDismiss = { showAddIngredientDialog = false },
        )
    }

    if (showAddTagDialog) {
        AddTagDialog(
            onAdd = { tag ->
                if (tags.none { it.name == tag.name } && allTags.none { it.name == tag.name }) {
                    tags = tags + tag
                }
                showAddTagDialog = false
            },
            onDismiss = { showAddTagDialog = false },
            viewModel = recipeViewModel
        )
    }
}


@Composable
fun EditRecipeScreen(
    recipeWithDetails: RecipeWithDetails,
    onSave: () -> kotlin.Unit,
    onCancel: () -> kotlin.Unit,
    recipeViewModel: RecipeViewModel
) {
    var title by remember { mutableStateOf(recipeWithDetails.recipe.title) }
    var instructions by remember { mutableStateOf(recipeWithDetails.recipe.instructions) }
    var ingredients by remember { mutableStateOf(recipeWithDetails.ingredients) }
    var tags by remember { mutableStateOf(recipeWithDetails.tags) }
    var showAddIngredientDialog by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }

    val allIngredients by recipeViewModel.allIngredients.observeAsState(emptyList())
    val allTags by recipeViewModel.allTags.observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = instructions,
            onValueChange = { instructions = it },
            label = { Text("Instructions") }
        )

        Text("Ingredients:")
        ingredients.forEachIndexed { index, ingredient ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${ingredient.name} - ${ingredient.quantity} ${ingredient.unit}")
                IconButton(onClick = {
                    ingredients = ingredients.toMutableList().also { it.removeAt(index) }
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove Ingredient")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { showAddIngredientDialog = true }) {
                Text("Add new ingredient")
            }

            SearchableExpandedDropDownMenu(
                listOfItems = allIngredients.map { "${it.name} - ${it.quantity} ${it.unit}" },
                modifier = Modifier.fillMaxWidth(),
                onDropDownItemSelected = { item ->
                    val selectedIngredient = allIngredients.firstOrNull {
                        "${it.name} - ${it.quantity} ${it.unit}" == item
                    }
                    if (selectedIngredient != null) {
                        if (ingredients.none { it.isSameAs( selectedIngredient) }) {
                            ingredients = ingredients +  selectedIngredient
                        }
                    }
                },
                enable = true,
                placeholder = "Select existing ingredient",
                parentTextFieldCornerRadius = 12.dp,
                colors = TextFieldDefaults.outlinedTextFieldColors(),
                dropdownItem = { name ->
                    Text(name)
                }
            )
        }

        Text("Tags:")
        tags.forEachIndexed { index, tag ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(tag.name)
                IconButton(onClick = {
                    tags = tags.toMutableList().also { it.removeAt(index) }
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove Tag")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { showAddTagDialog = true }) {
                Text("Add Tag")
            }

            SearchableExpandedDropDownMenu(
                listOfItems = allTags.map { it.name },
                modifier = Modifier.fillMaxWidth(),
                onDropDownItemSelected = { item ->
                    val selectedTag = allTags.firstOrNull { it.name == item }
                    if (selectedTag != null) {
                        if (tags.none { it.name == selectedTag.name }) {
                            tags = tags + selectedTag
                        }
                    }
                },
                enable = true,
                placeholder = "Select existing tag",
                parentTextFieldCornerRadius = 12.dp,
                colors = TextFieldDefaults.outlinedTextFieldColors(),
                dropdownItem = { name ->
                    Text(name)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val updatedRecipe = recipeWithDetails.recipe.copy(title = title, instructions = instructions)
                recipeViewModel.updateRecipe(updatedRecipe, ingredients, tags)
                onSave()//RecipeWithDetails(updatedRecipe, ingredients, tags))
            }) {
                Text("Save")
            }

            Button(onClick = { onCancel() }) {
                Text("Cancel")
            }
        }
    }

    if (showAddIngredientDialog) {
        AddIngredientDialog(
            onAdd ={ ingredient ->
                if (ingredients.none { it.isSameAs(ingredient) } && allIngredients.none { it.isSameAs(ingredient) }) {
                    ingredients = ingredients + ingredient
                }
                showAddIngredientDialog = false
            },
            onDismiss = { showAddIngredientDialog = false },
        )
    }

    if (showAddTagDialog) {
        AddTagDialog(
            onAdd = { tag ->
                    if (tags.none { it.name == tag.name } && allTags.none { it.name == tag.name }) {
                        tags = tags + tag
                    }
                showAddTagDialog = false
            },
            onDismiss = { showAddTagDialog = false },
            viewModel = recipeViewModel
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddIngredientDialog(
    onAdd: (Ingredient) -> kotlin.Unit,
    onDismiss: () -> kotlin.Unit
) {
    var ingredientName by remember { mutableStateOf("") }
    var ingredientQuantity by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf(Unit.G) }
    var expanded by remember { mutableStateOf(false) }
    val units = Unit.values()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Ingredient") },
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
                    label = { Text("Ingredient Quantity") }
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
                            DropdownMenuItem(
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
                val newIngredient = Ingredient(
                    name = ingredientName,
                    quantity = ingredientQuantity.toFloat(),
                    unit = selectedUnit
                )
                onAdd(newIngredient)
            }) {
                Text("Add")
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
fun AddTagDialog(onAdd: (Tag) -> kotlin.Unit, onDismiss: () -> kotlin.Unit, viewModel: RecipeViewModel) {
    var tagName by remember { mutableStateOf("") }
    val showErrorDialog by viewModel.showErrorDialog.observeAsState(false)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Tag") },
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
                val newTag = Tag(name = tagName)
                viewModel.addTag(newTag)
                onAdd(newTag)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
    if (showErrorDialog) {
        ShowErrorDialog(showDialog = true, onDismiss = { viewModel.showErrorDialog.value = false })
    }
}
