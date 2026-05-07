package com.kevinzamora.temporis_androidapp

import androidx.lifecycle.LifecycleOwner
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class SessionLifecycleManagerTest {

    private lateinit var manager: SessionLifecycleManager
    private lateinit var mockLifecycleOwner: LifecycleOwner

    @Before
    fun setUp() {
        manager = SessionLifecycleManager()
        mockLifecycleOwner = mock(LifecycleOwner::class.java)
        SessionLifecycleManager.isChangingConfiguration = false
    }

    @Test
    fun testConfigurationFlagResetOnStop() {
        // Simulamos que estamos cambiando de configuración (rotación)
        SessionLifecycleManager.isChangingConfiguration = true

        // Ejecutamos el stop
        manager.onStop(mockLifecycleOwner)

        // Verificamos que la bandera se resetee a false para la próxima vez
        assertFalse(SessionLifecycleManager.isChangingConfiguration)
    }

    @Test
    fun testIsChangingConfigurationInitialState() {
        assertFalse(SessionLifecycleManager.isChangingConfiguration)
    }
}