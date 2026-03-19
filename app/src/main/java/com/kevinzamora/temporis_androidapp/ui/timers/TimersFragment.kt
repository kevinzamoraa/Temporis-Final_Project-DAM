package com.kevinzamora.temporis_androidapp.ui.timers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevinzamora.temporis_androidapp.databinding.FragmentTimersBinding
import com.kevinzamora.temporis_androidapp.model.Timer
import com.kevinzamora.temporis_androidapp.ui.timer.TimerAdapter
import com.kevinzamora.temporis_androidapp.viewmodel.TimerViewModel

class TimersFragment : Fragment() {

    // CAMBIO AQUÍ: Usamos FragmentTimersBinding
    private lateinit var binding: FragmentTimersBinding
    private lateinit var adapter: TimerAdapter
    private val timerViewModel: TimerViewModel by viewModels()
    private var editingTimerId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // CAMBIO AQUÍ: Inflamos fragment_timers en lugar de fragment_home
        binding = FragmentTimersBinding.inflate(inflater, container, false)

        // Configurar RecyclerView
        adapter = TimerAdapter()
        binding.recyclerViewTimers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimers.adapter = adapter

        // Lógica de botones (se mantiene igual porque los IDs ahora coincidirán con fragment_timers.xml)
        binding.buttonCreateTimer.setOnClickListener {
            val name = binding.editTextTimerName.text.toString().trim()
            val duration = binding.editTextTimerDuration.text.toString().toIntOrNull()

            if (name.isNotEmpty() && duration != null) {
                val id = editingTimerId
                if (id == null) {
                    timerViewModel.addTimer(name, duration)
                    Toast.makeText(requireContext(), "Temporizador creado", Toast.LENGTH_SHORT).show()
                } else {
                    val originalTimer = timerViewModel.timers.value?.find { it.id == id }
                    if (originalTimer != null) {
                        val updatedTimer = Timer().apply {
                            this.id = originalTimer.id
                            this.name = name
                            this.duration = duration
                            this.isActive = originalTimer.isActive
                            this.createdAt = originalTimer.createdAt
                            this.uid = originalTimer.uid
                        }
                        timerViewModel.updateTimer(updatedTimer)
                        Toast.makeText(requireContext(), "Temporizador actualizado", Toast.LENGTH_SHORT).show()
                    }
                    editingTimerId = null
                    binding.buttonCreateTimer.text = "Crear temporizador"
                }
                binding.editTextTimerName.text.clear()
                binding.editTextTimerDuration.text.clear()
            }
        }

        // ... resto de los listeners del adapter (onEditClick, onDeleteClick, etc.) se mantienen igual ...

        adapter.onEditClick = { timer ->
            editingTimerId = timer.id
            binding.editTextTimerName.setText(timer.name)
            binding.editTextTimerDuration.setText(timer.duration.toString())
            binding.buttonCreateTimer.text = "Actualizar temporizador"
        }

        adapter.onDeleteClick = { timer ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar temporizador")
                .setMessage("¿Estás seguro de que quieres eliminar este temporizador?")
                .setPositiveButton("Sí") { _, _ ->
                    timerViewModel.deleteTimer(timer.id)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        timerViewModel.timers.observe(viewLifecycleOwner, Observer { timers ->
            adapter.submitList(timers)
        })

        return binding.root
    }
}