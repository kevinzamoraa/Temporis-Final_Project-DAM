package com.kevinzamora.temporis_androidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kevinzamora.temporis_androidapp.databinding.ActivityMainBinding
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity
import com.kevinzamora.temporis_androidapp.util.ThemeUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Cargar preferencias y aplicar configuraciones visuales ANTES de super.onCreate
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Aplicar ajustes de Accesibilidad (Idioma, Fuente, Negrita)
        ThemeUtils.applyAppSettings(this)

        // Aplicar Tema (Alto Contraste o Normal)
        if (sharedPref.getBoolean("high_contrast", false)) {
            setTheme(R.style.Theme_Temporis_HighContrast)
        } else {
            setTheme(R.style.Theme_TemporisAndroidApp)
        }

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 2. Inflar vista
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val navView: BottomNavigationView = binding.navView

        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController

            navView.setupWithNavController(navController)

            navView.setOnItemSelectedListener { item ->
                val currentUser = auth.currentUser
                when (item.itemId) {
                    R.id.navigation_home -> {
                        navController.navigate(R.id.navigation_home)
                        true
                    }
                    R.id.navigation_timers -> {
                        if (currentUser != null) navController.navigate(R.id.navigation_timers)
                        else mostrarAvisoYSirveLogin("ver tus temporizadores")
                        true
                    }
                    R.id.navigation_statistics -> {
                        if (currentUser != null) navController.navigate(R.id.navigation_statistics)
                        else mostrarAvisoYSirveLogin("ver tus estadísticas")
                        true
                    }
                    R.id.navigation_settings -> {
                        navController.navigate(R.id.navigation_settings)
                        true
                    }
                    else -> false
                }
            }
            // Eliminamos el super.onCreate y el setTheme que tenías aquí abajo
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al inicializar navegación: ${e.message}")
        }
    }

    // Función auxiliar para redirigir al login
    private fun mostrarAvisoYSirveLogin(motivo: String) {
        Toast.makeText(this, "Inicia sesión para $motivo", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()

        // Lógica de caducidad de sesión
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lastLogin = sharedPref.getLong("last_login_time", 0)
        val currentTime = System.currentTimeMillis()

        // Definimos caducidad (Ejemplo: 24 horas)
        val expirationMillis = 24 * 60 * 60 * 1000

        if (auth.currentUser != null && lastLogin != 0L) {
            if (currentTime - lastLogin > expirationMillis) {
                auth.signOut()
                sharedPref.edit().remove("last_login_time").apply()
                Toast.makeText(this, "Tu sesión ha caducado por seguridad", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}