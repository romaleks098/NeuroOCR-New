
package com.example.neuroocr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var result: TextView

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) recognizeText(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Neuro OCR"
            textSize = 28f
        }

        val chooseButton = Button(this).apply {
            text = "Выбрать изображение"
            setOnClickListener { pickImage.launch("image/*") }
        }

        status = TextView(this).apply {
            text = "Выбери изображение для распознавания текста."
            textSize = 16f
        }

        result = TextView(this).apply {
            textSize = 18f
            setTextIsSelectable(true)
        }

        layout.addView(title)
        layout.addView(chooseButton)
        layout.addView(status)
        layout.addView(result)

        setContentView(ScrollView(this).apply {
            addView(layout)
        })
    }

    private fun recognizeText(uri: Uri) {
        status.text = "Распознаю текст…"
        result.text = ""

        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    result.text = visionText.text.ifBlank {
                        "Текст не найден."
                    }
                    status.text = "Готово!"
                    recognizer.close()
                }
                .addOnFailureListener {
                    status.text = "Не удалось распознать текст."
                    recognizer.close()
                }
        } catch (e: Exception) {
            status.text = "Не удалось открыть изображение."
        }
    }
}
