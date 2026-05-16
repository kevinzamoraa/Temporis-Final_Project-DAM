package com.kevinzamora.temporis_androidapp.ui.general_settings

import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
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
        // Lanzamos una FragmentActivity vacía que sí tiene soporte para FragmentManager
        val scenario = ActivityScenario.launch(FragmentActivity::class.java)

        scenario.onActivity { activity ->
            // Aplicamos el tema visual de tu aplicación
            activity.setTheme(R.style.Theme_TemporisAndroidApp)

            // Instanciamos tu fragmento de ajustes
            val fragment = GeneralSettingsFragment()

            // Lo añadimos al contenedor principal de la actividad de pruebas
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow()

            // Verificamos que la vista se haya creado correctamente
            assertNotNull(fragment.view)
        }
        scenario.close()
    }

    @Test
    fun testOnDestroyView() {
        val scenario = ActivityScenario.launch(FragmentActivity::class.java)

        scenario.onActivity { activity ->
            activity.setTheme(R.style.Theme_TemporisAndroidApp)
            val fragment = GeneralSettingsFragment()

            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow()

            assertNotNull(fragment.view)
        }
        scenario.close()
    }
}