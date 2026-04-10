package com.kevinzamora.temporis_androidapp.ui.timers

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return try {
            _binding = FragmentTimersBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e("TimersCrash", "Error inflado: ${e.message}")
            View(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return

        applySavedFontScale()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupKeyboardDetection()
    }

    private fun setupKeyboardDetection() {
        val rootView = binding.mainTimersLayout
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            if (_binding == null) return@addOnGlobalLayoutListener

            val rect = android.graphics.Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val params = binding.cardForm.layoutParams as ViewGroup.MarginLayoutParams

            if (keypadHeight > screenHeight * 0.15) {
                // TECLADO ABIERTO
                binding.textTimersTitle.visibility = View.GONE
                params.bottomMargin = 0 // Pegado al teclado
            } else {
                // TECLADO CERRADO
                binding.textTimersTitle.visibility = View.VISIBLE
                params.bottomMargin = dpToPx(100) // Margen para el Bottom Nav
            }
            binding.cardForm.layoutParams = params
        }
    }

    // Función auxiliar para mantener la consistencia del margen en cualquier pantalla
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun applySavedFontScale() {
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val config = resources.configuration
        if (Math.abs(config.fontScale - savedFontSize) > 0.01f) {
            config.fontScale = savedFontSize
            requireContext().applicationContext.resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    private fun setupRecyclerView() {
        adapter = TimerAdapter(
            timerList = emptyList(),
            onPlayClick = { timer -> Toast.makeText(requireContext(), "Start: ${timer.name}", Toast.LENGTH_SHORT).show() },
            onEditClick = { timer -> prepararEdicion(timer) },
            onDeleteClick = { timer -> mostrarDialogoEliminar(timer) }
        )
        binding.recyclerViewTimers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTimers.adapter = adapter
    }

    private fun setupObservers() {
        timerViewModel.timers.observe(viewLifecycleOwner) { timers -> adapter.updateData(timers) }
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
                hideKeyboard()
            }
        }
    }

    private fun prepararEdicion(timer: Timer) {
        editingTimerId = timer.id
        binding.editTextTimerName.setText(timer.name)
        binding.editTextTimerDuration.setText(timer.duration.toString())
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
        }
    }

    private fun mostrarDialogoEliminar(timer: Timer) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.label_delete_button))
            .setMessage(getString(R.string.label_are_you_sure))
            .setPositiveButton(getString(R.string.label_yes)) { _, _ -> timerViewModel.deleteTimer(timer.id) }
            .setNegativeButton(getString(R.string.label_no), null)
            .show()
    }

    private fun limpiarFormulario() {
        editingTimerId = null
        binding.editTextTimerName.text.clear()
        binding.editTextTimerDuration.text.clear()
        binding.tvFormTitle.text = getString(R.string.title_create_timer)
        binding.buttonCreateTimer.text = getString(R.string.create_button)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}