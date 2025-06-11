package com.example.fitnesstracker.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.FitnessTrackerTheme
import com.example.fitnesstracker.data.Meal
import com.example.fitnesstracker.data.MealDatabaseHelper
import com.example.fitnesstracker.network.RetrofitClient
import com.example.fitnesstracker.network.USDAFood
import com.example.fitnesstracker.network.USDAResponse
import com.example.fitnesstracker.ui.ThemedScaffold
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMealActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTrackerTheme {
                SearchMealScreen()
            }
        }
    }
}

@Composable
fun SearchMealScreen() {
    val context = LocalContext.current
    val dbHelper = remember { MealDatabaseHelper(context) }

    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<USDAFood>()) }

    val textColor = Color.White
    val outline = Color.White

    ThemedScaffold(title = "Search USDA Meal") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search food (e.g. Apple)", color = textColor, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = outline,
                    unfocusedBorderColor = outline,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor,
                    cursorColor = textColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (query.isNotBlank()) {
                        RetrofitClient.usdaApi.searchFoods(query)
                            .enqueue(object : Callback<USDAResponse> {
                                override fun onResponse(call: Call<USDAResponse>, response: Response<USDAResponse>) {
                                    results = response.body()?.foods ?: emptyList()
                                }

                                override fun onFailure(call: Call<USDAResponse>, t: Throwable) {
                                    Toast.makeText(context, "API failed: ${t.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("USDA", "API failed", t)
                                }
                            })
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF00897B)
                )
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results) { food ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF00897B).copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(food.description, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))

                            food.foodNutrients.take(3).forEach {
                                Text("${it.nutrientName}: ${it.value} ${it.unitName}", color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val calories = food.foodNutrients.find { it.nutrientName.contains("Energy") }?.value?.toInt() ?: 0
                                    val protein = food.foodNutrients.find { it.nutrientName.contains("Protein") }?.value?.toInt() ?: 0
                                    val carbs = food.foodNutrients.find { it.nutrientName.contains("Carbohydrate") }?.value?.toInt() ?: 0
                                    val fats = food.foodNutrients.find { it.nutrientName.contains("Total lipid") }?.value?.toInt() ?: 0

                                    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                                    val meal = Meal(
                                        id = 0,
                                        name = food.description,
                                        calories = calories,
                                        protein = protein,
                                        carbs = carbs,
                                        fats = fats,
                                        date = today
                                    )

                                    dbHelper.insertMeal(meal)
                                    Toast.makeText(context, "Added ${food.description} to meals", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF00897B)
                                )
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }
}