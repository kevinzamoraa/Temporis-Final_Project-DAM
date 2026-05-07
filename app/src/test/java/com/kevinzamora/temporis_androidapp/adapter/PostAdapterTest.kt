package com.kevinzamora.temporis_androidapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.kevinzamora.temporis_androidapp.model.Post
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// 1. Forzamos el SDK a 33 para evitar el "VerifyError" de bytecode en versiones superiores
// 2. Aplicamos un tema de MaterialComponents para que el inflado de vistas no falle
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PostAdapterTest {

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

        // 1. Obtenemos el contexto
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 2. APLICAMOS EL TEMA
        context.setTheme(com.kevinzamora.temporis_androidapp.R.style.Theme_TemporisAndroidApp)

        val parent = FrameLayout(context)

        // 3. Ahora el inflado encontrará todos los atributos de Material3 que faltaban
        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        assertNotNull("El ViewHolder no debería ser nulo", viewHolder)

        adapter.onBindViewHolder(viewHolder, 0)
        assertEquals("Titulo", viewHolder.title.text)
    }

    @Test
    fun testUpdateData() {
        val adapter = PostAdapter(mutableListOf())
        val newPosts = listOf(Post("1", "Nuevo"))
        adapter.updateData(newPosts)
        assertEquals(1, adapter.itemCount)
    }
}