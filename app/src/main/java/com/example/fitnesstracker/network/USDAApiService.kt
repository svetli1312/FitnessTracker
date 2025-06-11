package com.example.fitnesstracker.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// ✅ Retrofit interface for accessing USDA FoodData Central API
interface USDAApiService {

    // 🔍 Performs a search query for foods based on the given string
    @GET("foods/search")
    fun searchFoods(
        @Query("query") query: String, // e.g., "Apple", "Chicken breast"
        @Query("api_key") apiKey: String = "9pbglig0YkZ5XTSEo5fP6jhuLpYupCnMIxQAhlPb"
        // API key to access the USDA food data API
    ): Call<USDAResponse> // The result will be mapped to the USDAResponse data class
}