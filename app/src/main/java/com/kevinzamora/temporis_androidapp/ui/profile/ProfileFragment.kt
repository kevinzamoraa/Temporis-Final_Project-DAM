package com.kevinzamora.temporis_androidapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentProfileBinding
import com.kevinzamora.temporis_androidapp.model.User
import com.kevinzamora.temporis_androidapp.repository.UserRepository
import com.kevinzamora.temporis_androidapp.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 1. Carga rápida desde Firebase Auth (evita que los campos salgan vacíos al inicio)
            loadInitialAuthData(currentUser)

            // 2. Carga completa desde Firestore
            loadFirestoreData(currentUser.uid)

            // Configuración de botones
            binding.btnSafe.setOnClickListener { updateProfile(currentUser.uid) }
            binding.btnDeleteAccount.setOnClickListener { confirmDeleteAccount() }
        }
    }

    private fun loadInitialAuthData(user: FirebaseUser) {
        binding.etEmail.setText(user.email)
        binding.etDisplayName.setText(user.displayName)
        binding.etUsername.setText(user.displayName?.replace("\\s+".toRegex(), "")?.lowercase())
        binding.etProfileUrl.setText(user.photoUrl.toString())

        Glide.with(this)
            .load(user.photoUrl)
            .placeholder(R.drawable.ic_default_profile)
            .circleCrop()
            .into(binding.imgProfilePhoto)
    }

    private fun loadFirestoreData(userId: String) {
        lifecycleScope.launch {
            try {
                val snapshot = db.collection("users").document(userId).get().await()

                // Usamos el mapeo automático a la clase User como tenías antes
                val userData = snapshot.toObject(User::class.java)

                userData?.let {
                    binding.etUsername.setText(it.username)
                    binding.etDisplayName.setText(it.displayName)
                    binding.etEmail.setText(it.email)
                    binding.etProfileUrl.setText(it.profilePhotoUrl)

                    Glide.with(this@ProfileFragment)
                        .load(it.profilePhotoUrl)
                        .placeholder(R.drawable.ic_default_profile)
                        .circleCrop()
                        .into(binding.imgProfilePhoto)
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error al cargar Firestore", e)
            }
        }
    }

    private fun updateProfile(uid: String) {
        val newDisplayName = binding.etDisplayName.text.toString()
        val newPhotoUrl = binding.etProfileUrl.text.toString()
        val newUsername = binding.etUsername.text.toString()

        val userUpdates = User().apply {
            setUid(uid)
            setUsername(newUsername)
            setDisplayName(newDisplayName)
            setProfilePhotoUrl(newPhotoUrl)
            setEmail(binding.etEmail.text.toString())
        }

        lifecycleScope.launch {
            // 1. Guardar en Firestore a través del repositorio
            userRepository.saveUser(userUpdates).collect { result ->
                result.onSuccess {
                    // 2. Si Firestore tiene éxito, sincronizamos con Firebase Auth
                    try {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = newDisplayName
                            photoUri = Uri.parse(newPhotoUrl)
                        }
                        auth.currentUser?.updateProfile(profileUpdates)?.await()
                        if (isAdded) Toast.makeText(context, "Perfil y Nube actualizados", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("AuthUpdate", "Error sincronizando Auth: ${e.message}")
                    }
                }.onFailure { e ->
                    if (isAdded) Toast.makeText(context, "Error en Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar cuenta")
            .setMessage("¿Estás completamente seguro? Se borrarán tus datos de Firestore y tu acceso. Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar definitivamente") { _, _ -> deleteAccount() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        lifecycleScope.launch {
            try {
                user?.let {
                    val userId = it.uid
                    // 1. Borrar documento en Firestore
                    db.collection("users").document(userId).delete().await()
                    // 2. Borrar usuario en Auth
                    it.delete().await()

                    if (isAdded) {
                        Toast.makeText(context, "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                if (isAdded) Toast.makeText(context, "Para borrar la cuenta debes haber iniciado sesión recientemente.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}