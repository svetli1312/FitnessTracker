package com.example.fitnesstracker.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a Meal entry.
 *
 * @param id Unique ID of the meal (auto-incremented in DB).
 * @param name Name of the meal (e.g. "Apple", "Chicken Salad").
 * @param calories Number of calories in the meal.
 * @param protein Protein content in grams.
 * @param carbs Carbohydrates content in grams.
 * @param fats Fat content in grams.
 * @param date The date the meal was consumed/logged (formatted as yyyy-MM-dd).
 *        Defaults to the current date if not provided.
 */
data class Meal(
    val id: Int = 0,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)