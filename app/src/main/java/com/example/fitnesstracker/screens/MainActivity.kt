@file:OptIn(ExperimentalMaterialApi::class)

package com.example.fitnesstracker.screens

// ✅ UI imports
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog

// ✅ Layouts and Lists
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// ✅ Swipe-to-dismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState

// ✅ Compose Material 3 elements
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

// ✅ Project-specific components and themes
import com.example.fitnesstracker.FitnessTrackerTheme
import com.example.fitnesstracker.data.MealDatabaseHelper
import com.example.fitnesstracker.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTrackerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dbHelper = remember { MealDatabaseHelper(context) }

    val currentMeals = remember { mutableStateOf(emptyList<com.example.fitnesstracker.data.Meal>()) }

    // 🧹 Lifecycle observer to refresh meals every time screen becomes visible
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentMeals.value = dbHelper.getMealsForToday()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val goals = dbHelper.getUserGoals()

    ThemedScaffold(title = "Daily Gains") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (goals != null) {
                MacroSummaryCard(
                    goals = goals,
                    meals = currentMeals.value,
                    onUpdateWeightClick = {
                        context.startActivity(Intent(context, NutritionActivity::class.java))
                    }
                )
            } else {
                Text("No goals set", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val buttonColors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    contentColor = Color.White
                )

                Button(
                    onClick = {
                        val options = arrayOf("Manual Entry", "Search USDA Database", "Scan from Image")
                        AlertDialog.Builder(context).apply {
                            setTitle("Add Meal")
                            setItems(options) { _, which ->
                                when (which) {
                                    0 -> context.startActivity(Intent(context, AddEntryActivity::class.java))
                                    1 -> context.startActivity(Intent(context, SearchMealActivity::class.java))
                                    2 -> context.startActivity(Intent(context, TextRecognitionActivity::class.java))
                                }
                            }
                            show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = buttonColors
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Meal")
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, CalendarActivity::class.java))
                    },
                    modifier = Modifier.weight(1f),
                    colors = buttonColors
                ) {
                    Text("View Calendar")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("🍽 Meals Today", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(currentMeals.value, key = { it.id }) { meal ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = { value ->
                            if (value == DismissValue.DismissedToStart || value == DismissValue.DismissedToEnd) {
                                dbHelper.deleteMeal(meal.id)
                                currentMeals.value = dbHelper.getMealsForToday()
                                true
                            } else false
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text("Delete", color = Color.White)
                            }
                        },
                        dismissContent = {
                            MealCard(meal)
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}