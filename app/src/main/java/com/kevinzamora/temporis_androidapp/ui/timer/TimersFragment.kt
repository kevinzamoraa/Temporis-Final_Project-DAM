package com.kevinzamora.temporis_androidapp.ui.timer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.adapter.TimerAdapter
import com.kevinzamora.temporis_androidapp.databinding.FragmentTimersBinding
import com.kevinzamora.temporis_androidapp.model.Timer

class TimersFragment : Fragment(R.layout.fragment_timers) {

    private var _binding: FragmentTimersBinding? = null
    private val binding get() = _binding!!

    private lateinit var timerAdapter: TimerAdapter
    private var timerParaEditar: Timer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTimersBinding.bind(view)

        setupRecyclerView()

        binding.buttonCreateTimer.setOnClickListener {
            if (timerParaEditar == null) {
                // Lógica de creación
            } else {
                // Lógica de actualización
            }
        }
    }

    private fun setupRecyclerView() {
        // CORRECCIÓN: Ahora pasamos 4 parámetros
        timerAdapter = TimerAdapter(
            timerList = emptyList(),
            onPlayClick = { timer ->
            },
            onEditClick = { timer ->
                prepararEdicion(timer)
            },
            onDeleteClick = { timer ->
            }
        )

        binding.recyclerViewTimers.apply {
            adapter = timerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun prepararEdicion(timer: Timer) {
        timerParaEditar = timer
        binding.tvFormTitle.text = "Actualizar Temporizador"
        binding.buttonCreateTimer.text = "ACTUALIZAR"
        binding.editTextTimerName.setText(timer.name)
        binding.editTextTimerDuration.setText(timer.duration.toString())
        binding.editTextTimerName.requestFocus()
    }
}