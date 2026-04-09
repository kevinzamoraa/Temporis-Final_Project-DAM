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

        // 1. Actualizar el texto del botón según el idioma guardado
        val currentLang = sharedPref.getString("language", "es")
        binding.btnChangeLanguage.text = when(currentLang) {
            "en" -> "English"
            "ca" -> "Català"
            else -> "Español"
        }

        // 2. Lógica de cambio de idioma
        binding.btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Español", "English", "Català / Valencià")
        val languageCodes = arrayOf("es", "en", "ca")
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.label_language)) // Usa el string del archivo que creamos antes
            .setItems(languages) { _, which ->
                val selectedLanguage = languageCodes[which]

                // Guardar preferencia
                sharedPref.edit().putString("language", selectedLanguage).apply()

                // Aplicar vía AppCompatDelegate (Cambia el idioma sin cerrar la app)
                val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(selectedLanguage)
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)

                // Forzar recreación para refrescar todos los textos
                requireActivity().recreate()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}