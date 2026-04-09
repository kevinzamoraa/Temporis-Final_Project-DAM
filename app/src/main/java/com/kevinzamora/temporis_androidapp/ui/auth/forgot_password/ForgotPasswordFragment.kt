package com.kevinzamora.temporis_androidapp.ui.auth.forgotten_password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPasswordBinding.bind(view)

        // Configuración del botón de enviar
        binding.btnContraOlvidadaEnviar.setOnClickListener {
            val email = binding.etContraOlvidadaEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                sendPasswordReset(email)
            } else {
                Toast.makeText(requireContext(), "Por favor, introduce tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuración de la flecha atrás (ImageView)
        binding.ivRegistroAtras2.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun sendPasswordReset(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Visualización del mensaje de éxito
                    Toast.makeText(
                        requireContext(),
                        "Se ha enviado un correo de recuperación a: $email",
                        Toast.LENGTH_LONG
                    ).show()

                    // Redirección automática al Login
                    findNavController().popBackStack()
                } else {
                    // Visualización de error (ej: email no registrado o error de red)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}