package com.example.recipeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.viewmodel.RecipeViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.data.entities.RecipeWithDetails


class MainActivity : ComponentActivity() {
    private lateinit var recipeViewModel: RecipeViewModel

    sealed class Screen(val route: String) {
        object RecipeList : Screen("recipeList")
        object AddRecipe : Screen("addRecipe")
        object IngriedientList: Screen("all_ingredients")
        object TagList: Screen("all_tags")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        setContent {
            AppNavigator(recipeViewModel)
        }
    }
}

@Composable
fun AppNavigator(recipeViewModel: RecipeViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MainActivity.Screen.RecipeList.route) {
        composable(MainActivity.Screen.RecipeList.route) {
            RecipeListScreen(navController, recipeViewModel)
        }
        composable(MainActivity.Screen.AddRecipe.route) {
            AddRecipeScreen(navController, recipeViewModel)
        }
        composable(MainActivity.Screen.IngriedientList.route) {
            AllIngredientsScreen(navController, recipeViewModel)
        }
        composable(MainActivity.Screen.TagList.route) {
            AllTagsScreen(navController, recipeViewModel)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Recipe List") },
            label = { Text("Recipe List") },
            selected = true,
            onClick = { navController.navigate(MainActivity.Screen.RecipeList.route) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Recipe") },
            label = { Text("Add Recipe") },
            selected = false,
            onClick = { navController.navigate(MainActivity.Screen.AddRecipe.route) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Ingriedient List") },
            label = { Text("Ingriedient List") },
            selected = true,
            onClick = { navController.navigate(MainActivity.Screen.IngriedientList.route) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Tag List") },
            label = { Text("Tag List") },
            selected = true,
            onClick = { navController.navigate(MainActivity.Screen.TagList.route) }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeListScreen(navController: NavHostController,recipeViewModel: RecipeViewModel) {
    val recipes by recipeViewModel.recipeList.observeAsState(emptyList())
    var selectedRecipe by remember { mutableStateOf<RecipeWithDetails?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var isDetailView by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        if (selectedRecipe != null && isEditing) {
            EditRecipeScreen(
                recipeWithDetails = selectedRecipe!!,
                onSave = {
                    //recipeViewModel.updateRecipe(it.recipe, it.ingredients, it.tags)
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
            LazyColumn {
                items(recipes) { recipeWithDetails ->
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