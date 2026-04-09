package com.kevinzamora.temporis_androidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

    private val logoutHandler = Handler(Looper.getMainLooper())
    private val INACTIVITY_TIMEOUT: Long = 30 * 60 * 1000
    private val WARNING_BEFORE: Long = 1 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        ThemeUtils.applyAppSettings(this)

        if (sharedPref.getBoolean("high_contrast", false)) {
            setTheme(R.style.Theme_Temporis_HighContrast)
        } else {
            setTheme(R.style.Theme_TemporisAndroidApp)
        }

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val navView: BottomNavigationView = binding.navView

        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController

            // IMPORTANTE: Si usamos setOnItemSelectedListener manual,
            // no llamamos a setupWithNavController para evitar conflictos.

            navView.setOnItemSelectedListener { item ->
                val currentUser = auth.currentUser

                when (item.itemId) {
                    R.id.navigation_home -> {
                        navController.navigate(R.id.navigation_home)
                        true
                    }
                    R.id.navigation_settings -> {
                        navController.navigate(R.id.navigation_settings)
                        true
                    }
                    R.id.navigation_timers -> {
                        if (currentUser != null) {
                            navController.navigate(R.id.navigation_timers)
                            true
                        } else {
                            mostrarAvisoYSirveLogin("ver tus temporizadores")
                            false
                        }
                    }
                    R.id.navigation_statistics -> {
                        if (currentUser != null) {
                            navController.navigate(R.id.navigation_statistics)
                            true
                        } else {
                            mostrarAvisoYSirveLogin("ver tus estadísticas")
                            false
                        }
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al inicializar navegación: ${e.message}")
        }

        if (auth.currentUser != null) {
            resetInactivityTimer()
        }

        val openTimers = intent.getBooleanExtra("OPEN_TIMERS", false)
        if (openTimers) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController

            // Cambiamos visualmente el item seleccionado en el BottomNav
            binding.navView.selectedItemId = R.id.navigation_timers
            // Navegamos al fragment
            navController.navigate(R.id.navigation_timers)
        }
    }

    private fun mostrarAvisoYSirveLogin(motivo: String) {
        Toast.makeText(this, "Inicia sesión para $motivo", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        // No cerramos MainActivity para que el usuario pueda volver atrás
        startActivity(intent)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (auth.currentUser != null) {
            resetInactivityTimer()
        }
    }

    private fun resetInactivityTimer() {
        logoutHandler.removeCallbacksAndMessages(null)
        logoutHandler.postDelayed({
            if (!isFinishing && auth.currentUser != null) {
                showLogoutWarningDialog()
            }
        }, INACTIVITY_TIMEOUT - WARNING_BEFORE)
    }

    private fun showLogoutWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle("Aviso de sesión")
            .setMessage("Tu sesión va a expirar por inactividad en 1 minuto. ¿Quieres seguir conectado?")
            .setCancelable(false)
            .setPositiveButton("Sí, continuar") { _, _ ->
                resetInactivityTimer()
            }
            .setNegativeButton("No, salir") { _, _ ->
                cerrarSesionForzada()
            }
            .show()

        logoutHandler.postDelayed({
            // Verificamos de nuevo si el usuario sigue ahí antes de cerrar
            if (auth.currentUser != null) cerrarSesionForzada()
        }, WARNING_BEFORE)
    }

    private fun cerrarSesionForzada() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val lastLogin = sharedPref.getLong("last_login_time", 0)
        val currentTime = System.currentTimeMillis()

        if (auth.currentUser != null) {
            // Si el lastLogin es 0, significa que es la primera vez que entra tras limpiar datos
            if (lastLogin != 0L && (currentTime - lastLogin > 1 * 60 * 60 * 1000)) {
                cerrarSesionForzada()
                Toast.makeText(this, "Sesión caducada por inactividad", Toast.LENGTH_LONG).show()
            } else {
                // MUY IMPORTANTE: Actualizamos siempre al entrar para que la sesión esté viva
                sharedPref.edit().putLong("last_login_time", currentTime).apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutHandler.removeCallbacksAndMessages(null)
        // Cerramos sesión cuando la actividad principal se destruye (app cerrada por el usuario)
        // FirebaseAuth.getInstance().signOut()
    }
}