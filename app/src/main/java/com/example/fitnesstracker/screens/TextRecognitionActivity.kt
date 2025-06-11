package com.example.fitnesstracker.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.ui.ThemedScaffold
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognitionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }
            var resultText by remember { mutableStateOf("") }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    bitmap = loadBitmapFromUri(context, it)
                    bitmap?.let { bmp ->
                        val image = InputImage.fromBitmap(bmp, 0)
                        val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

                        recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                resultText = visionText.text
                                val macros = extractMacros(resultText)

                                val intent = Intent(context, AddEntryActivity::class.java).apply {
                                    putExtra("calories", macros["calories"] ?: "")
                                    putExtra("protein", macros["protein"] ?: "")
                                    putExtra("carbs", macros["carbs"] ?: "")
                                    putExtra("fat", macros["fat"] ?: "")
                                    putExtra("meal_name", macros["meal_name"] ?: "")
                                }
                                context.startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to recognize text", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            ThemedScaffold(title = "Scan Label") { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Pick Image")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    bitmap?.let {
                        Image(bitmap = it.asImageBitmap(), contentDescription = null)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (resultText.isNotEmpty()) {
                        Text("Detected Text:\n$resultText")
                    }
                }
            }
        }
    }

    private fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            Log.e("BitmapLoad", "Failed to decode bitmap", e)
            null
        }
    }

    private fun extractMacros(text: String): Map<String, String> {
        val macros = mutableMapOf<String, String>()

        // Normalize lines and strip empty ones
        val lines = text.lines()
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        val macroKeys = listOf("calories", "protein", "carbs", "fat")

        // Flexible aliases to detect different macro labels
        val aliases = mapOf(
            "calories" to listOf("calories", "kcal", "cal"),
            "protein" to listOf("protein", "protien", "proten", "prot"),
            "carbs" to listOf("carbs", "carbohydrates", "carbohydrate", "carb"),
            "fat" to listOf("fat", "fats", "total fat")
        )

        val numericRegex = Regex("""(\d{1,4})[gk]*""") // Match numbers like 12, 300, etc.

        for (i in lines.indices) {
            val current = lines[i]
            val next = lines.getOrNull(i + 1)

            for ((macro, keywords) in aliases) {
                if (keywords.any { current.contains(it) }) {
                    // First try to find number in the same line
                    numericRegex.find(current)?.groupValues?.get(1)?.let { value ->
                        macros[macro] = value
                    } ?: run {
                        // Then try to find number in the next line
                        next?.let {
                            numericRegex.find(it)?.groupValues?.get(1)?.let { value ->
                                macros[macro] = value
                            }
                        }
                    }
                }
            }
        }

        // Try to extract meal name from top 1–3 lines if not already identified as macro
        val possibleName = lines.take(3).filterNot { line ->
            aliases.values.flatten().any { line.contains(it) } || numericRegex.containsMatchIn(line)
        }.joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

        if (possibleName.isNotBlank()) {
            macros["meal_name"] = possibleName
        }

        Log.d("OCR_MACRO_EXTRACT", macros.toString())
        return macros
    }}