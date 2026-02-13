package com.example.hsklearner

import android.app.Application
import com.example.hsklearner.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HskApplication : Application() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate() {
        super.onCreate()
        databaseInitializer.initialize()
    }
}
