package com.kevinzamora.temporis_androidapp.ui.general_settings

import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.TestApplication
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
class GeneralSettingsFragmentTest {

    @Test
    fun testOnViewCreated() {
        // Creamos un controlador de actividad pura de fragmentos
        val controller = Robolectric.buildActivity(FragmentActivity::class.java)
        val activity = controller.get()

        // Asignamos el tema visual antes de crear la vista
        activity.setTheme(R.style.Theme_TemporisAndroidApp)

        // Creamos un contenedor layout dinámico en la actividad de pruebas
        val container = FrameLayout(activity).apply { id = android.R.id.content }
        activity.setContentView(container)

        // Pasamos la actividad por su ciclo de vida
        controller.create().start().resume()

        val fragment = GeneralSettingsFragment()

        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment)
            .commitNow()

        assertNotNull(fragment.view)
    }

    @Test
    fun testOnDestroyView() {
        val controller = Robolectric.buildActivity(FragmentActivity::class.java)
        val activity = controller.get()
        activity.setTheme(R.style.Theme_TemporisAndroidApp)

        val container = FrameLayout(activity).apply { id = android.R.id.content }
        activity.setContentView(container)

        controller.create().start().resume()

        val fragment = GeneralSettingsFragment()

        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment)
            .commitNow()

        assertNotNull(fragment.view)
    }
}