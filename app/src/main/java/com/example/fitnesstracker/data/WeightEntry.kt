package com.example.fitnesstracker.data

/**
 * Data class representing a weight entry.
 *
 * Used to log the user's weight over time. This data can be extended later
 * to show progress graphs or trends based on historical weight changes.
 *
 * @param id Auto-incremented ID for the entry (set by the database).
 * @param weight The user's weight in kilograms.
 * @param timestamp The time the weight was recorded, stored as milliseconds since epoch.
 */
data class WeightEntry(
    val id: Int = 0,               // Default value for new entries before insertion into the database
    val weight: Float,             // Weight value in kilograms
    val timestamp: Long            // Unix timestamp for when the weight was logged
)