package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAccessibilityBinding

class AccessibilityFragment : Fragment(R.layout.fragment_accessibility) {

    private var _binding: FragmentAccessibilityBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccessibilityBinding.bind(view)

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Cargar preferencias
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val isHighContrast = sharedPref.getBoolean("high_contrast", false)
        val isBoldText = sharedPref.getBoolean("bold_text", false)

        // 2. Bloque Try-Catch para el Slider (Punto crítico del FATAL ERROR)
        try {
            binding.sliderFontSize.apply {
                // Aseguramos que el valor esté estrictamente entre 0.8 y 1.4
                val safeValue = savedFontSize.coerceIn(0.8f, 1.4f)
                value = safeValue
            }
        } catch (e: Exception) {
            // Si falla, registramos el error en el Logcat pero la app NO se cierra
            Log.e("AccessibilityError", "Error al configurar el Slider: ${e.message}")
            binding.sliderFontSize.value = 1.0f // Valor por defecto seguro
        }

        // 3. Configurar Switches
        binding.switchHighContrast.isChecked = isHighContrast
        binding.switchBoldText.isChecked = isBoldText

        // 4. Listeners
        binding.switchHighContrast.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
                requireActivity().recreate()
            }
        }

        binding.switchBoldText.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                sharedPref.edit().putBoolean("bold_text", isChecked).apply()
                requireActivity().recreate()
            }
        }

        binding.sliderFontSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                sharedPref.edit().putFloat("font_size_scale", value).apply()
            }
        }

        // Estado Modo Oscuro
        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode
        binding.switchDarkMode.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}