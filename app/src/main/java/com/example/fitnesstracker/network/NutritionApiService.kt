package com.example.fitnesstracker.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Interface for accessing the RapidAPI Nutrition Calculator API
interface NutritionApiService {

    // These headers are required by RapidAPI to authenticate your request
    @Headers(
        "x-rapidapi-host: nutrition-calculator.p.rapidapi.com",
        "x-rapidapi-key: 67a90518b3msh930f12f0c492515p15ae16jsn2dbd69769805" // ❗ Replace with your actual key in production or move to a safe location
    )
    @GET("api/nutrition-info")
    fun getNutritionInfo(
        // Units for measurement (e.g., metric)
        @Query("measurement_units") units: String = "met",

        // Required fields from the user input screen
        @Query("sex") sex: String,
        @Query("age_value") ageValue: Int,
        @Query("age_type") ageType: String = "yrs",
        @Query("cm") cm: Int,
        @Query("kilos") kilos: Int,
        @Query("activity_level") activity: String
    ): Call<NutritionResponse>
}