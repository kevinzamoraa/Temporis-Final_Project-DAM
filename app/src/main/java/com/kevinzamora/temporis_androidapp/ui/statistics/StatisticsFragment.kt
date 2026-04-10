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

import com.kevinzamora.temporis_androidapp.ui.statistics.StatisticsViewModel
import com.kevinzamora.temporis_androidapp.ui.statistics.StatisticsViewModelFactory

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

    private fun setupContributionChart() {
        val chart = binding.contributionChart

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setScaleEnabled(false)
        chart.setTouchEnabled(false)

        // EJE X: Días de la semana (7 columnas)
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = Color.GRAY
        xAxis.textSize = 10f
        xAxis.granularity = 1f
        xAxis.labelCount = 7

        xAxis.valueFormatter = object : ValueFormatter() {
            private val daysRes = intArrayOf(
                R.string.day_1, R.string.day_2, R.string.day_3,
                R.string.day_4, R.string.day_5, R.string.day_6, R.string.day_7
            )
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in daysRes.indices) getString(daysRes[index]) else ""
            }
        }

        // EJE Y IZQUIERDO: Los 12 meses (Filas apiladas)
        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.GRAY
        leftAxis.textSize = 9f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 12f
        leftAxis.setLabelCount(12, true)

        leftAxis.valueFormatter = object : ValueFormatter() {
            private val monthsRes = intArrayOf(
                R.string.month_1, R.string.month_2, R.string.month_3, R.string.month_4,
                R.string.month_5, R.string.month_6, R.string.month_7, R.string.month_8,
                R.string.month_9, R.string.month_10, R.string.month_11, R.string.month_12
            )
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in monthsRes.indices) getString(monthsRes[index]) else ""
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

        // Ahora iteramos: X = 7 días, Y = 12 niveles (meses)
        for (day in 0 until 7) {
            val monthlyValues = FloatArray(12)
            for (month in 0 until 12) {
                monthlyValues[month] = 1f // Altura uniforme para crear el "cuadrado"

                val activity = (0..3).random()
                when (activity) {
                    0 -> colors.add(colorEmpty)
                    1, 2 -> colors.add(colorLow)
                    else -> colors.add(colorHigh)
                }
            }
            // Creamos una barra por cada día de la semana, con 12 segmentos (meses)
            entries.add(BarEntry(day.toFloat(), monthlyValues))
        }

        val dataSet = BarDataSet(entries, "")
        dataSet.colors = colors
        dataSet.setDrawValues(false)
        dataSet.barBorderWidth = 1.5f
        dataSet.barBorderColor = Color.WHITE

        val barData = BarData(dataSet)
        barData.barWidth = 0.85f

        binding.contributionChart.data = barData
        // Ya no forzamos la altura por código, se usa la del XML (320dp)
        binding.contributionChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}