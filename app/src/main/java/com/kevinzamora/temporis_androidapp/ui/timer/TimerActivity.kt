package com.kevinzamora.temporis_androidapp.ui.timer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevinzamora.temporis_androidapp.databinding.ActivityTimerBinding
import com.kevinzamora.temporis_androidapp.model.Timer
import com.kevinzamora.temporis_androidapp.adapter.TimerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private lateinit var adapter: TimerAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadTimers()
    }

    private fun setupRecyclerView() {
        // CORRECCIÓN CRÍTICA: Pasamos las funciones directamente en el constructor
        adapter = TimerAdapter(
            timerList = emptyList(),
            onPlayClick = { timer ->
                Toast.makeText(this, "Iniciar: ${timer.name}", Toast.LENGTH_SHORT).show()
                // Aquí irá tu lógica de CountDownTimer
            },
            onEditClick = { timer ->
                Toast.makeText(this, "Editar: ${timer.name}", Toast.LENGTH_SHORT).show()
                // Aquí podrías llamar a una función para abrir el formulario
            },
            onDeleteClick = { timer ->
                eliminarTimer(timer)
            }
        )

        binding.recyclerViewTimers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTimers.adapter = adapter
    }

    private fun eliminarTimer(timer: Timer) {
        // Usamos el ID del documento de Firebase
        db.collection("timers").document(timer.id ?: return)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Temporizador eliminado", Toast.LENGTH_SHORT).show()
                loadTimers()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTimers() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("timers")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val timers = result.mapNotNull { it.toObject(Timer::class.java).apply { id = it.id } }
                // CORRECCIÓN: Cambiamos submitList por updateData
                adapter.updateData(timers)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar temporizadores", Toast.LENGTH_SHORT).show()
            }
    }
}