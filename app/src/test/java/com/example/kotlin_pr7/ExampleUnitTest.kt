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

// Библиотеки:
// Robolectric — это библиотека для юнит-тестирования Android-приложений вне эмулятора или физического устройства.
// JUnit - для написания и выполнения тестов.
// Mockito — это популярная библиотека для создания и использования моков (имитаций) объектов в тестах.
// Kotlin Coroutines Test - Эта библиотека предоставляет средства для тестирования корутин в Kotlin
// AssertJ — это библиотека для утверждений, которая предоставляет удобный и читаемый синтаксис для работы с тестами.
// AndroidX Test помогает создать и запустить экземпляр MainActivity в тестах.

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class) // Указывает на использование Robolectric для тестирования Android-кода вне устройства/эмулятора.
@Config(manifest = Config.DEFAULT_MANIFEST_NAME) // Указывает Robolectric использовать стандартный AndroidManifest.xml для тестов.
class ExampleUnitTest {
    private lateinit var mainActivity: MainActivity  // Ссылка на MainActivity.
    private lateinit var mainActivitySpy: MainActivity  // Шпионская версия MainActivity для проверки вызова методов.
    private lateinit var mockContext: Context  // Мок контекста для использования в тестах.
    private val testDispatcher = StandardTestDispatcher()  // Диспетчер для тестирования корутин в основной очереди.
    private val imageView: ImageView = mock(ImageView::class.java)  // Мок для ImageView.
    private val textView: TextView = mock(TextView::class.java)  // Мок для TextView.
    private val button: Button = mock(Button::class.java)  // Мок для Button.

    // Выполняется перед каждым тестом
    @Before
    fun setUp(){
        mockContext = mock(Context::class.java)  // Создаем мок для контекста.
        Dispatchers.setMain(testDispatcher)  // Устанавливаем диспетчер для главного потока в тестах.
        mainActivity = MainActivity()  // Создаем экземпляр MainActivity.
        mainActivitySpy = spy(MainActivity())  // Создаем шпионскую версию MainActivity, чтобы отслеживать вызовы методов.

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

    // Тест на инициализацию Activity и UI элементов
    @Test
    fun test_initalize(){
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()  // Настройка Activity.
            val activity = controller.get()  // Получаем экземпляр Activity.
            assertNotNull(activity)  // Проверяем, что Activity не null.

            val txt = activity.findViewById<TextView>(R.id.txt)
            assertNotNull(txt)  // Проверяем, что TextView с ID txt найден.
            val button = activity.findViewById<Button>(R.id.button)
            assertNotNull(button)  // Проверяем, что Button с ID button найден.
        }
    }

    // Тест на правильную работу загрузки при клике
    @Test
    fun test_isDownload_work(){
        val link = "https://funik.ru/wp-content/uploads/2018/10/17478da42271207e1d86.jpg"
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()
            Mockito.doAnswer {
                mainActivitySpy.downloadImage(link)  // Заменяем поведение кнопки.
                null
            }.`when`(button).performClick()  // Настроили мок для выполнения метода при клике на кнопку.

            button.performClick()  // Выполняем клик по кнопке.
            verify(mainActivitySpy).downloadImage(link)  // Проверяем, что метод downloadImage был вызван.
        }
    }

    @Test
    fun test_doTextChanges(){
        Robolectric.buildActivity(MainActivity::class.java).use { controller ->
            controller.setup()
            controller.get().findViewById<Button>(R.id.button).performClick()  // Выполняем клик по кнопке.
            val texts: TextView = controller.get().findViewById(R.id.txt)  // Находим TextView.

            Mockito.doAnswer {
                texts.text = "This is cat!"  // Заменяем текст в TextView.
                null
            }.`when`(button).performClick()  // Настроили мок для выполнения метода при клике на кнопку.

            button.performClick()  // Выполняем клик по кнопке.
            assertEquals("This is cat!", texts.text)  // Проверяем, что текст изменился на "This is cat!".
        }
    }
}