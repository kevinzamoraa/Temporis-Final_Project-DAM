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
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity // Ajusta según tu paquete

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment3) // Usa el ID de tu mobile_navigation
        }

        binding.btnGeneralSettings.setOnClickListener {
            // Por ahora un Toast o crea un fragmento vacío
            Toast.makeText(requireContext(), "Ajustes en desarrollo", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        // 1. Cerrar sesión en Firebase
        FirebaseAuth.getInstance().signOut()

        // 2. Notificar al usuario
        Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        // 3. Redirigir al Login y limpiar el historial de navegación
        // Esto evita que el usuario pueda volver atrás a la app tras cerrar sesión
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