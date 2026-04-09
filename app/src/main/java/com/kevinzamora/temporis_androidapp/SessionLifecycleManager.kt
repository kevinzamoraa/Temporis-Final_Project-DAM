package com.kevinzamora.temporis_androidapp

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class SessionLifecycleManager : Application(), DefaultLifecycleObserver {

    companion object {
        // Restauramos esta variable para que LoginActivity pueda usarla
        var isChangingConfiguration: Boolean = false
    }

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        // Versión actualizada de App Check (sin .ktx)
        val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }

    override fun onStop(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onStop(owner)

        // Solo cerramos sesión si NO estamos cambiando de pantalla internamente
        if (!isChangingConfiguration) {
            // FirebaseAuth.getInstance().signOut() // Descomentar cuando SHA-1 esté listo
        }
        isChangingConfiguration = false
    }
}