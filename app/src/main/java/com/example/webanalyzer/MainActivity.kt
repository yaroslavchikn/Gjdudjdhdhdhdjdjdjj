package com.example.webanalyzer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrl: EditText
    private lateinit var buttonAnalyze: Button
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUrl = findViewById(R.id.editTextUrl)
        buttonAnalyze = findViewById(R.id.buttonAnalyze)
        textViewResult = findViewById(R.id.textViewResult)

        buttonAnalyze.setOnClickListener {
            val url = editTextUrl.text.toString()
            if (url.isBlank()) {
                textViewResult.text = "Введите ссылку"
                return@setOnClickListener
            }
            analyzeUrl(url)
        }
    }

    private fun analyzeUrl(url: String) {
        textViewResult.text = "Анализирую..."
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).get()
                val title = doc.title()
                val bodyText = doc.body().text()
                val result = buildString {
                    appendLine("Заголовок страницы: $title")
                    appendLine("---")
                    appendLine("Текст (первые 500 символов):")
                    appendLine(bodyText.take(500) + if (bodyText.length > 500) "..." else "")
                }
                withContext(Dispatchers.Main) {
                    textViewResult.text = result
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    textViewResult.text = "Ошибка загрузки: ${e.message}"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textViewResult.text = "Ошибка: ${e.message}"
                }
            }
        }
    }
}
