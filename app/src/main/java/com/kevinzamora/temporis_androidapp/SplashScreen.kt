package com.kevinzamora.temporis_androidapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalamos la Splash Screen oficial antes de super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Redirigir directamente al Login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}