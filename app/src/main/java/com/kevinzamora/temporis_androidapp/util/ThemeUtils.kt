package com.kevinzamora.temporis_androidapp.util

import android.content.Context
import android.os.Build

class ThemeUtils {
    companion object {
        fun applyAppSettings(context: Context) {
            val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

            val config = context.resources.configuration

            // 1. Tamaño de fuente
            val fontScale = prefs.getFloat("font_size_scale", 1.0f)
            config.fontScale = fontScale

            // 2. Negrita (Solo para Android 12+)
            val isBold = prefs.getBoolean("bold_text", false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                config.fontWeightAdjustment = if (isBold) 300 else 0
            }

            // 3. Idioma
            val lang = prefs.getString("language", "es") ?: "es"
            val locale = java.util.Locale(lang)
            java.util.Locale.setDefault(locale)
            config.setLocale(locale)

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}