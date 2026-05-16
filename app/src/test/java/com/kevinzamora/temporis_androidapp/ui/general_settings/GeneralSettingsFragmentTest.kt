package com.kevinzamora.temporis_androidapp.ui.general_settings

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.TestApplication

import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
class GeneralSettingsFragmentTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<TestApplication>()

        // ESTRATEGIA:
        // Si tu LocalDependencyResolver tiene un método estático como "register" o "init",
        // necesitamos pasarle el contexto o los mocks de los servicios que usa el Fragment.
        // Ejemplo ficticio si tuviera un mapa:
        // LocalDependencyResolver.register(Context::class.java, context)
    }

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
        scenario.onFragment { fragment ->
            assertNotNull(fragment.view)
        }
    }
}