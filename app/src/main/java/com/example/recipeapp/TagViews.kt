package com.example.recipeapp

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeapp.data.entities.RecipeWithDetails
import com.example.recipeapp.data.entities.Tag
import com.example.recipeapp.viewmodel.RecipeViewModel


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllTagsScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val tags by recipeViewModel.allTags.observeAsState(emptyList())
    var expanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf(Tag()) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showDeleteTagDialog by remember { mutableStateOf(false) }
    val recipesByTag by recipeViewModel.getRecipesForTag(selectedTag).observeAsState(emptyList())
    var selectedRecipe by remember { mutableStateOf<RecipeWithDetails?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var isDetailView by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        LazyColumn (modifier = Modifier
            .padding(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 70.dp)) {
            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedTag.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
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
                        tags.forEach { tag ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedTag = tag
                                    expanded = false
                                    selectedRecipe = null
                                    isEditing = false
                                    isDetailView = false
                                }
                            ) {
                                Text(text = tag.name)
                            }
                        }
                    }
                }
                if (selectedTag.name.isNotEmpty()) {
                    if (selectedRecipe != null && isEditing) {
                        EditRecipeScreen(
                            recipeWithDetails = selectedRecipe!!,
                            onSave = {
                                selectedRecipe = null
                                isEditing = false
                            },
                            onCancel = {
                                selectedRecipe = null
                                isEditing = false
                            },
                            recipeViewModel = recipeViewModel
                        )
                    } else if (selectedRecipe != null && isDetailView) {
                        RecipeDetailScreen(
                            recipeWithDetails = selectedRecipe!!,
                            onBack = {
                                selectedRecipe = null
                                isDetailView = false
                            }
                        )
                    } else {
                        Row(
                            modifier = Modifier.padding(16.dp)

                        ) {
                            Button(onClick = { showEditTagDialog = true }) {
                                Text("Edit Tag")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { showDeleteTagDialog = true }){
                                Text("Delete Tag")
                            }
                        }
                        LazyColumn(modifier =  Modifier.height(200.dp))  {
                            items(recipesByTag) { recipeWithDetails ->
                                RecipeItem(
                                    recipeWithDetails,
                                    onClick = {
                                        selectedRecipe = recipeWithDetails
                                        isDetailView = true
                                    },
                                    onEdit = {
                                        selectedRecipe = recipeWithDetails
                                        isEditing = true
                                    },
                                    onDelete = {
                                        recipeViewModel.deleteRecipe(recipeWithDetails.recipe)
                                    }
                                )
                            }
                        }
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
    if (showDeleteTagDialog) {
        DeleteTagDialog(
            tag = selectedTag,
            viewModel = recipeViewModel,
            onDismiss = { showDeleteTagDialog = false }
        )
    }

}
@Composable
fun DeleteTagDialog(
    tag: Tag,
    viewModel: RecipeViewModel,
    onDismiss: () -> kotlin.Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Delete Tag") },
        text = {
            Column {
                Text("Are you sure you want to delete this tag?")
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.deleteTagFromAllRecipes(tag)
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