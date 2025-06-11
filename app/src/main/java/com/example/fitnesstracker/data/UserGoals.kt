package com.example.fitnesstracker.data

/**
 * Data class representing the user's nutritional goals.
 *
 * These values are stored as Strings because they are typically retrieved
 * from a SQLite database where everything is stored as text by default.
 * You can convert them to Int when needed.
 *
 * @param calories Daily calorie goal.
 * @param protein Daily protein goal in grams.
 * @param carbs Daily carbohydrate goal in grams.
 * @param fat Daily fat goal in grams.
 */
data class UserGoals(
    val calories: String,
    val protein: String,
    val carbs: String,
    val fat: String
)