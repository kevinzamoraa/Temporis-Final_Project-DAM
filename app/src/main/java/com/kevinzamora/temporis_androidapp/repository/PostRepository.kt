package com.kevinzamora.temporis_androidapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kevinzamora.temporis_androidapp.model.Post

class PostRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsLiveData = MutableLiveData<List<Post>>()

    fun getPosts(): LiveData<List<Post>> {
        // Consultamos la colección "posts" ordenada por fecha
        firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PostRepository", "Error al obtener posts", error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.apply { id = doc.id }
                } ?: emptyList()

                postsLiveData.value = posts
            }
        return postsLiveData
    }
}