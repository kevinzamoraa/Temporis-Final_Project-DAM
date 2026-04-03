package com.kevinzamora.temporis_androidapp.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kevinzamora.temporis_androidapp.repository.TimerRepository

class StatisticsViewModel(private val repository: TimerRepository) : ViewModel() {

    private val _totalSessions = MutableLiveData<Int>()
    val totalSessions: LiveData<Int> = _totalSessions

    private val _totalMinutes = MutableLiveData<Int>()
    val totalMinutes: LiveData<Int> = _totalMinutes

    fun calculateStats() {
        // Obtenemos los temporizadores del repositorio
        repository.getTimers().observeForever { timers ->
            _totalSessions.value = timers.size
            _totalMinutes.value = timers.sumOf { it.duration }
        }
    }
}