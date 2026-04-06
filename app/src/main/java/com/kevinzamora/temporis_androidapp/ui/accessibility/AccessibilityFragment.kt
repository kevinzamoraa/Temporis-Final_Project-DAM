package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
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

        // 1. Configuración inicial de los estados (sin disparar recreación)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val isHighContrast = sharedPref.getBoolean("high_contrast", false)
        val isBoldText = sharedPref.getBoolean("bold_text", false)

        // Validación de seguridad para el Slider
        if (savedFontSize >= binding.sliderFontSize.valueFrom && savedFontSize <= binding.sliderFontSize.valueTo) {
            binding.sliderFontSize.value = savedFontSize
        } else {
            binding.sliderFontSize.value = 1.0f
        }

        binding.switchHighContrast.isChecked = isHighContrast
        binding.switchBoldText.isChecked = isBoldText

        // 2. Listeners con protección contra bucles de recreación (isPressed)
        binding.switchHighContrast.setOnCheckedChangeListener { buttonView, isChecked ->
            // isPressed asegura que solo responda a la pulsación física del usuario
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
            // Solo guardamos, no recreamos aquí para evitar una mala experiencia al deslizar
            if (fromUser) {
                sharedPref.edit().putFloat("font_size_scale", value).apply()
            }
        }

        // 3. Estado del Modo Oscuro (Solo lectura)
        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode
        binding.switchDarkMode.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}