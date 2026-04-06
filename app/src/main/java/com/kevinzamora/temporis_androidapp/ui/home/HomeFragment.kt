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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return try {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e("HomeCrash", "Error inflado: ${e.message}")
            View(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return
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