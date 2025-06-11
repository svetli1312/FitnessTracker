package com.example.fitnesstracker.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.example.fitnesstracker.FitnessTrackerTheme
import com.example.fitnesstracker.data.Meal
import com.example.fitnesstracker.data.MealDatabaseHelper
import com.example.fitnesstracker.ui.ThemedScaffold

class AddEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTrackerTheme {
                AddMealEntryScreen()
            }
        }
    }
}

@Composable
fun AddMealEntryScreen() {
    val context = LocalContext.current
    val dbHelper = remember { MealDatabaseHelper(context) }

    // ✅ Retrieve intent extras once
    val intent = remember { (context as? ComponentActivity)?.intent }
    val defaultName = remember { intent?.getStringExtra("meal_name") ?: "" }
    val defaultCalories = remember { intent?.getStringExtra("calories") ?: "" }
    val defaultProtein = remember { intent?.getStringExtra("protein") ?: "" }
    val defaultCarbs = remember { intent?.getStringExtra("carbs") ?: "" }
    val defaultFats = remember { intent?.getStringExtra("fat") ?: "" }

    // ✅ Input states
    var name by remember { mutableStateOf(defaultName) }
    var calories by remember { mutableStateOf(defaultCalories) }
    var protein by remember { mutableStateOf(defaultProtein) }
    var carbs by remember { mutableStateOf(defaultCarbs) }
    var fats by remember { mutableStateOf(defaultFats) }

    val textColor = Color.White
    val borderColor = Color.White

    ThemedScaffold(title = "Add Meal") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔠 Reusable composable for text input fields
            @Composable
            fun field(value: String, onChange: (String) -> Unit, label: String) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = borderColor,
                        focusedBorderColor = borderColor,
                        unfocusedLabelColor = textColor,
                        focusedLabelColor = textColor,
                        cursorColor = textColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
            }

            // 🧾 Input fields
            field(name, { name = it }, "Meal Name")
            field(calories, { calories = it }, "Calories")
            field(protein, { protein = it }, "Protein (g)")
            field(carbs, { carbs = it }, "Carbs (g)")
            field(fats, { fats = it }, "Fats (g)")

            Spacer(modifier = Modifier.height(20.dp))

            // 💾 Save button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val meal = Meal(
                            name = name,
                            calories = calories.toIntOrNull() ?: 0,
                            protein = protein.toIntOrNull() ?: 0,
                            carbs = carbs.toIntOrNull() ?: 0,
                            fats = fats.toIntOrNull() ?: 0
                        )

                        val result = dbHelper.insertMeal(meal)

                        if (result != -1L) {
                            // Let MainActivity know to refresh data
                            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                            prefs.edit().putBoolean("refreshMeals", true).apply()

                            Toast.makeText(context, "Meal saved!", Toast.LENGTH_SHORT).show()
                            (context as? ComponentActivity)?.finish()
                        } else {
                            Toast.makeText(context, "Error saving meal", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Please enter a meal name", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Meal", fontWeight = FontWeight.Bold)
            }
        }
    }
}