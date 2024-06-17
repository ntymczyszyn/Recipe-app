package com.example.recipeapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0 ,
    var name: String = "",
    var quantity: Float = 0f,
    var unit: Unit = Unit.G
) {
    fun isSameAs(other: Ingredient): Boolean {
        return this.name == other.name && this.quantity == other.quantity && this.unit == other.unit
    }
}