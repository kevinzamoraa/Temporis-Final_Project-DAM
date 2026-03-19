package com.kevinzamora.temporis_androidapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinzamora.temporis_androidapp.databinding.FragmentHomeBinding
import com.kevinzamora.temporis_androidapp.model.Post

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Lógica de Firebase aquí
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadPostsFromFirestore()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerPosts.layoutManager = LinearLayoutManager(requireContext())
        // Aquí asignamos el adaptador, cuando lo tenemos listo
    }

    private fun loadPostsFromFirestore() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                val postList = mutableListOf<Post>()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    post.id = document.id
                    postList.add(post)
                }
                // Aquí actualizamos el adaptador con la lista postList
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}