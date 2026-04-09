package com.kevinzamora.temporis_androidapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kevinzamora.temporis_androidapp.model.Timer

class TimerRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val timersLiveData = MutableLiveData<List<Timer>>()
    private var listenerRegistration: ListenerRegistration? = null

    fun getTimers(): LiveData<List<Timer>> {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            listenerRegistration?.remove()
            listenerRegistration = firestore.collection("timers")
                .whereEqualTo("uid", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("TimerRepository", "Error al obtener temporizadores", error)
                        timersLiveData.value = emptyList()
                        return@addSnapshotListener
                    }

                    Log.d("TimerRepository", "Snapshot recibido: ${snapshot?.documents?.size} documentos")

                    // Bloque Try-Catch añadido en el mapeo
                    val timers = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Timer::class.java)?.apply { id = doc.id }
                        } catch (e: Exception) {
                            Log.e("TimerRepository", "Error al convertir documento ${doc.id}", e)
                            null // Si falla este documento, se ignora y no rompe la app
                        }
                    } ?: emptyList()

                    Log.d("TimerRepository", "Timers parseados: $timers")
                    timersLiveData.value = timers
                }
        } else {
            timersLiveData.value = emptyList()
        }
        return timersLiveData
    }

    fun addTimer(name: String, duration: Int, isActive: Boolean = false) {
        val uid = auth.currentUser?.uid ?: return
        val newTimer = hashMapOf(
            "name" to name,
            "duration" to duration,
            "isActive" to isActive,
            "createdAt" to Timestamp.now(),
            "uid" to uid
        )
        firestore.collection("timers").add(newTimer)
            .addOnSuccessListener { Log.d("TimerRepository", "ID: ${it.id}") }
            .addOnFailureListener { Log.e("TimerRepository", "Error", it) }
    }

    fun updateTimer(timer: Timer) {
        if (timer.id.isNullOrEmpty()) return
        val updatedTimer = hashMapOf(
            "name" to timer.name,
            "duration" to timer.duration,
            "isActive" to timer.isActive,
            "createdAt" to timer.createdAt,
            "uid" to auth.currentUser?.uid
        )
        firestore.collection("timers").document(timer.id!!).set(updatedTimer)
            .addOnSuccessListener { Log.d("TimerRepository", "Actualizado") }
            .addOnFailureListener { Log.e("TimerRepository", "Error", it) }
    }

    fun deleteTimer(timerId: String) {
        firestore.collection("timers").document(timerId).delete()
            .addOnSuccessListener { Log.d("TimerRepository", "Eliminado") }
            .addOnFailureListener { Log.e("TimerRepository", "Error", it) }
    }

    fun clearListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
