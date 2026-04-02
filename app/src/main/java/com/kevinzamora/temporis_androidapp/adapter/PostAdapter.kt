package com.kevinzamora.temporis_androidapp.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.model.Post
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(private var posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textViewTitulo)
        val content: TextView = view.findViewById(R.id.textViewContenido)
        val date: TextView = view.findViewById(R.id.textViewFecha)
        val image: ImageView = view.findViewById(R.id.imageViewPost)
        val btnWeb: Button = view.findViewById(R.id.buttonWeb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.title.text = post.title
        holder.content.text = post.content

        // Cargar imagen con Coil
        if (post.imageUrl.isNotEmpty()) {
            holder.image.visibility = View.VISIBLE
            holder.image.load(post.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground) // Pon un icono por defecto aquí
            }
        } else {
            holder.image.visibility = View.GONE // Si no hay URL, ocultamos el hueco
        }

        if (post.webUrl.isNotEmpty()) {
            holder.btnWeb.visibility = View.VISIBLE
            holder.btnWeb.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.webUrl))
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.btnWeb.visibility = View.GONE
        }

        post.createdAt?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.date.text = sdf.format(it.toDate())
        }
    }

    override fun getItemCount() = posts.size

    fun updateData(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }
}