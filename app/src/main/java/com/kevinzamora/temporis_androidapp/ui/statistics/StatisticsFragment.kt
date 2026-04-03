package com.kevinzamora.temporis_androidapp.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
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

        val repository = TimerRepository()
        val factory = StatisticsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StatisticsViewModel::class.java)

        setupObservers()
        setupContributionChart()

        viewModel.calculateStats()
    }

    private fun setupObservers() {
        viewModel.totalSessions.observe(viewLifecycleOwner) { sessions ->
            binding.tvTotalSessionsCount.text = sessions.toString()
        }

        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.tvTotalTimeMinutes.text = "$minutes min"
        }
    }

    // --- LÓGICA DEL GRÁFICO TIPO TOGGL ---

    private fun setupContributionChart() {
        val chart = binding.contributionChart

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.isHighlightFullBarEnabled = false
        chart.setScaleEnabled(false) // Desactivar zoom para mantener la forma de rejilla
        chart.setTouchEnabled(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Cambiado a BOTTOM para mejor alineación
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = Color.parseColor("#888888")
        xAxis.textSize = 10f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val months = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago")
            override fun getFormattedValue(value: Float): String {
                return months.getOrElse(value.toInt()) { "" }
            }
        }

        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.parseColor("#888888")
        leftAxis.textSize = 10f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 7f // Forzamos 7 espacios para los días de la semana
        leftAxis.setLabelCount(7, true)
        leftAxis.valueFormatter = object : ValueFormatter() {
            private val days = arrayOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            override fun getFormattedValue(value: Float): String {
                return days.getOrElse(value.toInt()) { "" }
            }
        }
        chart.axisRight.isEnabled = false

        setChartData()
    }

    private fun setChartData() {
        val entries = mutableListOf<BarEntry>()
        val colors = mutableListOf<Int>()

        val colorEmpty = Color.parseColor("#EEEEEE")
        val colorLow = ContextCompat.getColor(requireContext(), R.color.purple_200)
        val colorHigh = ContextCompat.getColor(requireContext(), R.color.purple_500)

        // Dibujaremos 8 semanas (columnas)
        for (week in 0 until 8) {
            val dailyValues = FloatArray(7)
            for (day in 0 until 7) {
                // Cada "día" tiene una altura de 1f para que todos los cuadrados sean iguales
                dailyValues[day] = 1f

                val activity = (0..3).random()
                when (activity) {
                    0 -> colors.add(colorEmpty)
                    1, 2 -> colors.add(colorLow)
                    else -> colors.add(colorHigh)
                }
            }
            // Añadimos la columna de la semana con 7 bloques de altura 1
            entries.add(BarEntry(week.toFloat(), dailyValues))
        }

        val dataSet = BarDataSet(entries, "Actividad")
        dataSet.colors = colors
        dataSet.setDrawValues(false)

        // ESTO CREA EL EFECTO DE CUADRADITOS:
        // Añade un borde blanco grueso alrededor de cada segmento apilado
        dataSet.barBorderWidth = 2f
        dataSet.barBorderColor = Color.WHITE

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f // Espacio entre columnas (semanas)

        binding.contributionChart.data = barData
        binding.contributionChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}