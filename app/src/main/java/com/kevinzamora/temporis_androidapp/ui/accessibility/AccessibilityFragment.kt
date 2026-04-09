package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAccessibilityBinding
import kotlin.math.roundToInt

class AccessibilityFragment : Fragment(R.layout.fragment_accessibility) {

    private var _binding: FragmentAccessibilityBinding? = null
    private val binding get() = _binding!!
    private var isInitializing = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccessibilityBinding.bind(view)
        isInitializing = true // Marcamos inicio

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

// --- 1. CARGAR ESTADOS ACTUALES (Esto soluciona que los botones se vean apagados) ---
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val isHighContrast = sharedPref.getBoolean("high_contrast", false)
        val isBoldText = sharedPref.getBoolean("bold_text", false)

        binding.sliderFontSize.value = savedFontSize.coerceIn(0.8f, 1.4f)
        binding.switchHighContrast.isChecked = isHighContrast
        binding.switchBoldText.isChecked = isBoldText

        // ... carga de datos ...
        binding.switchHighContrast.isChecked = isHighContrast
        binding.switchBoldText.isChecked = isBoldText

        // 3. Usamos un Listener más sencillo para guardar el valor
        //addOnChangeListener se dispara mientras el usuario mueve el slider
        binding.sliderFontSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                val newValue = slider.value

                // Guardamos el valor tal cual
                sharedPref.edit().putFloat("font_size_scale", newValue).apply()

                // Aplicamos el cambio
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded && activity != null) {
                        updateCustomFontScale(newValue)
                    }
                }, 150)
            }
        })

        // 4. Listeners para Switches con validación de pulsación manual
        binding.switchHighContrast.setOnCheckedChangeListener { _, isChecked ->
            // Solo actuamos si no estamos inicializando la vista
            if (!isInitializing) {
                val current = sharedPref.getBoolean("high_contrast", false)
                if (current != isChecked) {
                    sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
                    triggerRecreate()
                }
            }
        }

        binding.switchBoldText.setOnCheckedChangeListener { _, isChecked ->
            if (!isInitializing) {
                val current = sharedPref.getBoolean("bold_text", false)
                if (current != isChecked) {
                    sharedPref.edit().putBoolean("bold_text", isChecked).apply()
                    triggerRecreate()
                }
            }
        }

        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode

        // Al final del setup de vistas, cambiamos el estado
        isInitializing = false
    }

    private fun updateCustomFontScale(scale: Float) {
        val activity = activity ?: return
        try {
            val configuration = activity.resources.configuration
            configuration.fontScale = scale
            val metrics = activity.resources.displayMetrics
            activity.resources.updateConfiguration(configuration, metrics)

            // ¡Cuidado aquí!Esto llama a triggerRecreate() para que se active la bandera
            triggerRecreate()
        } catch (e: Exception) {
            Log.e("Accessibility", "Error recreando: ${e.message}")
        }
    }

    private fun triggerRecreate() {
        val activity = activity ?: return
        // Guardamos en SharedPreferences que queremos volver a Accesibilidad tras recrear
        val sharedPref = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("should_return_to_accessibility", true).apply()

        activity.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}