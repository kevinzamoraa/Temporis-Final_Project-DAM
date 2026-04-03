package com.kevinzamora.temporis_androidapp.ui.general_settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentGeneralSettingsBinding

class GeneralSettingsFragment : Fragment(R.layout.fragment_general_settings) {

    private var _binding: FragmentGeneralSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGeneralSettingsBinding.bind(view)

        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Cargar preferencias
        binding.switchTimeFormat.isChecked = sharedPref.getBoolean("use_24h", true)
        binding.switchFinishSound.isChecked = sharedPref.getBoolean("enable_sound", true)

        // 2. Guardar cambios de Formato Horario
        binding.switchTimeFormat.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("use_24h", isChecked).apply()
        }

        // 3. Guardar cambios de Sonido
        binding.switchFinishSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("enable_sound", isChecked).apply()
        }

        // 4. Lógica de cambio de idioma (la implementaremos a continuación)
        binding.btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Español", "English")
        val languageCodes = arrayOf("es", "en")

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Selecciona Idioma")
            .setItems(languages) { _, which ->
                val selectedLanguage = languageCodes[which]

                // Aplicar el idioma globalmente
                val appLocale: androidx.core.os.LocaleListCompat =
                    androidx.core.os.LocaleListCompat.forLanguageTags(selectedLanguage)
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)

                requireActivity().recreate() // Reiniciar para aplicar
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}