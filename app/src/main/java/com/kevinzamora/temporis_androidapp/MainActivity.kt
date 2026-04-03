package com.kevinzamora.temporis_androidapp

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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // 1. Inflar el layout y configurar Firebase
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // 2. Inicializar la vista del menú ANTES de cualquier uso
        val navView: BottomNavigationView = binding.navView

        // 3. Configurar el NavController de forma segura
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController

            // Vinculamos inicialmente
            navView.setupWithNavController(navController)

            // 4. Configurar el Listener manual para las restricciones de acceso
            navView.setOnItemSelectedListener { item ->
                val currentUser = auth.currentUser

                when (item.itemId) {
                    R.id.navigation_home -> {
                        navController.navigate(R.id.navigation_home)
                        true
                    }
                    R.id.navigation_timers -> {
                        if (currentUser != null) {
                            navController.navigate(R.id.navigation_timers)
                        } else {
                            mostrarAvisoYSirveLogin("ver tus temporizadores")
                        }
                        true
                    }
                    R.id.navigation_statistics -> {
                        if (currentUser != null) {
                            navController.navigate(R.id.navigation_statistics)
                        } else {
                            mostrarAvisoYSirveLogin("ver tus estadísticas")
                        }
                        true
                    }
                    R.id.navigation_settings -> {
                        navController.navigate(R.id.navigation_settings)
                        true
                    }
                    else -> false
                }
            }

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
}