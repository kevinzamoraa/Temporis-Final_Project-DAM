package com.kevinzamora.temporis_androidapp.ui.auth.Login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.kevinzamora.temporis_androidapp.R
import com.google.firebase.auth.FirebaseAuth
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity
import java.util.regex.Pattern


class ForgottenPassword : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        val ivRegistroAtras = root.findViewById<ImageView>(R.id.ivRegistroAtras2)
        val etContraOlvidadaEmail = root.findViewById<EditText>(R.id.etContraOlvidadaEmail)
        val btnContraOlvidadaEnviar = root.findViewById<Button>(R.id.btnContraOlvidadaEnviar)

        auth = FirebaseAuth.getInstance()

        ivRegistroAtras.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnContraOlvidadaEnviar.setOnClickListener {
            val email = etContraOlvidadaEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                if (comprobarEmail(email)) {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Email no válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Introduce tu email", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun comprobarEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return pattern.matcher(email).find()
    }
}