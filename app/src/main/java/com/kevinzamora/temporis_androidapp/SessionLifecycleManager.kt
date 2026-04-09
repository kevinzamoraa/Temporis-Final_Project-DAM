package com.kevinzamora.temporis_androidapp

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth

class SessionLifecycleManager : Application(), DefaultLifecycleObserver {

    // Variable estática para saber si estamos cambiando de pantalla
    companion object {
        var isChangingConfiguration: Boolean = false
    }

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onStop(owner)

        // SOLO cerramos sesión si la app realmente va al fondo
        // y NO es un cambio de actividad interno
        if (!isChangingConfiguration) {
            FirebaseAuth.getInstance().signOut()
        }

        // Resetear el flag para la próxima vez
        isChangingConfiguration = false
    }
}