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
        isInitializing = true

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. CARGAR ESTADOS ACTUALES
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val isHighContrast = sharedPref.getBoolean("high_contrast", false)
        val isBoldText = sharedPref.getBoolean("bold_text", false)

        binding.sliderFontSize.value = savedFontSize.coerceIn(0.8f, 1.4f)
        binding.switchHighContrast.isChecked = isHighContrast
        binding.switchBoldText.isChecked = isBoldText

        // 2. Listener Slider
        binding.sliderFontSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                val newValue = slider.value
                sharedPref.edit().putFloat("font_size_scale", newValue).apply()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded && activity != null) updateCustomFontScale(newValue)
                }, 150)
            }
        })

        // 3. Listeners Switches
        binding.switchHighContrast.setOnCheckedChangeListener { _, isChecked ->
            if (!isInitializing) {
                sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
                triggerRecreate()
            }
        }

        binding.switchBoldText.setOnCheckedChangeListener { _, isChecked ->
            if (!isInitializing) {
                sharedPref.edit().putBoolean("bold_text", isChecked).apply()
                triggerRecreate()
            }
        }

        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode

        // 4. Guía explicativa (Ventana Modal con Strings traducidos)
        binding.btnShowTalkBackGuide.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.talkback_guide_title))
                .setMessage(getString(R.string.talkback_guide_message))
                .setPositiveButton(getString(R.string.talkback_guide_button), null)
                .show()
        }

        // 5. Botón para Redirección a Ajustes
        binding.btnOpenTalkBack.setOnClickListener {
            val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        isInitializing = false
    }

    private fun updateCustomFontScale(scale: Float) {
        val activity = activity ?: return
        try {
            val configuration = activity.resources.configuration
            configuration.fontScale = scale
            activity.resources.updateConfiguration(configuration, activity.resources.displayMetrics)
            triggerRecreate()
        } catch (e: Exception) {
            Log.e("Accessibility", "Error recreando: ${e.message}")
        }
    }

    private fun triggerRecreate() {
        val activity = activity ?: return
        val sharedPref = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("should_return_to_accessibility", true).apply()
        activity.recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}