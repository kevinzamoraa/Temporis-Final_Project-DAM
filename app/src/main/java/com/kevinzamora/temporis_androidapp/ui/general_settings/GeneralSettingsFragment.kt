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

        val sharespref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Cargar preferencias
        binding.switchTimeFormat.isChecked = sharespref.getBoolean("use_24h", true)
        binding.switchFinishSound.isChecked = sharespref.getBoolean("enable_sound", true)

        // 2. Guardar cambios de Formato Horario
        binding.switchTimeFormat.setOnCheckedChangeListener { _, isChecked ->
            sharespref.edit().putBoolean("use_24h", isChecked).apply()
        }

        // 3. Guardar cambios de Sonido
        binding.switchFinishSound.setOnCheckedChangeListener { _, isChecked ->
            sharespref.edit().putBoolean("enable_sound", isChecked).apply()
        }

        // 4. Lógica de cambio de idioma (la implementaremos a continuación)
        binding.btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Español", "English", "Català / Valencià")
        val languageCodes = arrayOf("es", "en", "ca")
        val sharespref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Selecciona Idioma")
            .setItems(languages) { _, which ->
                val selectedLanguage = languageCodes[which]

                // 1. Guardar en Sharespreferences para que ThemeUtils lo lea
                sharespref.edit().putString("language", selectedLanguage).apply()

                // 2. Aplicar vía AppCompatDelegate (recomendado para Android 13+)
                val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(selectedLanguage)
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)

                // 3. Reiniciar actividad para forzar cambio de textos
                requireActivity().recreate()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}