package com.kevinzamora.temporis_androidapp.ui.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentStatisticsBinding
import com.kevinzamora.temporis_androidapp.repository.TimerRepository

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatisticsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatisticsBinding.bind(view)

        // 1. Inicialización correcta con Factory
        val repository = TimerRepository()
        val factory = StatisticsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StatisticsViewModel::class.java)

        // 2. Configurar los observadores
        setupObservers()

        // 3. Cargar los datos iniciales
        viewModel.calculateStats()
    }

    private fun setupObservers() {
        // Observamos el total de sesiones
        viewModel.totalSessions.observe(viewLifecycleOwner) { sessions ->
            binding.tvTotalSessionsCount.text = sessions.toString()
        }

        // Observamos el tiempo total
        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.tvTotalTimeMinutes.text = "$minutes min"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

