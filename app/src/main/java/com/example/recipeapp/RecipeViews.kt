package com.example.recipeapp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.navigation.NavHostController
import com.example.recipeapp.data.entities.Ingredient
import com.example.recipeapp.data.entities.RecipeWithDetails
import com.example.recipeapp.data.entities.Tag
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.data.entities.Unit
import com.kanyidev.searchable_dropdown.SearchableExpandedDropDownMenu
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

@Composable
fun RecipeItem(
    recipeWithDetails: RecipeWithDetails,
    onClick: () -> kotlin.Unit,
    onEdit: () -> kotlin.Unit,
    onDelete: () -> kotlin.Unit
) {
    var showButtons by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
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
            AnimatedVisibility(visible = showButtons) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { onEdit() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
    LazyColumn(modifier = Modifier
        .padding(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 70.dp
        )
        .height(750.dp)) { item {
        Text(recipeWithDetails.recipe.title, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = recipeWithDetails.recipe.instructions,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Ingredients", fontSize = 17.sp)
        Spacer(modifier = Modifier.height(8.dp))
        recipeWithDetails.ingredients.forEach { ingredient ->
            Text(
                text = "${ingredient.name} - ${ingredient.quantity * quantityMultiplier} ${if (ingredient.unit == Unit.NONE) "" else " ${ingredient.unit}"}",
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Tags", fontSize = 17.sp)
        Spacer(modifier = Modifier.height(8.dp))
        recipeWithDetails.tags.forEach { tag ->
            Text(
                text = tag.name,
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
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
        Spacer(modifier = Modifier.height(15.dp))
        Button(onClick = { onBack() }) {
            Text("Back to list")
        }
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
        content = {
                LazyColumn(modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 70.dp
                )) {
                    item {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") }
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Instructions") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ingredients", fontSize = 17.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        ingredients.forEachIndexed { index, ingredient ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                elevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text( "${ingredient.name} - ${ingredient.quantity} ${if (ingredient.unit == Unit.NONE) "" else " ${ingredient.unit}"}")
                                    IconButton(
                                        onClick = {
                                            ingredients = ingredients.toMutableList().also { it.removeAt(index) }
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove Ingredient")
                                    }
                                }
                            }
                            Spacer(modifier =   Modifier.height(8.dp))
                        }
                        Button(onClick = { showAddIngredientDialog = true }) {
                            Text("Add new ingredient")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
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
                            placeholder = "Select existing ingredient",
                            dropdownItem = { name ->
                                Text(name)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tags", fontSize = 17.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        tags.forEachIndexed { index, tag ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                elevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(tag.name)
                                    IconButton(
                                        onClick = {
                                            tags = tags.toMutableList().also { it.removeAt(index) }
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove Tag")
                                    }
                                }
                            }
                            Spacer(modifier =   Modifier.height(8.dp))
                        }
                        Button(onClick = { showAddTagDialog = true }) {
                            Text("Add Tag")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        SearchableExpandedDropDownMenu(
                            listOfItems = allTags.map { it.name },
                            modifier = Modifier
                                .fillMaxWidth(),
                            onDropDownItemSelected = { item ->
                                val selectedTag = allTags.firstOrNull { it.name == item }
                                if (selectedTag != null) {
                                    if (tags.none { it.name == selectedTag.name }) {
                                        tags = tags + selectedTag
                                    }
                                }
                            },
                            placeholder = "Select existing tag",
                            dropdownItem = { name ->
                                Text(name)
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
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
        },
        bottomBar = { BottomBar(navController) }
    )

    if (showAddIngredientDialog) {
        AddIngredientDialog(
            onAdd ={ ingredient ->
                if (ingredients.none { it.isSameAs(ingredient) } && allIngredients.none { it.isSameAs(ingredient) }) {
                    ingredients = ingredients + ingredient
                }
                showAddIngredientDialog = false
            },
            onDismiss = { showAddIngredientDialog = false },
            viewModel = recipeViewModel
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

    LazyColumn(modifier = Modifier
        .padding(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 70.dp
        )
        .height(750.dp)) { item {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = instructions,
            onValueChange = { instructions = it },
            label = { Text("Instructions") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Ingredients", fontSize = 17.sp)
        Spacer(modifier = Modifier.height(8.dp))
        ingredients.forEachIndexed { index, ingredient ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text( "${ingredient.name} - ${ingredient.quantity} ${if (ingredient.unit == Unit.NONE) "" else " ${ingredient.unit}"}")
                    IconButton(
                        onClick = {
                            ingredients = ingredients.toMutableList().also { it.removeAt(index) }
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Ingredient")
                    }
                }
            }
            Spacer(modifier =   Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { showAddIngredientDialog = true }) {
            Text("Add new ingredient")
        }
        Spacer(modifier = Modifier.height(8.dp))
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
            placeholder = "Select existing ingredient",
            dropdownItem = { name ->
                Text(name)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tags", fontSize = 17.sp)
        Spacer(modifier = Modifier.height(8.dp))
        tags.forEachIndexed { index, tag ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tag.name)
                    IconButton(
                        onClick = {
                            tags = tags.toMutableList().also { it.removeAt(index) }
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Tag")
                    }
                }
            }
            Spacer(modifier =   Modifier.height(8.dp))
        }
        Button(onClick = { showAddTagDialog = true }) {
            Text("Add Tag")
        }
        Spacer(modifier =   Modifier.height(4.dp))
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
            placeholder = "Select existing tag",
            dropdownItem = { name ->
                Text(name)
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val updatedRecipe =
                    recipeWithDetails.recipe.copy(title = title, instructions = instructions)
                recipeViewModel.updateRecipeWithIngredientsAndTags(updatedRecipe, ingredients, tags)
                onSave()
            }) {
                Text("Save")
            }

            Button(onClick = { onCancel() }) {
                Text("Cancel")
            }
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
            viewModel = recipeViewModel
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
    onDismiss: () -> kotlin.Unit,
    viewModel: RecipeViewModel
) {
    var ingredientName by remember { mutableStateOf("name") }
    var ingredientQuantity by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf(Unit.G) }
    var expanded by remember { mutableStateOf(false) }
    val units = Unit.values()

    AlertDialog(
        onDismissRequest = { onDismiss() },
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
                            modifier = Modifier
                                .fillMaxWidth()
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
                viewModel.addIngredient(newIngredient)
                onAdd(newIngredient)
            }) {
                Text("Add ingredient")
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
    var tagName by remember { mutableStateOf("name") }
    val showErrorDialog by viewModel.showErrorDialog.observeAsState(false)

    AlertDialog(
        onDismissRequest = { onDismiss() },
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
                Text("Add tag")
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
