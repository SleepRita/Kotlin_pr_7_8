package com.example.kotlin_pr7

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_pr7.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        // Создание обработчика для работы с потоками и задачами
        val handler = Handler(Looper.getMainLooper())

        // Переменные для работы с изображениями
        var image: Bitmap? = null
        lateinit var imag: InputStream

        enableEdgeToEdge()
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Устанавливаем отступы для View, чтобы контент не перекрывался системными панелями
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button.setOnClickListener {
            // Запуск корутины с использованием lifecycleScope (позволяет управлять жизненным циклом корутины)
            lifecycleScope.launch {
                // Обновляем текст в TextView
                binding.txt.text = "This is cat!"

                // URL изображения, которое мы будем загружать
                val imageURL = "https://funik.ru/wp-content/uploads/2018/10/17478da42271207e1d86.jpg"

                // Загружаем и сохраняем изображение с помощью метода downloadAndSaveImage
                image = downloadAndSaveImage(imageURL)

                // Если изображение успешно загружено, отображаем его в ImageView
                if (image != null) {
                    binding.imageView.setImageBitmap(image)
                }
            }
        }
    }

    // suspend функция для загрузки и сохранения изображения (она может быть приостановлена)
    suspend fun downloadAndSaveImage(imageUrl: String): Bitmap? {
        // Переводим выполнение в фоновый поток с помощью withContext
        val bitmap = withContext(Dispatchers.IO) {
            // Загружаем изображение
            downloadImage(imageUrl)
        }

        // Если изображение было успешно загружено, сохраняем его
        if (bitmap != null) {
            // Сохраняем изображение в файл на диске
            withContext(Dispatchers.IO) {
                saveImageToDisk(bitmap)
            }
        }
        return bitmap // Возвращаем результат (bitmap или null)
    }

    // Функция для загрузки изображения из интернета
    fun downloadImage(imageUrl: String): Bitmap? = runCatching {
        // Открываем соединение и получаем поток данных для изображения
        val connection = URL(imageUrl).openConnection()
        connection.doInput = true
        val input = connection.getInputStream()
        // Декодируем поток в Bitmap
        BitmapFactory.decodeStream(input)
    }.getOrNull() // Если произошла ошибка, возвращаем null

    // Функция для сохранения изображения на диск
    fun saveImageToDisk(bitmap: Bitmap) {
        runCatching {
            // Получаем директорию для хранения изображений на внешнем хранилище
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "downloaded_image.jpg")
            // Создаем поток вывода для записи файла
            FileOutputStream(file).use { outputStream ->
                // Сжимаем изображение в JPEG формат и записываем в файл
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                // Принудительная запись данных в файл
                outputStream.flush()
                Log.d("rrr", "Изображение сохранено")

            }
        }.onFailure {
            // Логируем ошибку в случае неудачного сохранения
            Log.d("rrr", "Ошибка сохранения изображения")
        }
    }
}