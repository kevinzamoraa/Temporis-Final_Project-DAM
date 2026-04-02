package com.kevinzamora.temporis_androidapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        // Botón Cerrar Sesión
        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }

        // Placeholders para las otras opciones
        binding.btnProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Perfil próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnAppInfo.setOnClickListener {
            Toast.makeText(requireContext(), "Temporis v1.0", Toast.LENGTH_SHORT).show()
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