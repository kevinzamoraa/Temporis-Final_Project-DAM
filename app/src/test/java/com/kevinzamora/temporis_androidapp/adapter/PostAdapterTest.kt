package com.kevinzamora.temporis_androidapp.adapter

import junit.framework.TestCase
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.kevinzamora.temporis_androidapp.model.Post
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import junit.framework.TestCase.assertNotNull

@RunWith(RobolectricTestRunner::class)
class PostAdapterTest {

    /*override fun setUp() {
        super.setUp()
    }

    override fun tearDown() {
        super.tearDown()
    }*/

    fun testOnCreateViewHolder() {
    }

    fun testOnBindViewHolder() {
    }

    fun testGetItemCount() {
    }

    @Test
    fun testAdapterItemCount() {
        val posts = listOf(
            Post("1", "Post 1", "Contenido 1"),
            Post("2", "Post 2", "Contenido 2")
        )
        val adapter = PostAdapter(posts)
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun testAdapterFullLogic() {
        val posts = listOf(Post("1", "Titulo", "Contenido", "url", "web"))
        val adapter = PostAdapter(posts)

        val context = ApplicationProvider.getApplicationContext<Context>()
        val parent = FrameLayout(context)

        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        assertNotNull(viewHolder)

        adapter.onBindViewHolder(viewHolder, 0)
        assertEquals("Titulo", viewHolder.title.text)
    }

    @Test
    fun testUpdateData() {
        val adapter = PostAdapter(emptyList())
        adapter.updateData(listOf(Post("1", "Nuevo")))
        assertEquals(1, adapter.itemCount)
    }

}