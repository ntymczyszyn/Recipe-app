package com.example.recipeapp

import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ShowErrorDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Error") },
            text = { Text("Duplicate value - it already exists in the database.") },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("OK")
                }
            }
        )
    }
}