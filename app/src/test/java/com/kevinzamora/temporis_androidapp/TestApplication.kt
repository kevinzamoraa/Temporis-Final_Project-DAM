package com.kevinzamora.temporis_androidapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializamos Firebase con valores dummy para evitar el IllegalStateException
        if (FirebaseApp.getApps(this).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:1234567890:android:1234567890")
                .setApiKey("fake_api_key")
                .setProjectId("fake-project-id")
                .build()
            FirebaseApp.initializeApp(this, options)
        }
    }
}