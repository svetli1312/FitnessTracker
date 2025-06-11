package com.example.fitnesstracker.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.preference.PreferenceManager

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Retrieve stored preferences to check if the user has set BMI/goals before
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val bmiSet = prefs.getBoolean("bmiSet", false)

        // ✅ Choose the next screen based on whether BMI has already been set
        val nextIntent = if (bmiSet) {
            // User has set goals → go to main screen
            Intent(this, MainActivity::class.java)
        } else {
            // User has not set goals → go to nutrition setup screen
            Intent(this, NutritionActivity::class.java)
        }

        // ✅ Start the appropriate activity and close this welcome screen
        startActivity(nextIntent)
        finish() // This prevents user from returning to the welcome screen with the back button
    }
}