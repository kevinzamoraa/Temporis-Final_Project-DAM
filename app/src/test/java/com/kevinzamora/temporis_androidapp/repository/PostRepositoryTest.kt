package com.kevinzamora.temporis_androidapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kevinzamora.temporis_androidapp.TestApplication
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

// CORREGIDO: Añadimos el Runner de Robolectric para soportar las llamadas asíncronas
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class, sdk = [33])
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
        every { mockCollection.orderBy("createdAt", Query.Direction.DESCENDING) } returns mockQuery

        postRepository = PostRepository(mockFirestore)
    }

    @Test
    fun testGetPosts() {
        val mockRegistration = mockk<com.google.firebase.firestore.ListenerRegistration>()
        every { mockQuery.addSnapshotListener(any()) } returns mockRegistration

        postRepository.getPosts()
        ShadowLooper.idleMainLooper()

        verify { mockFirestore.collection("posts") }
        verify { mockCollection.orderBy("createdAt", Query.Direction.DESCENDING) }
        verify { mockQuery.addSnapshotListener(any()) }
    }

    @Test
    fun testGetPosts_Error() {
        every { mockQuery.addSnapshotListener(any()) } answers {
            val callback = firstArg<com.google.firebase.firestore.EventListener<com.google.firebase.firestore.QuerySnapshot>>()
            callback.onEvent(null, mockk(relaxed = true))
            mockk()
        }

        postRepository.getPosts()
        ShadowLooper.idleMainLooper()

        verify { mockQuery.addSnapshotListener(any()) }
    }
}