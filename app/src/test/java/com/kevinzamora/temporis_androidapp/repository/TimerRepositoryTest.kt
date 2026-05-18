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
import com.kevinzamora.temporis_androidapp.TestApplication
import com.kevinzamora.temporis_androidapp.model.Timer
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
class TimerRepositoryTest {

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

        every { mockFirestore.collection("timers") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockCollection.whereEqualTo("uid", any()) } returns mockQuery

        val mockUser = mockk<FirebaseUser>()
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        timerRepository = TimerRepository(mockFirestore, mockAuth)
    }

    @Test
    fun testGetTimers() {
        val mockRegistration = mockk<ListenerRegistration>()
        every { mockQuery.addSnapshotListener(any()) } returns mockRegistration

        timerRepository.getTimers()
        ShadowLooper.idleMainLooper()

        verify { mockFirestore.collection("timers") }
        verify { mockCollection.whereEqualTo("uid", "user123") }
        verify { mockQuery.addSnapshotListener(any()) }
    }

    @Test
    fun testGetTimers_UserNull() {
        every { mockAuth.currentUser } returns null

        val liveData = timerRepository.getTimers()
        ShadowLooper.idleMainLooper()

        assert(liveData.value?.isEmpty() == true)
        verify(exactly = 0) { mockFirestore.collection(any()) }
    }

    @Test
    fun testAddTimer() {
        // 1. Configuramos el mock del documento para que cuando el repositorio pida su ID, no lance excepción
        every { mockDocument.id } returns "timer_mock_id"

        // 2. Simulamos que la colección añade con éxito devolviendo la tarea del documento
        every { mockCollection.add(any()) } returns Tasks.forResult(mockDocument)

        // 3. Ejecutamos la acción del repositorio
        timerRepository.addTimer("Test Timer", 10, true)

        // 4. Vaciamos los hilos asíncronos del Looper
        ShadowLooper.idleMainLooper()

        // 5. Verificamos que se llamó al método de añadir en Firebase
        verify(exactly = 1) { mockCollection.add(any()) }
    }

    @Test
    fun testUpdateTimer() {
        val testTimer = Timer("id123", "Timer Update", 5, true, null, "user123")
        every { mockDocument.set(any()) } returns Tasks.forResult(null)

        timerRepository.updateTimer(testTimer)
        ShadowLooper.idleMainLooper()

        verify { mockCollection.document("id123") }
        verify { mockDocument.set(any()) }
    }

    @Test
    fun testDeleteTimer() {
        val timerId = "id_a_borrar"
        every { mockDocument.delete() } returns Tasks.forResult(null)

        timerRepository.deleteTimer(timerId)
        ShadowLooper.idleMainLooper()

        verify { mockCollection.document(timerId) }
        verify { mockDocument.delete() }
    }

    @Test
    fun testClearListener() {
        timerRepository.clearListener()
    }
}