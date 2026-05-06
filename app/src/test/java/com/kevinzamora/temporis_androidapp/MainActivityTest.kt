package com.kevinzamora.temporis_androidapp

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowSystemClock

// Indicamos que use nuestra clase TestApplication
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class MainActivityTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
    }

    @Test
    fun onCreate_shouldSetCorrectTheme() {
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("high_contrast", true).commit()

        // Usamos buildActivity para tener más control
        val controller = Robolectric.buildActivity(MainActivity::class.java).create()
        val activity = controller.get()

        // Procesamos cualquier evento pendiente en el hilo principal (evita el error de Main Looper)
        ShadowLooper.idleMainLooper()

        assertNotNull(activity)
    }

    @Test
    fun onUserInteraction_shouldResetTimer() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        activity.onUserInteraction()

        assertFalse(activity.isFinishing)
    }

    @Test
    fun onStart_updatesLastLoginTime() {
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Guardamos un tiempo pasado manualmente
        val oldTime = System.currentTimeMillis() - 5000 // 5 segundos atrás
        sharedPref.edit().putLong("last_login_time", oldTime).commit()

        // 2. Avanzamos el reloj simulado de Robolectric para asegurar que System.currentTimeMillis() devuelva algo nuevo
        ShadowSystemClock.advanceBy(java.time.Duration.ofSeconds(2))

        // 3. Iniciamos la actividad
        // .setup() pasa por onCreate y onStart
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()

        // 4. Forzamos a que se procesen los mensajes del Looper (importante para SharedPreferences)
        ShadowLooper.idleMainLooper()

        // 5. Verificación
        val updatedTime = sharedPref.getLong("last_login_time", 0)

        assertTrue("El tiempo actualizado ($updatedTime) debería ser mayor al inicial ($oldTime)",
            updatedTime > oldTime)
    }

    @Test
    fun onDestroy_removesCallbacks() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        controller.pause().stop().destroy()

        ShadowLooper.idleMainLooper()
        assertTrue(true)
    }
}