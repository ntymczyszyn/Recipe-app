package com.example.recipeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp) // Zwiększamy wysokość paska nawigacyjnego
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Recipe List") },
            label = { Text("Recipe List", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            selected = navController.currentDestination?.route == MainActivity.Screen.RecipeList.route,
            onClick = { navController.navigate(MainActivity.Screen.RecipeList.route) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Recipe") },
            label = { Text("Add Recipe", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            selected = navController.currentDestination?.route == MainActivity.Screen.AddRecipe.route,
            onClick = { navController.navigate(MainActivity.Screen.AddRecipe.route) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Ingredient List") },
            label = { Text("Ingredient List", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            selected = navController.currentDestination?.route == MainActivity.Screen.IngriedientList.route,
            onClick = { navController.navigate(MainActivity.Screen.IngriedientList.route) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Tag List") },
            label = { Text("Tag List", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            selected = navController.currentDestination?.route == MainActivity.Screen.TagList.route,
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
            LazyColumn(modifier = Modifier.padding(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 70.dp
            ))  {
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
                            recipeViewModel.deleteRecipe(recipeWithDetails.recipe) // TODO : popraw na usuwanie z details recipie
                        }
                    )
                }
            }
        }
    }
}