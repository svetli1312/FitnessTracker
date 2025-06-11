package com.example.fitnesstracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstracker.data.Meal
import com.example.fitnesstracker.data.UserGoals
import com.example.fitnesstracker.R

// Define a theme color used across cards
private val cardColor = Color(0xFF00897B)


// ─────────────────────────────────────────────
// HEADER BAR COMPOSABLE
// ─────────────────────────────────────────────
@Composable
fun AppHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardColor)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.White
        )
    }
}


// ─────────────────────────────────────────────
// SCAFFOLD WITH BACKGROUND IMAGE AND HEADER
// ─────────────────────────────────────────────
@Composable
fun ThemedScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val backgroundPainter = painterResource(id = R.drawable.blurred_gym_background)

    Box {
        Image(
            painter = backgroundPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { AppHeader(title) },
            content = content
        )
    }
}


// ─────────────────────────────────────────────
// MACRO SUMMARY CARD WITH UPDATE BUTTON
// ─────────────────────────────────────────────
@Composable
fun MacroCard(goals: UserGoals, meals: List<Meal>, onUpdateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎯 Today's Macros", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(Modifier.height(8.dp))
            MacroSummary(goals, meals)

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onUpdateClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Update Goals", color = cardColor)
            }
        }
    }
}


// ─────────────────────────────────────────────
// MACRO SUMMARY TABLE (CALCULATED STATS)
// ─────────────────────────────────────────────
@Composable
fun MacroSummary(goals: UserGoals, meals: List<Meal>) {
    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFats = meals.sumOf { it.fats }

    val goalCalories = goals.calories.toIntOrNull() ?: 0
    val goalProtein = goals.protein.toIntOrNull() ?: 0
    val goalCarbs = goals.carbs.toIntOrNull() ?: 0
    val goalFats = goals.fat.toIntOrNull() ?: 0

    val red = Color(0xFFFFCDD2)
    val green = Color(0xFFC8E6C9)

    fun isWithinGoal(eaten: Int, goal: Int): Boolean {
        if (goal == 0) return false
        val min = (goal * 0.9).toInt()
        val max = (goal * 1.1).toInt()
        return eaten in min..max
    }

    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
        SummaryItem("Calories", totalCalories, goalCalories, isWithinGoal(totalCalories, goalCalories), green, red)
        SummaryItem("Protein", totalProtein, goalProtein, isWithinGoal(totalProtein, goalProtein), green, red)
        SummaryItem("Carbs", totalCarbs, goalCarbs, isWithinGoal(totalCarbs, goalCarbs), green, red)
        SummaryItem("Fats", totalFats, goalFats, isWithinGoal(totalFats, goalFats), green, red)
    }
}


// ─────────────────────────────────────────────
// SINGLE MACRO ITEM DISPLAY LINE
// ─────────────────────────────────────────────
@Composable
fun SummaryItem(label: String, actual: Int, goal: Int, met: Boolean, green: Color, red: Color) {
    val color = if (met) green else red
    Text(
        "$label: $goal (Eaten: $actual)",
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}


// ─────────────────────────────────────────────
// CARD THAT DISPLAYS MEAL INFO
// ─────────────────────────────────────────────
@Composable
fun MealCard(meal: Meal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                meal.name.uppercase(),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Calories: ${meal.calories} | P: ${meal.protein}g | C: ${meal.carbs}g | F: ${meal.fats}g",
                color = Color.White
            )
        }
    }
}


// ─────────────────────────────────────────────
// ALTERNATE MACRO SUMMARY CARD (USED ON MAIN)
// ─────────────────────────────────────────────
@Composable
fun MacroSummaryCard(
    goals: UserGoals,
    meals: List<Meal>,
    onUpdateWeightClick: () -> Unit
) {
    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFats = meals.sumOf { it.fats }

    val goalCalories = goals.calories.toIntOrNull() ?: 0
    val goalProtein = goals.protein.toIntOrNull() ?: 0
    val goalCarbs = goals.carbs.toIntOrNull() ?: 0
    val goalFats = goals.fat.toIntOrNull() ?: 0

    val green = Color(0xFF66BB6A)
    val red = Color(0xFFE57373)

    fun isWithinGoal(actual: Int, goal: Int): Boolean {
        if (goal == 0) return false
        val min = (goal * 0.9).toInt()
        val max = (goal * 1.1).toInt()
        return actual in min..max
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 Today's Macros",
                fontSize = 22.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow("Calories", totalCalories, goalCalories, isWithinGoal(totalCalories, goalCalories), green, red)
            SummaryRow("Protein", totalProtein, goalProtein, isWithinGoal(totalProtein, goalProtein), green, red)
            SummaryRow("Carbs", totalCarbs, goalCarbs, isWithinGoal(totalCarbs, goalCarbs), green, red)
            SummaryRow("Fats", totalFats, goalFats, isWithinGoal(totalFats, goalFats), green, red)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onUpdateWeightClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = cardColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Update Weight")
            }
        }
    }
}


// ─────────────────────────────────────────────
// ROW LINE FOR EACH MACRO (ALTERNATE VERSION)
// ─────────────────────────────────────────────
@Composable
fun SummaryRow(
    label: String,
    actual: Int,
    goal: Int,
    met: Boolean,
    green: Color,
    red: Color
) {
    val color = if (met) green else red
    Text(
        text = "$label: $goal (Eaten: $actual)",
        color = color,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium
    )
}