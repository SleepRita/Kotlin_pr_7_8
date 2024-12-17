package com.example.kotlin_pr7

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.DEFAULT_MANIFEST_NAME)
class   ExampleUnitTest {
    private lateinit var mainActivity: MainActivity
    private lateinit var mainActivitySpy: MainActivity
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()
    private val imageView: ImageView = mock(ImageView::class.java)
    private val textView: TextView = mock(TextView::class.java)
    private val button: Button = mock(Button::class.java)

    @Before
    fun setUp(){
        mockContext = mock(Context::class.java)
        Dispatchers.setMain(testDispatcher)
        mainActivity = MainActivity()
        mainActivitySpy = spy(MainActivity())

    }

    @Test
    fun test_downloadGood() = runBlocking{
        val link = "https://funik.ru/wp-content/uploads/2018/10/17478da42271207e1d86.jpg"
        assertNotNull(mainActivity.downloadImage(link))
    }
    @Test
    fun test_downloadBad() = runBlocking{
        val link = "https://funik.ru/wp-content/upload/2018/10/17478da42271207e1d86.jpg"
        assertNull(mainActivity.downloadImage(link))
    }

    @Test
    fun test_initalize(){
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()
            val activity = controller.get()
            assertNotNull(activity)
            val txt = activity.findViewById<TextView>(R.id.txt)
            assertNotNull(txt)
            val button = activity.findViewById<Button>(R.id.button)
            assertNotNull(button)
        }
    }

    @Test
    fun test_isDownload_work(){
        val link = "https://funik.ru/wp-content/uploads/2018/10/17478da42271207e1d86.jpg"
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()
            Mockito.doAnswer{
                mainActivitySpy.downloadImage(link)
                null
            }.`when`(button).performClick()
            button.performClick()
            verify(mainActivitySpy).downloadImage(link)
        }
    }

    @Test
    fun test_doTextChanges(){
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()
            controller.get().findViewById<Button>(R.id.button).performClick()
            val texts: TextView = controller.get().findViewById(R.id.txt)
            Mockito.doAnswer{
                texts.text = "This is cat!"
                null
            }.`when`(button).performClick()
            button.performClick()
            assertEquals("This is cat!",texts.text)
        }
    }
}