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
import com.kevinzamora.temporis_androidapp.R
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
        adapter = TimerAdapter(
            timerList = emptyList(),
            onPlayClick = { timer ->
                Toast.makeText(requireContext(), "Iniciando: ${timer.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { timer -> prepararEdicion(timer) },
            onDeleteClick = { timer -> mostrarDialogoEliminar(timer) }
        )
        binding.recyclerViewTimers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimers.adapter = adapter
    }

    private fun setupObservers() {
        // Usar viewLifecycleOwner asegura que el observador muera con la vista del Fragment
        timerViewModel.timers.observe(viewLifecycleOwner) { timers ->
            adapter.updateData(timers)
        }
    }

    private fun setupClickListeners() {
        binding.buttonCreateTimer.setOnClickListener {
            val name = binding.editTextTimerName.text.toString().trim()
            val duration = binding.editTextTimerDuration.text.toString().toIntOrNull()

            if (name.isNotEmpty() && duration != null) {
                if (editingTimerId == null) {
                    timerViewModel.addTimer(name, duration)
                    Toast.makeText(requireContext(), getString(R.string.toast_created), Toast.LENGTH_SHORT).show()
                } else {
                    actualizarTemporizador(name, duration)
                }
                limpiarFormulario()
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun prepararEdicion(timer: Timer) {
        editingTimerId = timer.id
        binding.editTextTimerName.setText(timer.name)
        binding.editTextTimerDuration.setText(timer.duration.toString())

        // CORRECCIÓN: Usar getString(R.string...) para asignar textos correctamente
        binding.tvFormTitle.text = getString(R.string.title_edit_timer)
        binding.buttonCreateTimer.text = getString(R.string.update_button)
        binding.editTextTimerName.requestFocus()
    }

    private fun actualizarTemporizador(name: String, duration: Int) {
        val originalTimer = timerViewModel.timers.value?.find { it.id == editingTimerId }
        originalTimer?.let {
            val updatedTimer = Timer().apply {
                id = it.id
                this.name = name
                this.duration = duration
                this.setActive(it.isActive)
                this.setCreatedAt(it.createdAt)
                this.setUid(it.uid)
            }
            timerViewModel.updateTimer(updatedTimer)
            Toast.makeText(requireContext(), getString(R.string.update_button), Toast.LENGTH_SHORT).show()
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

    private fun limpiarFormulario() {
        editingTimerId = null
        binding.editTextTimerName.text.clear()
        binding.editTextTimerDuration.text.clear()
        // CORRECCIÓN: Usar getString para resetear el formulario
        binding.tvFormTitle.text = getString(R.string.title_create_timer)
        binding.buttonCreateTimer.text = getString(R.string.create_button)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Importante: desconectar el adapter para liberar memoria
        binding.recyclerViewTimers.adapter = null
        _binding = null
    }
}