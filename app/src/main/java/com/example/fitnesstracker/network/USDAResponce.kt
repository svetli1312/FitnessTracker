package com.example.fitnesstracker.network

// ✅ Wrapper class for the full response returned by the USDA API
data class USDAResponse(
    val foods: List<USDAFood> // List of food items matching the search query
)

// ✅ Represents a single food item in the response
data class USDAFood(
    val description: String, // Name of the food item
    val brandOwner: String?, // Brand name (if available)
    val foodNutrients: List<USDANutrient> // List of nutrient details
)

// ✅ Describes a single nutrient within a food item
data class USDANutrient(
    val nutrientName: String, // e.g. "Energy", "Protein", "Fat"
    val value: Double,        // Numeric value for the nutrient
    val unitName: String      // e.g. "kcal", "g", "mg"
)