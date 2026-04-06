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
import com.kevinzamora.temporis_androidapp.adapter.TimerAdapter
import com.kevinzamora.temporis_androidapp.viewmodel.TimerViewModel

class TimersFragment : Fragment() {

    private var _binding: FragmentTimersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TimerAdapter
    private val timerViewModel: TimerViewModel by viewModels()
    private var editingTimerId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        // CORRECCIÓN: Pasamos los 4 parámetros obligatorios al constructor
        adapter = TimerAdapter(
            timerList = emptyList(),
            onPlayClick = { timer ->
                Toast.makeText(requireContext(), "Iniciando: ${timer.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { timer ->
                prepararEdicion(timer)
            },
            onDeleteClick = { timer ->
                mostrarDialogoEliminar(timer)
            }
        )

        binding.recyclerViewTimers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimers.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.buttonCreateTimer.setOnClickListener {
            val name = binding.editTextTimerName.text.toString().trim()
            val duration = binding.editTextTimerDuration.text.toString().toIntOrNull()

            if (name.isNotEmpty() && duration != null) {
                if (editingTimerId == null) {
                    timerViewModel.addTimer(name, duration)
                    Toast.makeText(requireContext(), "Temporizador creado", Toast.LENGTH_SHORT).show()
                } else {
                    actualizarTemporizador(name, duration)
                }
                limpiarFormulario()
            } else {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun prepararEdicion(timer: Timer) {
        editingTimerId = timer.id
        binding.editTextTimerName.setText(timer.name)
        binding.editTextTimerDuration.setText(timer.duration.toString())

        // Actualizamos textos del formulario compacto
        binding.tvFormTitle.text = "@string/title_edit_timer"
        binding.buttonCreateTimer.text = "@string/update_button"
        binding.editTextTimerName.requestFocus()
    }

    private fun actualizarTemporizador(name: String, duration: Int) {
        val originalTimer = timerViewModel.timers.value?.find { it.id == editingTimerId }
        originalTimer?.let {
            val updatedTimer = Timer().apply {
                this.id = it.id
                this.name = name
                this.duration = duration
                this.isActive = it.isActive
                this.createdAt = it.createdAt
                this.uid = it.uid
            }
            timerViewModel.updateTimer(updatedTimer)
            Toast.makeText(requireContext(), "Temporizador actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoEliminar(timer: Timer) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar temporizador")
            .setMessage("¿Estás seguro?")
            .setPositiveButton("Sí") { _, _ ->
                timerViewModel.deleteTimer(timer.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupObservers() {
        timerViewModel.timers.observe(viewLifecycleOwner, Observer { timers ->
            // CORRECCIÓN: Cambiado submitList por updateData
            adapter.updateData(timers)
        })
    }

    private fun limpiarFormulario() {
        editingTimerId = null
        binding.editTextTimerName.text.clear()
        binding.editTextTimerDuration.text.clear()
        binding.tvFormTitle.text = "Nuevo Temporizador"
        binding.buttonCreateTimer.text = "CREAR"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        binding.recyclerViewTimers.adapter = null // Ayuda a evitar fugas de memoria y crashes en recreación
    }
}