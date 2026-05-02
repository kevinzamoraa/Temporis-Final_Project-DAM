package com.kevinzamora.temporis_androidapp.adapter

import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.kevinzamora.temporis_androidapp.model.Timer
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
// 1. Configuramos el SDK y el tema a nivel de clase para mayor estabilidad
@Config(sdk = [34])
class TimerAdapterTest {

    private lateinit var context: Context
    private lateinit var timerList: List<Timer>

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // 2. Aplicamos tu tema real al contexto antes de cada test
        // Esto soluciona el InflateException de CardView en la línea 11
        context.setTheme(com.kevinzamora.temporis_androidapp.R.style.Theme_TemporisAndroidApp)

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

        // Ahora el contexto del parent ya tiene el tema cargado en setUp()
        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        assertNotNull(viewHolder.binding)
    }

    @Test
    fun testOnBindViewHolder() {
        val adapter = TimerAdapter(timerList, { _ -> }, { _ -> }, { _ -> })
        val parent = FrameLayout(context)
        val viewHolder = adapter.onCreateViewHolder(parent, 0)

        adapter.onBindViewHolder(viewHolder, 0)

        assertEquals("Entrenar", viewHolder.binding.timerName.text.toString())
        assertEquals("30 min", viewHolder.binding.timerDuration.text.toString())
        assertNotNull(viewHolder.binding.textViewCountdown.text)
    }
}