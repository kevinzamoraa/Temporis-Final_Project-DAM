package com.kevinzamora.temporis_androidapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Simplemente lanzamos MainActivity (donde está el fragmento de noticias)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}