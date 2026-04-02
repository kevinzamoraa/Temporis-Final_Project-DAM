package com.kevinzamora.temporis_androidapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kevinzamora.temporis_androidapp.adapter.PostAdapter // Asegúrate de que esta ruta es correcta
import com.kevinzamora.temporis_androidapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Inicializamos el ViewModel de forma delegada
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        // Inicializamos el adaptador con una lista vacía
        postAdapter = PostAdapter(emptyList())

        binding.recyclerPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun observeViewModel() {
        // Escuchamos los cambios en la lista de posts del ViewModel
        homeViewModel.posts.observe(viewLifecycleOwner) { listaDePosts ->
            postAdapter.updateData(listaDePosts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}