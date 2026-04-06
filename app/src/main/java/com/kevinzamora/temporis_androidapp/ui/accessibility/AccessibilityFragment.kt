package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAccessibilityBinding

class AccessibilityFragment : Fragment(R.layout.fragment_accessibility) {

    private var _binding: FragmentAccessibilityBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccessibilityBinding.bind(view)

        // Dentro de onViewCreated, busca donde asignas el valor al slider:
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)

// CLAVE: Validar el valor ANTES de asignarlo al Slider para evitar el crash
        if (savedFontSize >= binding.sliderFontSize.valueFrom && savedFontSize <= binding.sliderFontSize.valueTo) {
            binding.sliderFontSize.value = savedFontSize
        } else {
            binding.sliderFontSize.value = 1.0f // Valor por defecto seguro
        }

        // Listeners corregidos
        binding.switchHighContrast.isChecked = sharedPref.getBoolean("high_contrast", false)
        binding.switchBoldText.isChecked = sharedPref.getBoolean("bold_text", false)

        binding.switchHighContrast.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
            requireActivity().recreate()
        }

        binding.switchBoldText.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("bold_text", isChecked).apply()
            requireActivity().recreate()
        }

        binding.sliderFontSize.addOnChangeListener { _, value, _ ->
            sharedPref.edit().putFloat("font_size_scale", value).apply()
            // No recreamos aquí para que no de saltos mientras el usuario desliza
        }

        // Punto 2: Botón/Switch informativo de Modo Oscuro
        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode
        binding.switchDarkMode.isEnabled = false // Informativo, no editable manualmente aquí
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}