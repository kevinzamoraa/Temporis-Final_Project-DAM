package com.kevinzamora.temporis_androidapp.ui.appinfo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAppInfoBinding

class AppInfoFragment : Fragment(R.layout.fragment_app_info) {

    private var _binding: FragmentAppInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            _binding = FragmentAppInfoBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e("AppInfoCrash", "Error inflado: ${e.message}")
            View(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return
        applySavedFontScale() // <--- Añadir esto

        binding.btnGithub.setOnClickListener { openUrl("https://github.com/kevinzamoraa") }
        binding.btnLinkedin.setOnClickListener { openUrl("https://www.linkedin.com/in/kevin-zamora-webdev/") }
    }

    private fun applySavedFontScale() {
        try {
            val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)

            val config = resources.configuration
            // Solo actualizamos si hay una diferencia para evitar refrescos innecesarios
            if (config.fontScale != savedFontSize) {
                config.fontScale = savedFontSize
                val metrics = resources.displayMetrics
                @Suppress("DEPRECATION")
                resources.updateConfiguration(config, metrics)
            }
        } catch (e: Exception) {
            Log.e("FontError", "No se pudo aplicar el tamaño de fuente en Inicio: ${e.message}")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}