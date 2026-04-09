package com.kevinzamora.temporis_androidapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.kevinzamora.temporis_androidapp.MainActivity
import com.kevinzamora.temporis_androidapp.repository.UserRepository
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()
    private lateinit var btnRegistrar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_register, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        btnRegistrar = root.findViewById(R.id.btnRegistroRegistrar)
        val etEmail = root.findViewById<EditText>(R.id.etRegistroEmail)
        val etContra = root.findViewById<EditText>(R.id.etRegistroContra)
        val etConfirm = root.findViewById<EditText>(R.id.etRegistroConfirm)
        val etUser = root.findViewById<EditText>(R.id.etRegistroUserName)

        auth = FirebaseAuth.getInstance()

        btnRegistrar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etContra.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()
            val username = etUser.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirm.isNotEmpty() && username.isNotEmpty()) {
                if (comprobarEmail(email)) {
                    if (password.length >= 6) {
                        if (password == confirm) {
                            // BLOQUEAMOS el botón para evitar que el usuario pulse mil veces
                            btnRegistrar.isEnabled = false
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

        // PASO 1: Comprobar disponibilidad de Username (Aquí fallaba por permisos)
        db.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // PASO 2: Crear usuario en Firebase Auth
                    auth.createUserWithEmailAndPassword(email, contra)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                val uid = firebaseUser?.uid ?: ""
                                val defaultPhoto = "https://img.freepik.com/premium-vector/gamer-man_961307-25037.jpg?semt=ais_hybrid&w=740"

                                val newUser = User(uid, username, email, username, defaultPhoto)
                                newUser.rol = 1

                                // PASO 3: Guardar el perfil en Firestore
                                lifecycleScope.launch {
                                    userRepository.saveUser(newUser).collect { result ->
                                        if (result.isSuccess) {
                                            // PASO 4: Actualizar el DisplayName en el objeto Auth
                                            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                                                displayName = username
                                                photoUri = android.net.Uri.parse(defaultPhoto)
                                            }
                                            firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener {
                                                if (isAdded) {
                                                    Toast.makeText(context, "¡Registro completado con éxito!", Toast.LENGTH_SHORT).show()
                                                    goToMain()
                                                }
                                            }
                                        } else {
                                            btnRegistrar.isEnabled = true
                                            Toast.makeText(context, "Error al guardar perfil: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                btnRegistrar.isEnabled = true
                                Toast.makeText(context, "Error en Auth: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    btnRegistrar.isEnabled = true
                    Toast.makeText(context, "El nombre de usuario '$username' ya está en uso", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Si entra aquí, es que las reglas de Firebase siguen bloqueando o no hay internet
                btnRegistrar.isEnabled = true
                Log.e("FIRESTORE_ERROR", "Error: ${e.message}")
                Toast.makeText(context, "Error de permisos/conexión: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun goToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun comprobarEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return pattern.matcher(email).find()
    }
}