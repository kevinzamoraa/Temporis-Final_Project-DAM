package com.kevinzamora.temporis_androidapp.repository
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var postRepository: PostRepository

    @MockK
    lateinit var mockFirestore: FirebaseFirestore
    @MockK
    lateinit var mockCollection: CollectionReference
    @MockK
    lateinit var mockQuery: Query

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockFirestore.collection("posts") } returns mockCollection
        // Mock de la ordenación
        every { mockCollection.orderBy("createdAt", Query.Direction.DESCENDING) } returns mockQuery

        postRepository = PostRepository(mockFirestore)
    }

    @Test
    fun testGetPosts() {
        val mockRegistration = mockk<com.google.firebase.firestore.ListenerRegistration>()
        every { mockQuery.addSnapshotListener(any()) } returns mockRegistration

        postRepository.getPosts()

        // Verificamos por partes para evitar errores de cadena de MockK
        verify { mockFirestore.collection("posts") }
        verify { mockCollection.orderBy("createdAt", Query.Direction.DESCENDING) }
        verify { mockQuery.addSnapshotListener(any()) }
    }

    @Test
    fun testGetPosts_Error() {
        // 1. Simulamos que el listener recibe un error en lugar de datos
        every { mockQuery.addSnapshotListener(any()) } answers {
            val callback = firstArg<com.google.firebase.firestore.EventListener<com.google.firebase.firestore.QuerySnapshot>>()
            // Simulamos un error de Firebase
            callback.onEvent(null, mockk(relaxed = true))
            mockk()
        }

        val result = postRepository.getPosts()

        // Verificamos que la app no explote (puedes añadir asserts de LiveData aquí)
        verify { mockQuery.addSnapshotListener(any()) }
    }
}