package com.example.recipeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.viewmodel.RecipeViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.example.recipeapp.data.entities.Ingredient
import com.example.recipeapp.data.entities.Unit

class MainActivity : ComponentActivity() {
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        setContent {
            recipeListScreen(recipeViewModel)
        }
    }
}

@Composable
fun AddRecipeScreen(recipeViewModel: RecipeViewModel) {
    var title by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var ingredientName by remember { mutableStateOf("") }
    var ingredientQuantity by remember { mutableStateOf("") }

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
        Button(onClick = {
            val ingredient = Ingredient(name = ingredientName, quantity = ingredientQuantity.toFloat(), unit = Unit.GRAM)
            recipeViewModel.addRecipeWithIngredients(title, instructions, 1.0f, listOf(ingredient))
        }) {
            Text("Add Recipe")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun recipeListScreen(recipeViewModel: RecipeViewModel) {
    val recipes by recipeViewModel.recipeList.observeAsState(emptyList())

    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Recipe List") },
                    label = { Text("Recipe List") },
                    selected = true,
                    onClick = {}
                )
            }
        },
    ) {
        LazyColumn {
            items(recipes) { recipeWithDetails ->
                Text(recipeWithDetails.recipe.title)
                Text(recipeWithDetails.recipe.instructions)
            }
        }
    }
}

//
//@Composable
//fun RecipeApp(recipeViewModel: RecipeViewModel) {
//    val recipes by recipeViewModel.recipeList.observeAsState(emptyList())
//    var title by remember { mutableStateOf("") }
//    var instructions by remember { mutableStateOf("") }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        OutlinedTextField(
//            value = title,
//            onValueChange = { title = it },
//            label = { Text("Title") }
//        )
//        OutlinedTextField(
//            value = instructions,
//            onValueChange = { instructions = it },
//            label = { Text("Instructions") }
//        )
//        Button(onClick = {
//            recipeViewModel.addRecipeWithIngredients(title, instructions, 1.0f, emptyList())
//        }) {
//            Text("Add Recipe")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        LazyColumn {
//            items(recipes) { recipeWithDetails ->
//                Text(recipeWithDetails.recipe.title)
//                Text(recipeWithDetails.recipe.instructions)
//            }
//        }
//    }

//}