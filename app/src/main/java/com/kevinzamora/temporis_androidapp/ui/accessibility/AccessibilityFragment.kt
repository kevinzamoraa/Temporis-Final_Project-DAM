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

        // Usamos un solo archivo de preferencias coherente
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)

        // 1. Cargar estado guardado
        // binding.switchBoldText.isChecked = sharedPref.getBoolean("bold_text", false)
        binding.switchHighContrast.isChecked = sharedPref.getBoolean("high_contrast", false)

        // 2. Escuchar cambios de Texto en Negrita
        /*binding.switchBoldText.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("bold_text", isChecked).apply()
            Toast.makeText(context, "Ajuste de texto guardado", Toast.LENGTH_SHORT).show()
            // Nota: Para aplicar negrita global, lo ideal es reiniciar la actividad
            activity?.recreate()
        }*/

        // 3. Escuchar cambios de Alto Contraste
        binding.switchHighContrast.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("high_contrast", isChecked).apply()
            Toast.makeText(context, "Ajuste de contraste guardado", Toast.LENGTH_SHORT).show()
            activity?.recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}