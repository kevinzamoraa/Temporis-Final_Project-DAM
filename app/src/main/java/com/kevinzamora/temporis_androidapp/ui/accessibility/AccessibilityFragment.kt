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

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

// 1. Recuperamos el valor sucio (ej. 1.16)
        val rawFontSize = sharedPref.getFloat("font_size_scale", 1.0f)

// 2. Lo limpiamos ANTES de que toque el slider
// (1.16 * 10) = 11.6 -> round = 12 -> 12 / 10 = 1.2
        val cleanFontSize = (rawFontSize * 10).roundToInt() / 10.0f

// 3. Configuramos el slider por código
        binding.sliderFontSize.apply {
            value = cleanFontSize.coerceIn(0.8f, 1.4f)
            stepSize = 0.1f // Lo asignamos aquí, después de poner el valor inicial
        }

        binding.sliderFontSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                // Forzamos el valor al paso más cercano
                val snappedValue = (slider.value * 10).roundToInt() / 10.0f

                sharedPref.edit().putFloat("font_size_scale", snappedValue).apply()

                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded && activity != null) {
                        updateCustomFontScale(snappedValue)
                    }
                }, 150)
            }
        })

        // 4. Listeners para Switches con validación de pulsación manual
        binding.switchHighContrast.setOnClickListener {
            val isChecked = (it as com.google.android.material.switchmaterial.SwitchMaterial).isChecked
            sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
            activity?.recreate()
        }

        binding.switchBoldText.setOnClickListener {
            val isChecked = (it as com.google.android.material.switchmaterial.SwitchMaterial).isChecked
            sharedPref.edit().putBoolean("bold_text", isChecked).apply()
            activity?.recreate()
        }

        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode
    }

    private fun updateCustomFontScale(scale: Float) {
        val activity = activity ?: return
        try {
            val configuration = activity.resources.configuration
            configuration.fontScale = scale
            val metrics = activity.resources.displayMetrics
            activity.resources.updateConfiguration(configuration, metrics)
            activity.recreate()
        } catch (e: Exception) {
            Log.e("Accessibility", "Error recreando: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}