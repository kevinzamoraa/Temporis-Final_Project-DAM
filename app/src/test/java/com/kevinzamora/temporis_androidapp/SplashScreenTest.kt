package com.kevinzamora.temporis_androidapp

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*
import org.robolectric.Shadows.shadowOf
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity

@RunWith(RobolectricTestRunner::class)
class SplashScreenTest {

    @Test
    fun onCreate_shouldStartLoginActivity() {
        // Construimos la Activity
        val controller = Robolectric.buildActivity(SplashScreen::class.java).setup()
        val activity = controller.get()

        // Obtenemos el intent que se lanzó
        val expectedIntent = shadowOf(activity).nextStartedActivity

        // Verificamos que el destino sea LoginActivity
        assertEquals(LoginActivity::class.java.name, expectedIntent.component?.className)
        assertTrue(activity.isFinishing)
    }
}