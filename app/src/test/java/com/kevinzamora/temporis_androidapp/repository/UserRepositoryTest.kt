package com.kevinzamora.temporis_androidapp.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinzamora.temporis_androidapp.model.User
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    // El repositorio que vamos a testear
    private lateinit var userRepository: UserRepository

    // Creamos los "dobles" (mocks) de las clases de Firebase
    @MockK
    lateinit var mockFirestore: FirebaseFirestore

    @MockK
    lateinit var mockCollection: CollectionReference

    @MockK
    lateinit var mockDocument: DocumentReference

    @Before
    fun setUp() {
        // Inicializa las anotaciones @MockK
        MockKAnnotations.init(this)

        // IMPORTANTE: Para que el test funcione, inyectamos el mock en el repositorio.
        // Si tu UserRepository no acepta parámetros, asegúrate de que la variable db sea accesible.
        userRepository = UserRepository(mockFirestore)

        // Configuración común de los mocks
        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
    }

    @Test
    fun testGetUser() {
        val testUid = "user123"

        // Ejecutamos el método del repositorio
        userRepository.getUser(testUid)

        // Verificamos que se llamó a la colección "users" y al documento con el UID correcto
        verify { mockFirestore.collection("users").document(testUid) }
        verify { mockDocument.get() }
    }

    @Test
    fun testSaveUser() {
        val testUser = User("123", "kevin", "kevin@test.com", "Kevin Z", "photo_url")
        val mockTask = mockk<Task<Void>>()

        // Definimos que al llamar a set() con cualquier objeto, devuelva una tarea mockeada
        every { mockDocument.set(any()) } returns mockTask

        // Ejecutamos
        userRepository.saveUser(testUser)

        // Verificamos que se llamó a set con el objeto usuario correcto
        verify { mockDocument.set(testUser) }
    }

    @Test
    fun testRegisterUserInFirestore() {
        val testUser = User("456", "nuevo", "nuevo@test.com", "Nuevo Usuario", "url")
        val mockTask = mockk<Task<Void>>()

        every { mockDocument.set(any(), any()) } returns mockTask

        // Supongamos que registerUser usa set() con MergeOptions
        userRepository.registerUserInFirestore(testUser)

        // Verificamos que se intenta escribir en el documento del usuario
        verify { mockCollection.document(testUser.uid) }
        verify { mockDocument.set(testUser, any()) }
    }

    @Test
    fun testGetUserProfileCallsCorrectDocument() {
        // Este es un alias de testGetUser si tu repo usa ambos nombres
        val testUid = "user789"
        userRepository.getUser(testUid)
        verify { mockFirestore.collection("users").document(testUid) }
    }
}