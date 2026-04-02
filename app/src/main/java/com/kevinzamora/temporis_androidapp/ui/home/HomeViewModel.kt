package com.kevinzamora.temporis_androidapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kevinzamora.temporis_androidapp.model.Post
import com.kevinzamora.temporis_androidapp.repository.PostRepository

class HomeViewModel : ViewModel() {

    private val repository = PostRepository()

    // Obtenemos los posts directamente del repositorio
    val posts: LiveData<List<Post>> = repository.getPosts()
}