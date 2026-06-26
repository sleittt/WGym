package com.example.wgym

import android.app.Application
import com.example.data.local.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WGymApplication : Application() {
    @Inject
    lateinit var appInitializer: AppInitializer

    override fun onCreate() {
        super.onCreate()

        // Инициализация при старте приложения
        CoroutineScope(Dispatchers.IO).launch {
            appInitializer.initialize()
        }
    }
}