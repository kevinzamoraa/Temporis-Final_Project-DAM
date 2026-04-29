package com.kevinzamora.temporis_androidapp.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinzamora.temporis_androidapp.model.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @MockK
    lateinit var mockFirestore: FirebaseFirestore
    @MockK
    lateinit var mockAuth: FirebaseAuth
    @MockK
    lateinit var mockCollection: CollectionReference
    @MockK
    lateinit var mockDocument: DocumentReference

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        userRepository = UserRepository(mockFirestore, mockAuth)
    }

    private fun <T> completedTask(result: T): Task<T> = Tasks.forResult(result)

    @Test
    fun testGetUser() = runTest {
        val testUid = "user123"
        val mockSnapshot = mockk<DocumentSnapshot>()
        every { mockSnapshot.toObject(User::class.java) } returns User(testUid, "test", "test@test.com", "Test", "")
        every { mockDocument.get() } returns completedTask(mockSnapshot)

        // CAMBIO: Usamos toList() para recolectar todas las emisiones sin abortar el flujo
        val results = userRepository.getUser(testUid).toList()

        assertTrue(results.first().isSuccess)
        verify { mockCollection.document(testUid) }
    }

    @Test
    fun testSaveUser() = runTest {
        val testUser = User("123", "kevin", "kevin@test.com", "Kevin Z", "url")
        every { mockDocument.set(any(), any()) } returns completedTask(null)

        val results = userRepository.saveUser(testUser).toList()

        assertTrue(results.first().isSuccess)
        verify { mockDocument.set(testUser, any()) }
    }

    @Test
    fun testRegisterUserInFirestore() = runTest {
        val testUser = User("456", "nuevo", "nuevo@test.com", "Nuevo", "url")
        every { mockDocument.set(any()) } returns completedTask(null)

        val results = userRepository.registerUserInFirestore(testUser).toList()

        assertTrue(results.first().isSuccess)
        verify { mockDocument.set(testUser) }
    }

    @Test
    fun testDeleteFullAccount() = runTest {
        val mockFirebaseUser = mockk<FirebaseUser>()
        every { mockAuth.currentUser } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "123"
        every { mockDocument.delete() } returns completedTask(null)
        every { mockFirebaseUser.delete() } returns completedTask(null)

        val result = userRepository.deleteFullAccount()

        assertTrue(result.isSuccess)
        verify { mockDocument.delete() }
        verify { mockFirebaseUser.delete() }
    }
}