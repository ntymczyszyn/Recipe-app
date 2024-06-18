package com.example.recipeapp

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
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
import com.example.recipeapp.data.entities.Tag
import com.example.recipeapp.viewmodel.RecipeViewModel


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AllTagsScreen(navController: NavHostController, recipeViewModel: RecipeViewModel) { // TODO popraw te przucisi na buttony zwykłe i chyba zrób link do itemów ?? jak w recipie view
    val tags by recipeViewModel.allTags.observeAsState(emptyList())
    var expanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf(Tag()) }
    var showEditTagDialog by remember { mutableStateOf(false) }
    val recipesByTag by recipeViewModel.getRecipesForTag(selectedTag).observeAsState(emptyList())

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
                            Icon(Icons.Default.Delete, contentDescription = "Delete Tag") // TODO usuń tag
                        }
                    }
                    LazyColumn (modifier = Modifier.height(200.dp)){
                        items(recipesByTag) { recipe ->
                            Text(text = recipe.recipe.title)
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