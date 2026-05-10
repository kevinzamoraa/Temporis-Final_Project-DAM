package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAccessibilityBinding
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [32]) // Usamos 32 para evitar conflictos de Resources en el SDK 35 con JaCoCo
class AccessibilityFragmentTest {

    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        // Obtenemos las SharedPreferences de la aplicación (Robolectric)
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Context>()
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Limpiamos preferencias antes de cada test para tener un estado limpio
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun testFragmentInitialization_loadsSavedPreferences() {
        // Pre-configuramos valores en las SharedPreferences
        sharedPreferences.edit()
            .putFloat("font_size_scale", 1.2f)
            .putBoolean("high_contrast", true)
            .apply()

        val scenario = launchFragmentInContainer<AccessibilityFragment>(themeResId = R.style.Theme_TemporisAndroidApp)

        scenario.onFragment { fragment ->
            // Accedemos al binding mediante reflexión o verificamos el estado de los componentes
            // En este caso, comprobamos que el Slider tiene el valor correcto
            val bindingField = AccessibilityFragment::class.java.getDeclaredField("_binding")
            bindingField.isAccessible = true
            val binding = bindingField.get(fragment) as FragmentAccessibilityBinding

            assertEquals(1.2f, binding.sliderFontSize.value)
            assertTrue(binding.switchHighContrast.isChecked)
        }
    }

    @Test
    fun testHighContrastSwitch_updatesPreferences() {
        val scenario = launchFragmentInContainer<AccessibilityFragment>(themeResId = R.style.Theme_TemporisAndroidApp)

        scenario.onFragment { fragment ->
            val bindingField = AccessibilityFragment::class.java.getDeclaredField("_binding")
            bindingField.isAccessible = true
            val binding = bindingField.get(fragment) as FragmentAccessibilityBinding

            // Simulamos el cambio del switch
            binding.switchHighContrast.isChecked = true

            // Verificamos que se guardó en SharedPreferences
            assertTrue(sharedPreferences.getBoolean("high_contrast", false))
        }
    }

    @Test
    fun testOpenTalkBackGuide_showsDialog() {
        val scenario = launchFragmentInContainer<AccessibilityFragment>(themeResId = R.style.Theme_TemporisAndroidApp)

        scenario.onFragment { fragment ->
            val bindingField = AccessibilityFragment::class.java.getDeclaredField("_binding")
            bindingField.isAccessible = true
            val binding = bindingField.get(fragment) as FragmentAccessibilityBinding

            // Hacemos clic en el botón de ayuda
            binding.btnShowTalkBackGuide.performClick()

            // Verificamos si se mostró un diálogo (ShadowDialog)
            val latestDialog = org.robolectric.shadows.ShadowAlertDialog.getLatestAlertDialog()
            assertNotNull("El diálogo debería haberse mostrado", latestDialog)
        }
    }

    @Test
    fun testOpenSettingsButton_triggersIntent() {
        val scenario = launchFragmentInContainer<AccessibilityFragment>(themeResId = R.style.Theme_TemporisAndroidApp)

        scenario.onFragment { fragment ->
            val bindingField = AccessibilityFragment::class.java.getDeclaredField("_binding")
            bindingField.isAccessible = true
            val binding = bindingField.get(fragment) as FragmentAccessibilityBinding

            binding.btnOpenTalkBack.performClick()

            // Verificamos que se lanzó el Intent de ajustes de accesibilidad
            val expectedIntent = android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
            val actualIntent = shadowOf(fragment.requireActivity()).nextStartedActivity
            assertEquals(expectedIntent, actualIntent.action)
        }
    }
}