package com.example.fitnesstracker.network

import com.example.fitnesstracker.network.NutritionApiService
import com.example.fitnesstracker.network.USDAApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ✅ Singleton object for managing Retrofit clients
object RetrofitClient {

    // ✅ Lazy-initialized instance for the Nutrition API
    val nutritionApi: NutritionApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nutrition-calculator.p.rapidapi.com/") // Base URL for the nutrition calculator API
            .addConverterFactory(GsonConverterFactory.create()) // JSON to Kotlin conversion
            .build()
            .create(NutritionApiService::class.java) // Create an implementation of the NutritionApi interface
    }

    // ✅ Lazy-initialized instance for the USDA FoodData API
    val usdaApi: USDAApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/v1/") // Base URL for USDA food search
            .addConverterFactory(GsonConverterFactory.create()) // JSON to Kotlin conversion
            .build()
            .create(USDAApiService::class.java) // Create an implementation of the USDAApi interface
    }
}