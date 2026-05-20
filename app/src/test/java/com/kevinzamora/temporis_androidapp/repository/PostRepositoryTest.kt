package com.kevinzamora.temporis_androidapp.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.SQLiteMode

@SQLiteMode(SQLiteMode.Mode.LEGACY)
class PostRepositoryTest {

    // 1. Declaramos los mocks de Firebase
    private val mockFirestore: FirebaseFirestore = mockk(relaxed = true)
    private val mockCollection: CollectionReference = mockk(relaxed = true)
    private val mockTask: Task<QuerySnapshot> = mockk(relaxed = true)
    private val mockSnapshot: QuerySnapshot = mockk(relaxed = true)

    private lateinit var postRepository: PostRepository

    @Before
    fun setUp() {
        // 2. Encadenamos el comportamiento para evitar que toque el SDK real
        every { mockFirestore.collection("posts") } returns mockCollection
        every { mockCollection.get() } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns mockSnapshot
        every { mockSnapshot.documents } returns listOf() // Devolvemos una lista vacía simulada

        // 3. Inicializamos el repositorio inyectando el mock
        postRepository = PostRepository(mockFirestore)
    }

    @Test
    fun testGetPosts() {
        // 4. Ejecutamos el método del repositorio
        val result = postRepository.getPosts()

        // 5. Verificamos que no sea nulo y responda correctamente de forma instantánea
        assertNotNull(result)
    }
}