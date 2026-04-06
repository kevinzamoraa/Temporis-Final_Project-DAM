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
            Log.e("HomeCrash", "Error inflado Modo Oscuro: ${e.message}")
            View(requireContext()) // Vista de emergencia
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null) return

        applySavedFontScale() // Asegura que la fuente se mantenga
        setupRecyclerView()
        observeViewModel()
    }

    private fun applySavedFontScale() {
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val savedFontSize = sharedPref.getFloat("font_size_scale", 1.0f)
        val config = resources.configuration

        // Solo aplicar si la diferencia es significativa para evitar bucles de refresco
        if (Math.abs(config.fontScale - savedFontSize) > 0.01f) {
            config.fontScale = savedFontSize
            val metrics = resources.displayMetrics
            // Usamos el contexto de la aplicación para que sea más estable
            requireContext().applicationContext.resources.updateConfiguration(config, metrics)
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(emptyList())
        binding.recyclerPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPosts.adapter = postAdapter
    }

    private fun observeViewModel() {
        homeViewModel.posts.observe(viewLifecycleOwner) { postAdapter.updateData(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}