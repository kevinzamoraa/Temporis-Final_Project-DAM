package com.kevinzamora.temporis_androidapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.kevinzamora.temporis_androidapp.model.Timer
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerRepositoryTest {

    // Regla necesaria para testear LiveData
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var timerRepository: TimerRepository

    @MockK
    lateinit var mockFirestore: FirebaseFirestore
    @MockK
    lateinit var mockAuth: FirebaseAuth
    @MockK
    lateinit var mockCollection: CollectionReference
    @MockK
    lateinit var mockDocument: DocumentReference
    @MockK
    lateinit var mockQuery: Query

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // Configuración básica de mocks
        every { mockFirestore.collection("timers") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockCollection.whereEqualTo("uid", any()) } returns mockQuery

        // Simulamos usuario logueado
        val mockUser = mockk<FirebaseUser>()
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        timerRepository = TimerRepository(mockFirestore, mockAuth)
    }

    @Test
    fun testGetTimers() {
        // 1. Preparamos el mock del listener
        val mockRegistration = mockk<ListenerRegistration>()
        // Usamos any() en el listener para que coincida con cualquier lambda
        every { mockQuery.addSnapshotListener(any()) } returns mockRegistration

        // 2. Ejecutamos el método
        timerRepository.getTimers()

        // 3. VERIFICACIÓN PASO A PASO:
        // Verificamos que se accede a la colección correcta
        verify { mockFirestore.collection("timers") }

        // Verificamos que se aplica el filtro por UID
        verify { mockCollection.whereEqualTo("uid", "user123") }

        // Verificamos que finalmente se añade el listener
        verify { mockQuery.addSnapshotListener(any()) }
    }

    @Test
    fun testGetTimers_UserNull() {
        // Simulamos que no hay nadie logueado
        every { mockAuth.currentUser } returns null

        val liveData = timerRepository.getTimers()

        // Verificamos que devuelve una lista vacía y no intenta llamar a Firestore
        assert(liveData.value?.isEmpty() == true)
        verify(exactly = 0) { mockFirestore.collection(any()) }
    }

    @Test
    fun testAddTimer() {
        // Configuramos la tarea exitosa para .add()
        every { mockCollection.add(any()) } returns Tasks.forResult(mockDocument)

        timerRepository.addTimer("Test Timer", 10, true)

        // Verificamos que se llamó a add en la colección "timers"
        verify { mockCollection.add(any()) }
    }

    @Test
    fun testUpdateTimer() {
        val testTimer = Timer("id123", "Timer Update", 5, true, null, "user123")

        every { mockDocument.set(any()) } returns Tasks.forResult(null)

        timerRepository.updateTimer(testTimer)

        // Verificamos que se use el ID del timer para buscar el documento
        verify { mockCollection.document("id123") }
        verify { mockDocument.set(any()) }
    }

    @Test
    fun testDeleteTimer() {
        val timerId = "id_a_borrar"
        every { mockDocument.delete() } returns Tasks.forResult(null)

        timerRepository.deleteTimer(timerId)

        verify { mockCollection.document(timerId) }
        verify { mockDocument.delete() }
    }

    @Test
    fun testClearListener() {
        // Para este test no necesitamos mocks complejos, solo verificar que no explote
        timerRepository.clearListener()
    }
}