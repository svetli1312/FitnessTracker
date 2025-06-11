@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitnesstracker.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.ui.ThemedScaffold
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.example.fitnesstracker.FitnessTrackerTheme
import com.example.fitnesstracker.data.MealDatabaseHelper
import com.example.fitnesstracker.network.NutritionResponse
import com.example.fitnesstracker.network.RetrofitClient
import androidx.preference.PreferenceManager

class NutritionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTrackerTheme {
                NutritionScreen() // Load the composable Nutrition UI
            }
        }
    }
}

@Composable
fun NutritionScreen() {
    val context = LocalContext.current

    // Form input states
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // Dropdown menu for activity level
    val activityOptions = listOf("Inactive", "Low Active", "Active", "Very Active")
    var activity by remember { mutableStateOf("Active") }
    var expanded by remember { mutableStateOf(false) }

    // Scaffold with reusable header
    ThemedScaffold(title = "Nutrition Calculator") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Define consistent text field colors
            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )

            // User input fields
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender (male/female)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for activity level
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = activity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Activity Level") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = fieldColors
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    activityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                activity = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to calculate and save goals
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    val heightInt = height.toIntOrNull()
                    val weightInt = weight.toIntOrNull()

                    // Check input validation
                    if (ageInt != null && heightInt != null && weightInt != null) {
                        // Make API call
                        RetrofitClient.nutritionApi.getNutritionInfo(
                            sex = gender,
                            ageValue = ageInt,
                            cm = heightInt,
                            kilos = weightInt,
                            activity = activity
                        ).enqueue(object : Callback<NutritionResponse> {
                            override fun onResponse(
                                call: Call<NutritionResponse>,
                                response: Response<NutritionResponse>
                            ) {
                                val body = response.body()
                                if (response.isSuccessful && body != null) {
                                    val eer = body.bmiEer
                                    val table = body.macronutrientsWrapper.table

                                    // Extract and clean numbers from API text
                                    val caloriesGoal = extractFirstNumber(eer.calories)
                                    var protein = ""
                                    var carbs = ""
                                    var fat = ""

                                    table.forEach { row ->
                                        if (row.size >= 2) {
                                            when (row[0]) {
                                                "Protein" -> protein = extractFirstNumber(row[1])
                                                "Carbohydrate" -> carbs = extractFirstNumber(row[1])
                                                "Fat" -> fat = extractFirstNumber(row[1])
                                            }
                                        }
                                    }

                                    Log.d("GOALS_SAVE", "Saving to DB: Calories=$caloriesGoal Protein=$protein Carbs=$carbs Fat=$fat")

                                    val db = MealDatabaseHelper(context)
                                    db.saveUserGoals(caloriesGoal, protein, carbs, fat)

                                    // ✅ Fix: Save the "bmiSet" flag in SharedPreferences
                                    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                                    prefs.edit().putBoolean("bmiSet", true).apply()

                                    Toast.makeText(context, "Goals updated!", Toast.LENGTH_SHORT).show()
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                } else {
                                    Toast.makeText(context, "Invalid response", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<NutritionResponse>, t: Throwable) {
                                Toast.makeText(context, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(context, "Enter valid numbers", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Goals")
            }
        }
    }
}

// Helper function to extract first number from a string
fun extractFirstNumber(input: String): String {
    val clean = input.replace(",", "") // Remove commas like in "3,045 kcal"
    val match = Regex("""\d+(\.\d+)?""").find(clean)
    return match?.value?.split(".")?.firstOrNull() ?: "0"
}