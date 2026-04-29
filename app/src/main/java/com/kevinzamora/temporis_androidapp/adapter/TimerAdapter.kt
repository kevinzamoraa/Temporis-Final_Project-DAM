package com.kevinzamora.temporis_androidapp.adapter

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kevinzamora.temporis_androidapp.databinding.ItemTimerBinding
import com.kevinzamora.temporis_androidapp.model.Timer
import java.text.SimpleDateFormat
import java.util.*

class TimerAdapter(
    private var timerList: List<Timer>,
    private val onPlayClick: (Timer) -> Unit,
    private val onEditClick: (Timer) -> Unit,
    private val onDeleteClick: (Timer) -> Unit
) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    private val activeTimers = mutableMapOf<String, CountDownTimer>()
    private val remainingTimes = mutableMapOf<String, Long>()
    private val isPaused = mutableMapOf<String, Boolean>()

    inner class TimerViewHolder(val binding: ItemTimerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timer = timerList[position]
        val timerId = timer.id ?: ""

        with(holder.binding) {
            timerName.text = timer.name
            timerDuration.text = "${timer.duration} min"

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            timerCreatedAt.text = timer.createdAt?.let { sdf.format(it.toDate()) } ?: ""

            val remaining = remainingTimes[timerId] ?: (timer.duration * 60 * 1000L)
            updateCountdownText(holder, remaining)

            // Actualizar iconos según el estado actual (importante al hacer scroll)
            val isRunning = activeTimers.containsKey(timerId) && isPaused[timerId] == false
            val icon = if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            btnPlay.setImageResource(icon)
            btnQuickPlay.setImageResource(icon)

            ivExpand.setOnClickListener {
                val isVisible = layoutActions.visibility == View.VISIBLE
                layoutActions.visibility = if (isVisible) View.GONE else View.VISIBLE
                ivExpand.animate().rotation(if (isVisible) 0f else 180f).setDuration(300).start()
            }

            // --- UNIFICACIÓN DE BOTONES ---
            btnPlay.setOnClickListener { handlePlayPause(timer, holder) }
            btnQuickPlay.setOnClickListener { handlePlayPause(timer, holder) }
            // ------------------------------

            btnEdit.setOnClickListener { onEditClick(timer) }
            btnDelete.setOnClickListener { onDeleteClick(timer) }
        }
    }

    // Función centralizada para manejar la reproducción/pausa
    private fun handlePlayPause(timer: Timer, holder: TimerViewHolder) {
        val timerId = timer.id ?: ""

        if (activeTimers.containsKey(timerId)) {
            if (isPaused[timerId] == true) {
                startCountdown(timer, holder, remainingTimes[timerId] ?: 0L)
                isPaused[timerId] = false
                updateActionIcons(holder, true)
            } else {
                activeTimers[timerId]?.cancel()
                isPaused[timerId] = true
                updateActionIcons(holder, false)
            }
        } else {
            startCountdown(timer, holder, timer.duration * 60 * 1000L)
            isPaused[timerId] = false
            updateActionIcons(holder, true)
        }
        onPlayClick(timer)
    }

    // Función para cambiar los iconos de ambos botones a la vez
    private fun updateActionIcons(holder: TimerViewHolder, running: Boolean) {
        val icon = if (running) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        holder.binding.btnPlay.setImageResource(icon)
        holder.binding.btnQuickPlay.setImageResource(icon)
    }

    private fun startCountdown(timer: Timer, holder: TimerViewHolder, duration: Long) {
        val timerId = timer.id ?: return
        activeTimers[timerId]?.cancel()

        val countdown = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimes[timerId] = millisUntilFinished
                updateCountdownText(holder, millisUntilFinished)
            }

            override fun onFinish() {
                updateCountdownText(holder, 0)
                activeTimers.remove(timerId)
                isPaused[timerId] = false
                updateActionIcons(holder, false)
            }
        }
        activeTimers[timerId] = countdown
        countdown.start()
    }

    private fun updateCountdownText(holder: TimerViewHolder, millis: Long) {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        holder.binding.textViewCountdown.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun getItemCount(): Int = timerList.size

    fun updateData(newList: List<Timer>) {
        this.timerList = newList
        notifyDataSetChanged()
    }
}