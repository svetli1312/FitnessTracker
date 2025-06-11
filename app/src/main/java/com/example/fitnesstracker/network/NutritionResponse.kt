package com.example.fitnesstracker.network

import com.google.gson.annotations.SerializedName

// ✅ Top-level data class that maps the entire API response from the Nutrition Calculator API
data class NutritionResponse(
    @SerializedName("BMI_EER") // Maps JSON key "BMI_EER" to this variable
    val bmiEer: BmiEer,

    @SerializedName("macronutrients_table") // Maps JSON key to a wrapper for the table
    val macronutrientsWrapper: MacronutrientsWrapper
)

// ✅ Nested data class for BMI and Estimated Energy Requirements (EER)
data class BmiEer(
    @SerializedName("BMI") // JSON key
    val bmi: String,

    @SerializedName("Estimated Daily Caloric Needs")
    val calories: String
)

// ✅ This wraps the 2D table structure with nutrient details like protein, carbs, fat
data class MacronutrientsWrapper(
    @SerializedName("macronutrients-table") // JSON key with dash requires annotation
    val table: List<List<String>> // Each sub-list represents a row from the API's macro table
)