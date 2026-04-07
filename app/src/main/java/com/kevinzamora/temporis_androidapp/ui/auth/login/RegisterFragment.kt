package com.kevinzamora.temporis_androidapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinzamora.temporis_androidapp.repository.UserRepository
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val btnRegistrar = root.findViewById<Button>(R.id.btnRegistroRegistrar)
        val etRegistroEmail = root.findViewById<EditText>(R.id.etRegistroEmail)
        val etRegistroContra = root.findViewById<EditText>(R.id.etRegistroContra)

        val etRegistroConfirmContra = root.findViewById<EditText>(R.id.etRegistroConfirm)

        val etRegistroUserName = root.findViewById<EditText>(R.id.etRegistroUserName)

        auth = FirebaseAuth.getInstance()

        btnRegistrar.setOnClickListener {
            val email = etRegistroEmail.text.toString().trim()
            val password = etRegistroContra.text.toString().trim()
            val confirmPassword = etRegistroConfirmContra.text.toString().trim()
            val username = etRegistroUserName.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && username.isNotEmpty()) {
                if (comprobarEmail(email)) {
                    if (password.length >= 6) {
                        if (password == confirmPassword) {
                            registrar(username, email, password)
                        } else {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Formato de email inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun registrar(username: String, email: String, contra: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, contra)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = auth.uid.toString()
                                val defaultPhoto = "https://img.freepik.com/premium-vector/gamer-man_961307-25037.jpg?semt=ais_hybrid&w=740"
                                val newUser = User(uid, username, email, username, defaultPhoto).apply {
                                    setRol(1)
                                }
                                lifecycleScope.launch {
                                    userRepository.saveUser(newUser).collect { result ->
                                        result.onSuccess {
                                            if (isAdded) {
                                                Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                                                parentFragmentManager.popBackStack()
                                            }
                                        }.onFailure { e ->
                                            Toast.makeText(context, "Error Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Error Auth: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun comprobarEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return pattern.matcher(email).find()
    }
}