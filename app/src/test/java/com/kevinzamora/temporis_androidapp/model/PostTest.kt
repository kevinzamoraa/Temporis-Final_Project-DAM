package com.kevinzamora.temporis_androidapp.model

import com.google.firebase.Timestamp
import junit.framework.TestCase
import java.util.Date

class PostTest : TestCase() {

    private lateinit var post: Post
    private val testDate = Timestamp(Date())

    override fun setUp() {
        super.setUp()
        post = Post("id1", "Titulo", "Contenido", "img.url", "web.url", testDate)
    }

    fun testGetId() = assertEquals("id1", post.id)

    fun testSetId() {
        post.id = "nuevoId"
        assertEquals("nuevoId", post.id)
    }

    fun testGetTitle() = assertEquals("Titulo", post.title)

    fun testGetContent() = assertEquals("Contenido", post.content)

    fun testGetImageUrl() = assertEquals("img.url", post.imageUrl)

    fun testGetWebUrl() = assertEquals("web.url", post.webUrl)

    fun testGetCreatedAt() = assertEquals(testDate, post.createdAt)

    // Test de los componentes automáticos de la Data Class
    fun testComponent1() = assertEquals("id1", post.component1())
    fun testComponent2() = assertEquals("Titulo", post.component2())
    fun testComponent3() = assertEquals("Contenido", post.component3())
    fun testComponent4() = assertEquals("img.url", post.component4())
    fun testComponent5() = assertEquals("web.url", post.component5())
    fun testComponent6() = assertEquals(testDate, post.component6())

    fun testCopy() {
        val copy = post.copy(title = "Copia")
        assertEquals("Copia", copy.title)
        assertEquals(post.id, copy.id)
    }

    fun testToString() {
        val string = post.toString()
        assertTrue(string.contains("id1"))
        assertTrue(string.contains("Titulo"))
    }

    fun testHashCode() {
        val post2 = Post("id1", "Titulo", "Contenido", "img.url", "web.url", testDate)
        assertEquals(post.hashCode(), post2.hashCode())
    }

    fun testEquals() {
        val post2 = Post("id1", "Titulo", "Contenido", "img.url", "web.url", testDate)
        val post3 = post.copy(id = "otro")

        assertTrue(post == post2)
        assertFalse(post == post3)
    }
}