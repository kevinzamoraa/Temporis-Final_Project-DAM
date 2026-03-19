package com.kevinzamora.temporis_androidapp.model

import com.google.firebase.Timestamp

data class Post(
    var id: String? = null,
    val title: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val createdAt: Timestamp? = null
)