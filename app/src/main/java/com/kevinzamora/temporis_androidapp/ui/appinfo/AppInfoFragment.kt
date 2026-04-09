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
import com.kevinzamora.temporis_androidapp.databinding.FragmentAppInfoBinding

class AppInfoFragment : Fragment() {

    private var _binding: FragmentAppInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return try {
            _binding = FragmentAppInfoBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e("AppInfoCrash", "Error Modo Oscuro: ${e.message}")
            View(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return

        applySavedFontScale()

        binding.btnGithub.setOnClickListener { openUrl("https://github.com/kevinzamoraa") }
        binding.btnLinkedin.setOnClickListener { openUrl("https://www.linkedin.com/in/kevin-zamora-webdev/") }
    }

    private fun applySavedFontScale() {
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val config = resources.configuration

        // Solo aplicar si la diferencia es significativa para evitar bucles de refresco
        if (Math.abs(config.fontScale - savedFontSize) > 0.01f) {
            config.fontScale = savedFontSize
            val metrics = resources.displayMetrics
            // Usamos el contexto de la aplicación para que sea más estable
            requireContext().applicationContext.resources.updateConfiguration(config, metrics)
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}