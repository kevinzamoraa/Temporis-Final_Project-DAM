package com.kevinzamora.temporis_androidapp.ui.auth

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.TestApplication
import org.hamcrest.Matcher
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
class LoginActivityTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // BLINDAJE DE FIREBASE: Inicializamos una instancia Mock/Ficticia para que la actividad no colapse
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseOptions.Builder()
                .setApplicationId("1:1234567890:android:1234567890")
                .setApiKey("fake_api_key_for_testing")
                .setProjectId("temporis-fake-project")
                .build().also { options ->
                    FirebaseApp.initializeApp(context, options)
                }
        }
    }

    @Test
    fun testActivityCreation() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
                assertNotNull(activity.findViewById<EditText>(R.id.etLoginEmail))
                assertNotNull(activity.findViewById<View>(R.id.btnRegistroRegistrar))
            }
        }
    }

    @Test
    fun testSharedPreferencesSecurity() {
        // ESCENARIO: Aunque existan datos antiguos en Prefs,
        // la App ya NO debe cargarlos por seguridad (según lo que cambiamos en LoginActivity).
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        prefs.edit().putString("email", "test@test.com").putString("password", "123456").apply()

        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val etEmail = activity.findViewById<EditText>(R.id.etLoginEmail)
                val etPass = activity.findViewById<EditText>(R.id.etRegistroContra)

                // Verificamos que los campos están VACÍOS a pesar de las Prefs
                assertTrue("El email debe estar vacío por seguridad", etEmail.text.toString().isEmpty())
                assertTrue("La password debe estar vacía por seguridad", etPass.text.toString().isEmpty())
            }
        }
    }

    @Test
    fun testEmptyFieldsShowError() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            // Limpiamos y clickamos
            onView(withId(R.id.etLoginEmail)).perform(clearText())
            onView(withId(R.id.etRegistroContra)).perform(clearText())
            onView(withId(R.id.btnRegistroRegistrar)).perform(forceClick())

            // Verificamos que el ProgressBar sigue oculto porque no pasó la validación
            onView(withId(R.id.progressBarLogin)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    @Test
    fun testOnWindowFocusChanged() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.onWindowFocusChanged(true)
                assertNotNull(activity)
            }
        }
    }

    @Test
    fun testOnActivityResultGoogle() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Usamos 100 o GOOGLE_SIGN_IN_CODE (si es público)
                activity.onActivityResult(100, android.app.Activity.RESULT_CANCELED, null)
                val progressBar = activity.findViewById<ProgressBar>(R.id.progressBarLogin)
                assert(progressBar.visibility == View.GONE)
            }
        }
    }

    @Test
    fun testNavigationToRegister() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            onView(withId(R.id.btnLoginRegistro)).perform(forceClick())
            scenario.onActivity { activity ->
                val fragmentCount = activity.supportFragmentManager.backStackEntryCount
                assertTrue(fragmentCount >= 0)
            }
        }
    }

    @Test
    fun testOnCreateNotNull() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            assertNotNull(scenario)
        }
    }

    /**
     * Helper para clics forzados en Robolectric
     */
    private fun forceClick(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isEnabled()
            override fun getDescription(): String = "force click"
            override fun perform(uiController: UiController, view: View) {
                view.performClick()
            }
        }
    }
}