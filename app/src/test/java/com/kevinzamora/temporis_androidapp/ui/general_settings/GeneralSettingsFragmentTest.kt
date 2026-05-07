package com.kevinzamora.temporis_androidapp.ui.general_settings

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.TestApplication
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
class GeneralSettingsFragmentTest {

    @Test
    fun testOnViewCreated() {
        val scenario = launchFragmentInContainer<GeneralSettingsFragment>(
            themeResId = R.style.Theme_TemporisAndroidApp
        )
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
        }
    }

    @Test
    fun testOnDestroyView() {
        val scenario = launchFragmentInContainer<GeneralSettingsFragment>(
            themeResId = R.style.Theme_TemporisAndroidApp
        )
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED)
    }

    /*@Test
    fun testFragmentDisplay() {
        // Lanzamos el fragmento con el tema de la app
        launchFragmentInContainer<GeneralSettingsFragment>(themeResId = R.style.Theme_TemporisAndroidApp)

        // Verificamos elementos clave para cobertura de onViewCreated
        onView(withId(R.id.btnChangeLanguage)).check(matches(isDisplayed()))
    }*/

}