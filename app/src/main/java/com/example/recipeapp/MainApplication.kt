package com.example.recipeapp

import android.app.Application
import androidx.room.Room
import com.example.recipeapp.data.Database

class MainApplication : Application() {

    companion object {
        lateinit var database: Database
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            Database.NAME
        ).build()
    }
}
