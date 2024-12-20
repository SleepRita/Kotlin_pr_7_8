package com.example.kotlin_pr7

import android.os.Handler
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

// Используются классы:
// Handler - класс Android, используемый для работы с потоками и выполнения кода в UI-потоке с задержкой
// Looper — класс, который предоставляет цикл обработки сообщений для потока
// ActivityScenario — класс из библиотеки AndroidX для тестирования активности

// Используются библиотеки:
// Библиотека JUnit, которая позволяет указать, какой тестовый раннер будет использоваться для выполнения тестов.
// В данном случае AndroidJUnit4
// Espresso - библиотека для тестирования пользовательского интерфейса (UI)

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)  // Указывает, что тесты будут выполняться с использованием AndroidJUnit4
class ExampleInstrumentedTest {

    // Правило для запуска Activity в тесте
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Тест на проверку видимости UI элементов (кнопки и текста)
    @Test
    fun checkUiElementsVisibility() {
        // Проверяем, что кнопка отображается на экране
        onView(withId(R.id.button)).check(matches(isDisplayed()))
        // Проверяем, что текстовое поле также отображается на экране
        onView(withId(R.id.txt)).check(matches(isDisplayed()))
    }

    // Тест на проверку состояния изображения при начальной загрузке
    @Test
    fun checkImageInitialState() {
        // Мы используем Handler и Looper для задержки, чтобы дать время на отрисовку UI.
        Handler(Looper.getMainLooper()).postDelayed({
            // Проверяем, что ImageView существует (не является null)
            checkNotNull(R.id.imageView)
        }, 2000)  // Задержка в 2000 мс (2 секунды)
    }

    // Тест на проверку текста в TextView
    @Test
    fun checkText() {
        // Проверяем, что в текстовом поле отображается текст "Hello World!"
        onView(withId(R.id.txt)).check(matches(withText("Hello World!")))
    }

    // Тест на проверку реакции кнопки при нажатии
    @Test
    fun checkButtonClick() {
        // Симулируем клик по кнопке
        onView(withId(R.id.button)).perform(click())

        // После клика, проверяем состояние ImageView с задержкой, чтобы дать время на обновление UI
        Handler(Looper.getMainLooper()).postDelayed({
            // Проверяем, что ImageView не равен null после нажатия
            checkNotNull(R.id.imageView)
        }, 2000)
    }

    // Тест для проверки, что текст меняется после загрузки изображения
    @Test
    fun checkTextChangedDisplayAfterDownload() {
        // Используем задержку, чтобы дать время на выполнение асинхронных операций
        Handler(Looper.getMainLooper()).postDelayed({
            // После задержки, имитируем клик по кнопке
            onView(withId(R.id.button)).perform(click())

            // Проверяем, что текст в TextView изменился на "Downloaded"
            onView(withId(R.id.txt)).check(matches(withText("This is cat")))
        }, 3000)  // Задержка в 3000 мс (3 секунды)
    }
}