package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAccessibilityBinding
import kotlin.math.roundToInt

class AccessibilityFragment : Fragment(R.layout.fragment_accessibility) {

    private var _binding: FragmentAccessibilityBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccessibilityBinding.bind(view)

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Cargar y normalizar el valor (Importante para cambios de idioma/recreación)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)

        // Función para redondear al primer decimal (evita el error 1.0000001)
        val normalizedValue = (savedFontSize * 10).roundToInt() / 10.0f

        binding.switchHighContrast.isChecked = sharedPref.getBoolean("high_contrast", false)
        binding.switchBoldText.isChecked = sharedPref.getBoolean("bold_text", false)

        // 2. Aplicación segura con post y doble validación
        binding.sliderFontSize.post {
            try {
                // Coerción estricta y asignación
                binding.sliderFontSize.value = normalizedValue.coerceIn(0.8f, 1.4f)
            } catch (e: Exception) {
                // Si falla por precisión, forzamos el valor por defecto
                binding.sliderFontSize.value = 1.0f
            }
        }

        // 3. Listeners mejorados
        binding.switchHighContrast.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
                activity?.recreate()
            }
        }

        binding.switchBoldText.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                sharedPref.edit().putBoolean("bold_text", isChecked).apply()
                activity?.recreate()
            }
        }

        binding.sliderFontSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val roundedValue = (value * 10).roundToInt() / 10.0f
                sharedPref.edit().putFloat("font_size_scale", roundedValue).commit() // Usamos commit() para asegurar escritura inmediata

                // Opcional: Avisar a la actividad que debe refrescarse si queremos cambio instantáneo
                // activity?.recreate()
            }
        }

        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode

        binding.sliderFontSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val roundedValue = (value * 10).roundToInt() / 10.0f

                // GUARDADO CRÍTICO: Usamos apply() pero forzamos la persistencia
                sharedPref.edit().putFloat("font_size_scale", roundedValue).apply()

                // En lugar de recrear toda la actividad (que causa el crash en modo oscuro),
                // avisamos al sistema de que la configuración ha cambiado.
                updateCustomFontScale(roundedValue)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Función de apoyo dentro del Fragment para aplicar el cambio visualmente
    private fun updateCustomFontScale(scale: Float) {
        val configuration = resources.configuration
        configuration.fontScale = scale
        val metrics = resources.displayMetrics
        resources.updateConfiguration(configuration, metrics)

        // Solo recreamos si es estrictamente necesario para ver el cambio
        activity?.recreate()
    }
}