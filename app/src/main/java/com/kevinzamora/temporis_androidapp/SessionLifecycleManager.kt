package com.kevinzamora.temporis_androidapp

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class SessionLifecycleManager : Application(), DefaultLifecycleObserver {

    companion object {
        var isChangingConfiguration: Boolean = false
    }

    override fun onCreate() {
        super<Application>.onCreate() // Corrección: super.onCreate() en lugar de super<Application> si no hay ambigüedad
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Se puede usar para registrar el inicio de la sesión visual
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)

        // Si la app va al background y no es por una navegación controlada (isChangingConfiguration),
        // cerramos sesión para obligar a pedir biometría al volver.
        if (!isChangingConfiguration) {
            // FirebaseAuth.getInstance().signOut()
            // Nota: Al volver a abrir la app, LoginActivity detectará currentUser == null
            // y lanzará la biometría automáticamente.
        }

        // Reset para la próxima acción
        isChangingConfiguration = false
    }
}