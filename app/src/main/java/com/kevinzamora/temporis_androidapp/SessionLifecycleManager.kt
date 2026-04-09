package com.kevinzamora.temporis_androidapp

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth

/**
 * Clase encargada de supervisar el ciclo de vida de la aplicación.
 * Implementa un cierre de sesión automático cuando la app deja de estar en primer plano.
 */
class SessionLifecycleManager : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        // Especificamos el supertipo Application
        super<Application>.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        // En DefaultLifecycleObserver, onStop no requiere llamar a super obligatoriamente,
        // pero si lo haces, especifica el supertipo si el IDE te lo pide:
        super<DefaultLifecycleObserver>.onStop(owner)

        FirebaseAuth.getInstance().signOut()
    }
}