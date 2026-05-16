package com.kevinzamora.temporis_androidapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
class MainActivityTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<TestApplication>()
    }

    @Test
    fun onCreate_initializesCorrectly() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun onUserInteraction_resetsInactivityTimer() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        activity.onUserInteraction()

        assertFalse(activity.isFinishing)
    }

    @Test
    fun onStart_updatesLastLoginTime() {
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Guardamos un tiempo pasado manualmente (5 segundos atrás)
        val oldTime = System.currentTimeMillis() - 5000
        sharedPref.edit().putLong("last_login_time", oldTime).commit()

        // 2. Avanzamos el reloj simulado de Robolectric
        ShadowSystemClock.advanceBy(Duration.ofSeconds(2))

        // 3. Iniciamos la actividad
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()

        // 4. Forzamos a que Robolectric procese el guardado en segundo plano
        ShadowLooper.idleMainLooper()

        // 5. Verificación
        val updatedTime = sharedPref.getLong("last_login_time", 0)

        assertTrue(
            "El tiempo actualizado ($updatedTime) debería ser mayor al inicial ($oldTime)",
            updatedTime > oldTime
        )
    }

    @Test
    fun onDestroy_removesCallbacks() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        controller.destroy()

        assertTrue(activity.isFinishing)
    }
}