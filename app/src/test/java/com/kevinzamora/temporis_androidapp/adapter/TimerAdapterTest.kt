package com.kevinzamora.temporis_androidapp.adapter

import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp // Añadir este import
import com.kevinzamora.temporis_androidapp.model.Timer
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerAdapterTest {

    private lateinit var context: Context
    private lateinit var timerList: List<Timer>

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // SOLUCIÓN AL ERROR DE LOG: Inicializamos Firebase para el entorno de test
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        timerList = listOf(
            Timer("1", "Entrenar", 30, true, Timestamp.now(), "user123"),
            Timer("2", "Leer", 15, false, Timestamp.now(), "user123")
        )
    }

    @Test
    fun testGetItemCount() {
        // Corregido: Especificamos el tipo de los lambdas para que Kotlin no se pierda
        val adapter = TimerAdapter(timerList, { _: Timer -> }, { _: Timer -> }, { _: Timer -> })
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun testUpdateData() {
        val adapter = TimerAdapter(emptyList(), { _ -> }, { _ -> }, { _ -> })
        val newList = listOf(Timer("3", "Nuevo", 10, true, Timestamp.now(), "user123"))
        adapter.updateData(newList)
        assertEquals(1, adapter.itemCount)
    }

    @Test
    fun testOnCreateViewHolder() {
        val adapter = TimerAdapter(timerList, { _ -> }, { _ -> }, { _ -> })
        val parent = FrameLayout(context)
        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        assertNotNull(viewHolder.binding)
    }

    @Test
    fun testOnBindViewHolder() {
        // Usamos el timer de la lista inicializada en setUp que ya tiene ID "1"
        val adapter = TimerAdapter(timerList, { _ -> }, { _ -> }, { _ -> })
        val context = ApplicationProvider.getApplicationContext<Context>()
        val parent = FrameLayout(context)
        val viewHolder = adapter.onCreateViewHolder(parent, 0)

        adapter.onBindViewHolder(viewHolder, 0)

        // Verificamos que los textos se asignaron correctamente
        assertEquals("Entrenar", viewHolder.binding.timerName.text.toString())
        assertEquals("30 min", viewHolder.binding.timerDuration.text.toString())

        // Verificamos que el contador se inicializó (00:00 o el tiempo restante)
        assertNotNull(viewHolder.binding.textViewCountdown.text)
    }
}