package com.kevinzamora.temporis_androidapp.ui.appinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kevinzamora.temporis_androidapp.R
import com.kevinzamora.temporis_androidapp.databinding.FragmentAppInfoBinding

class AppInfoFragment : Fragment(R.layout.fragment_app_info) {

    private var _binding: FragmentAppInfoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAppInfoBinding.bind(view)

        binding.btnGithub.setOnClickListener {
            openUrl("https://github.com/kevinzamoraa")
        }

        binding.btnLinkedin.setOnClickListener {
            openUrl("https://www.linkedin.com/in/tu-perfil") // Sustituye por el tuyo
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}