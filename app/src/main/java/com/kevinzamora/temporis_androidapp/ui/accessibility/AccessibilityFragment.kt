package com.kevinzamora.temporis_androidapp.ui.accessibility

import android.content.Context
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

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // Cargar estados
        binding.switchHighContrast.isChecked = sharedPref.getBoolean("high_contrast", false)
        binding.switchBoldText.isChecked = sharedPref.getBoolean("bold_text", false)
        // Esta fórmula asegura que el número sea exactamente un múltiplo de 0.1 (ej: 1.04 -> 1.0)
        val savedFontSize = 0.0
        val validatedValue = Math.round(savedFontSize * 10) / 10.0f

        // Validamos que esté en el rango y lo asignamos
        if (validatedValue in 0.8f..1.4f) {
            binding.sliderFontSize.value = validatedValue
        } else {
            binding.sliderFontSize.value = 1.0f
        }
        binding.sliderFontSize.value = if (validatedValue in 0.8f..1.4f) validatedValue else 1.0f
        binding.sliderFontSize.addOnChangeListener { _, value, _ ->
            sharedPref.edit().putFloat("font_size_scale", value).apply()
            // OPCIONAL: requireActivity().recreate()
            // Nota: Recreate() aplicará el tamaño a TODA la app, pero cerrará el fragment actual.
        }

        // Listener Alto Contraste
        binding.switchHighContrast.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
            requireActivity().recreate()
        }

        // Listener Negrita
        binding.switchBoldText.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("bold_text", isChecked).apply()
            requireActivity().recreate()
        }

        // Listener Slider Tamaño
        binding.sliderFontSize.addOnChangeListener { _, value, _ ->
            sharedPref.edit().putFloat("font_size_scale", value).apply()
            // No recreamos aquí para que la experiencia sea fluida,
            // se aplicará al cambiar de pantalla o reiniciar.
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}