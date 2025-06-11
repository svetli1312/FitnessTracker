@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitnesstracker.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnesstracker.FitnessTrackerTheme
import com.example.fitnesstracker.data.MealDatabaseHelper
import com.example.fitnesstracker.ui.ThemedScaffold
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class CalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Applies the app theme and launches the calendar screen
            FitnessTrackerTheme {
                CalendarScreen()
            }
        }
    }
}

@Composable
fun CalendarScreen() {
    val context = LocalContext.current

    val yearMonth = YearMonth.now()
    val today = LocalDate.now()
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sunday

    // Fill initial empty slots before the 1st day
    val days = mutableListOf<LocalDate?>()
    repeat(firstDayOfMonth) { days.add(null) }
    for (i in 1..daysInMonth) {
        days.add(yearMonth.atDay(i))
    }

    ThemedScaffold(title = "Progress Calendar") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month and Year Title
            Text(
                text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${yearMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Grid: 7 columns for days of the week
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days) { date ->
                    if (date != null) {
                        DayItem(fullDate = date)
                    } else {
                        Box(modifier = Modifier.aspectRatio(1f)) {} // Empty slot
                    }
                }
            }
        }
    }
}

@Composable
fun DayItem(fullDate: LocalDate) {
    val context = LocalContext.current
    val db = remember { MealDatabaseHelper(context) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateStr = fullDate.format(formatter)

    // Get meals for this specific date
    val meals = remember(dateStr) { db.getMealsByDate(dateStr) }
    val goals = db.getUserGoals()

    // ✅ Check if ALL goals are met for that day
    val isGoalMet = remember(meals, goals) {
        if (meals.isEmpty() || goals == null) return@remember false

        val totalCalories = meals.sumOf { it.calories }
        val totalProtein = meals.sumOf { it.protein }
        val totalCarbs = meals.sumOf { it.carbs }
        val totalFats = meals.sumOf { it.fats }

        fun isWithinGoal(actual: Int, goal: String): Boolean {
            val goalInt = goal.toIntOrNull() ?: return false
            val min = (goalInt * 0.9).toInt()
            val max = (goalInt * 1.1).toInt()
            return actual in min..max
        }

        return@remember isWithinGoal(totalCalories, goals.calories)
                && isWithinGoal(totalProtein, goals.protein)
                && isWithinGoal(totalCarbs, goals.carbs)
                && isWithinGoal(totalFats, goals.fat)
    }

    // ✅ Goal met = green, not met = red
    val backgroundColor = if (isGoalMet) Color(0xFF66BB6A) else Color(0xFFE57373)

    // Card representing a single day
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable {
                // Navigate to MealsByDateActivity when day is clicked
                val intent = Intent(context, MealsByDateActivity::class.java)
                intent.putExtra("date", dateStr)
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(fullDate.dayOfMonth.toString(), fontWeight = FontWeight.Bold, color = Color.White)
            if (!fullDate.isAfter(LocalDate.now())) {
                Text(if (isGoalMet) "✅" else "❌", color = Color.White)
            }
        }
    }
}