package com.kevinzamora.temporis_androidapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentSettingsBinding
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Navegación a Perfil (Asegúrate de que el ID en mobile_navigation coincida)
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }

        // Navegación a Accesibilidad
        binding.btnAccessibility.setOnClickListener {
            findNavController().navigate(R.id.navigation_accessibility)
        }

        // Navegación a Ajustes Generales
        binding.btnGeneralSettings.setOnClickListener {
            findNavController().navigate(R.id.navigation_general_settings)
        }

        // Navegación a Info de la Aplicación
        binding.btnAppInfo.setOnClickListener {
            findNavController().navigate(R.id.navigation_app_info)
        }

        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}