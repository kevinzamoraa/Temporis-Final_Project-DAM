package com.kevinzamora.temporis_androidapp.ui.auth

import junit.framework.TestCase
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.kevinzamora.temporis_androidapp.R
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(manifest=org.robolectric.annotation.Config.NONE)
class LoginActivityTest {

    fun testOnCreate() {
    }

    fun testOnWindowFocusChanged() {
    }

    fun testOnActivityResult() {
    }

    @Test
    fun testSharedPreferencesLoading() {
        // Simulamos datos guardados en Prefs
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("TemporisPrefs", Context.MODE_PRIVATE) // Ajusta al nombre de tu R.string.prefs_file
        prefs.edit().putString("email", "test@test.com").apply()

        ActivityScenario.launch(LoginActivity::class.java).use {
            // Verificamos que el email se cargó en el EditText
            onView(withId(R.id.etLoginEmail)).check(matches(withText("test@test.com")))
        }
    }

    @Test
    fun testActivityCreation() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity.findViewById(R.id.etLoginEmail))
            }
        }
    }

    // Rellena los métodos vacíos para cobertura
    @Test
    fun testOnCreateNotNull() {
        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
            assertNotNull(scenario)
        }
    }

}