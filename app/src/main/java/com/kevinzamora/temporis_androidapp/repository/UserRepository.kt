package com.kevinzamora.temporis_androidapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kevinzamora.temporis_androidapp.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val usersCollection = db.collection("users")

    // Obtener usuario
    fun getUser(userId: String): Flow<Result<User?>> = flow {
        try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Función UNIFICADA para crear o actualizar (Réplica eficiente)
    fun saveUser(user: User): Flow<Result<Boolean>> = flow {
        try {
            // Usamos SetOptions.merge() para no borrar campos extra (como rol o QrCode)
            // si solo estamos actualizando el nombre o la foto.
            usersCollection.document(user.uid).set(user, SetOptions.merge()).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Función para borrar los datos de Firestore
    suspend fun deleteUserFirestore(userId: String) {
        usersCollection.document(userId).delete().await()
    }
}