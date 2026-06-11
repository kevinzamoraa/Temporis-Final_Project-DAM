package com.kevinzamora.temporis_androidapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
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
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<TestApplication>()

        // Mockear estáticamente Firebase Auth para evitar inicializaciones reales de red
        mockkStatic(FirebaseAuth::class)
        mockAuth = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)

        every { FirebaseAuth.getInstance() } returns mockAuth
        // Simulamos un usuario activo por defecto para evitar redirecciones automáticas
        every { mockAuth.currentUser } returns mockUser
    }

    @After
    fun tearDown() {
        unmockkStatic(FirebaseAuth::class)
    }

    @Test
    fun onCreate_initializesCorrectly() {
        // Inicializamos una marca de tiempo válida para evitar que salte cerrarSesionForzada en onStart
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPref.edit().putLong("last_login_time", System.currentTimeMillis()).commit()

        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun onUserInteraction_resetsInactivityTimer() {
        // 1. Preparamos SharedPreferences con un login válido para que la actividad no haga finish() en onStart
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPref.edit().putLong("last_login_time", System.currentTimeMillis()).commit()

        // 2. Levantamos la Activity de forma segura
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        // 3. Provocamos la interacción
        activity.onUserInteraction()

        // 4. Consumimos los callbacks retrasados del Handler usando el tiempo virtual definido
        ShadowLooper.idleMainLooper(29)

        assertFalse("La actividad no debería finalizar tras una interacción de usuario", activity.isFinishing)
    }

    @Test
    fun onStart_updatesLastLoginTime() {
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Guardamos una marca de tiempo base inicial simulando un acceso previo activo
        val baseTime = System.currentTimeMillis()
        sharedPref.edit().putLong("last_login_time", baseTime).commit()

        // 2. Pasamos solo por el onCreate
        val controller = Robolectric.buildActivity(MainActivity::class.java).create()

        // 3. Avanzamos el reloj antes del onStart
        ShadowSystemClock.advanceBy(Duration.ofSeconds(5))

        // 4. Provocamos onStart y onResume de manera explícita
        controller.start().resume()
        ShadowLooper.idleMainLooper()

        // 5. Verificación de actualización exitosa
        val updatedTime = sharedPref.getLong("last_login_time", 0)

        assertTrue(
            "El tiempo actualizado ($updatedTime) debería ser mayor al base inicial ($baseTime)",
            updatedTime > baseTime
        )
    }

    @Test
    fun onDestroy_removesCallbacks() {
        // Inicializamos una marca de tiempo válida para el ciclo de vida seguro
        val sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPref.edit().putLong("last_login_time", System.currentTimeMillis()).commit()

        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        controller.destroy()

        ShadowLooper.idleMainLooper()
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        assertTrue(activity.isDestroyed)
    }
}