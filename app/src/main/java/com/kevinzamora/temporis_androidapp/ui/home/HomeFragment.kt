package com.kevinzamora.temporis_androidapp.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevinzamora.temporis_androidapp.adapter.PostAdapter
import com.kevinzamora.temporis_androidapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1. Blindaje de inflado para evitar cierres súbitos por Modo Oscuro o Recursos
        return try {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e("HomeCrash", "Error crítico inflando Inicio: ${e.message}")
            // Retornamos una vista básica de emergencia para que la app no se cierre
            View(context ?: requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verificamos si el binding se inicializó correctamente antes de seguir
        if (_binding == null) return

        // 2. Persistencia de la fuente: Forzamos la escala guardada en este fragmento
        applySavedFontScale()

        setupRecyclerView()
        observeViewModel()
    }

    private fun applySavedFontScale() {
        try {
            val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)

            val config = resources.configuration
            // Solo actualizamos si hay una diferencia para evitar refrescos innecesarios
            if (config.fontScale != savedFontSize) {
                config.fontScale = savedFontSize
                val metrics = resources.displayMetrics
                @Suppress("DEPRECATION")
                resources.updateConfiguration(config, metrics)
            }
        } catch (e: Exception) {
            Log.e("FontError", "No se pudo aplicar el tamaño de fuente en Inicio: ${e.message}")
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList())
        binding.recyclerPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.posts.observe(viewLifecycleOwner) { listaDePosts ->
            postAdapter.updateData(listaDePosts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}