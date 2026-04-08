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

        // 1. Recuperamos el valor guardado (si no existe, 1.0)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)

        // 2. Configuramos el slider
        binding.sliderFontSize.apply {
            // Importante: No asignes stepSize aquí tampoco
            value = savedFontSize.coerceIn(0.8f, 1.4f)
        }

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