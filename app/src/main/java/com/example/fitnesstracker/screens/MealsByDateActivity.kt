@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitnesstracker.screens

// ✅ Android & Compose imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// ✅ Project imports
import com.example.fitnesstracker.FitnessTrackerTheme
import com.example.fitnesstracker.data.MealDatabaseHelper
import com.example.fitnesstracker.ui.MealCard
import com.example.fitnesstracker.ui.ThemedScaffold
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ✅ Screen that displays meals for a selected calendar date
class MealsByDateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔁 Fetch date passed from calendar
        val date = intent.getStringExtra("date") ?: return

        setContent {
            FitnessTrackerTheme {
                MealsByDateScreen(date)
            }
        }
    }
}

@Composable
fun MealsByDateScreen(date: String) {
    val context = LocalContext.current
    val db = remember { MealDatabaseHelper(context) }

    // 🔁 Get all meals for the specific date
    val meals = remember { db.getMealsByDate(date) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val parsedDate = LocalDate.parse(date, formatter)

    // 🧾 Title: "Meals on 23 April"
    val formattedTitle = "Meals on ${parsedDate.dayOfMonth} ${
        parsedDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
    }"

    // ✅ Scaffold with top title bar and body layout
    ThemedScaffold(title = formattedTitle) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🚫 Show message if no meals found
            if (meals.isEmpty()) {
                Text(
                    "No meals logged on this day.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // 🍽 Display list of meals
                LazyColumn {
                    items(meals) { meal ->
                        MealCard(meal = meal)
                    }
                }
            }
        }
    }
}