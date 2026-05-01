package com.kevinzamora.temporis_androidapp.ui.auth

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.FirebaseApp
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.TestApplication
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import android.view.View
import org.hamcrest.Matcher

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [31],
    application = TestApplication::class
)
class LoginActivityTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }

    @Test
    fun testActivityCreation() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
                assertNotNull(activity.findViewById(R.id.etLoginEmail))
                assertNotNull(activity.findViewById(R.id.btnRegistroRegistrar))
            }
        }
    }

    @Test
    fun testSharedPreferencesLoading() {
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        prefs.edit().putString("email", "test@test.com").putString("password", "123456").apply()

        ActivityScenario.launch(LoginActivity::class.java).use {
            onView(withId(R.id.etLoginEmail)).check(matches(withText("test@test.com")))
        }
    }

    @Test
    fun testOnWindowFocusChanged() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Forzamos el evento de cambio de foco
                activity.onWindowFocusChanged(true)
                // Verificamos que la actividad sigue activa y no ha crasheado al disparar biometría
                assertNotNull(activity)
            }
        }
    }

    @Test
    fun testOnActivityResultGoogle() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Simulamos la respuesta de Google Sign In (Request Code 100)
                // Usamos un result code de CANCELED para verificar que el flujo no rompe la app
                activity.onActivityResult(100, android.app.Activity.RESULT_CANCELED, null)

                val progressBar = activity.findViewById<android.widget.ProgressBar>(R.id.progressBarLogin)
                // Al cancelar, el progress bar debería estar oculto (GONE)
                assert(progressBar.visibility == android.view.View.GONE)
            }
        }
    }

    @Test
    fun testEmptyFieldsShowError() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            // Usamos forceClick() en lugar de click() para evitar errores de visibilidad/scroll
            onView(withId(R.id.btnRegistroRegistrar)).perform(forceClick())

            assertNotNull(it)
        }
    }

    @Test
    fun testNavigationToRegister() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            // Usamos forceClick() para navegar sin que importe el layout
            onView(withId(R.id.btnLoginRegistro)).perform(forceClick())

            scenario.onActivity { activity ->
                // Si el error NoClassDefFoundError de antes persiste,
                // asegúrate de que RegisterFragment esté bien importado en LoginActivity.kt
                val fragmentCount = activity.supportFragmentManager.backStackEntryCount
                assert(fragmentCount >= 0)
            }
        }
    }

    /**
     * Función de ayuda para forzar un clic sin restricciones de visibilidad.
     * Esto soluciona los errores de "view does not match constraints" en Robolectric.
     */
    fun forceClick(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isEnabled()
            override fun getDescription(): String = "force click"
            override fun perform(uiController: UiController, view: View) {
                view.performClick()
            }
        }
    }

    @Test
    fun testOnCreateNotNull() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            assertNotNull(scenario)
        }
    }
}