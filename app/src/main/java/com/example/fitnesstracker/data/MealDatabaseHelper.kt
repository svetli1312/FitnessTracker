package com.example.fitnesstracker.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SQLiteOpenHelper class for handling local storage of meals, goals, and weight data.
 */
class MealDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    /**
     * Fetches the user's calorie/macro goals from the database.
     * Returns a UserGoals object if found, null otherwise.
     */
    fun getUserGoals(): UserGoals? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_GOALS LIMIT 1", null)

        return if (cursor.moveToFirst()) {
            val calories = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOAL_CALORIES))
            val protein = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOAL_PROTEIN))
            val carbs = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOAL_CARBS))
            val fat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOAL_FAT))
            UserGoals(calories, protein, carbs, fat)
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    /**
     * Called when the database is created for the first time.
     * Creates three tables: meals, weights, and user goals.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createMealTable = """
            CREATE TABLE $TABLE_MEALS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_CALORIES INTEGER,
                $COLUMN_PROTEIN INTEGER,
                $COLUMN_CARBS INTEGER,
                $COLUMN_FATS INTEGER,
                $COLUMN_DATE TEXT
            )
        """.trimIndent()
        db?.execSQL(createMealTable)

        val createWeightTable = """
            CREATE TABLE $TABLE_WEIGHTS (
                $COLUMN_WEIGHT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WEIGHT_VALUE REAL,
                $COLUMN_TIMESTAMP INTEGER
            )
        """.trimIndent()
        db?.execSQL(createWeightTable)

        val createGoalsTable = """
            CREATE TABLE $TABLE_GOALS (
                $COLUMN_GOALS_ID INTEGER PRIMARY KEY,
                $COLUMN_GOAL_CALORIES TEXT,
                $COLUMN_GOAL_PROTEIN TEXT,
                $COLUMN_GOAL_CARBS TEXT,
                $COLUMN_GOAL_FAT TEXT
            )
        """.trimIndent()
        db?.execSQL(createGoalsTable)
    }

    /**
     * Called when the database needs to be upgraded.
     * Drops existing tables and recreates them.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MEALS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WEIGHTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        onCreate(db)
    }

    /**
     * Inserts a new weight entry into the weights table.
     */
    fun insertWeight(weightEntry: WeightEntry): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WEIGHT_VALUE, weightEntry.weight)
            put(COLUMN_TIMESTAMP, weightEntry.timestamp)
        }
        return db.insert(TABLE_WEIGHTS, null, values)
    }

    /**
     * Inserts a new meal into the meals table.
     */
    fun insertMeal(meal: Meal): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, meal.name)
            put(COLUMN_CALORIES, meal.calories)
            put(COLUMN_PROTEIN, meal.protein)
            put(COLUMN_CARBS, meal.carbs)
            put(COLUMN_FATS, meal.fats)
            put(COLUMN_DATE, meal.date)
        }
        return db.insert(TABLE_MEALS, null, values)
    }

    /**
     * Retrieves all meals from the database for a specific date.
     */
    fun getMealsByDate(date: String): List<Meal> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MEALS WHERE $COLUMN_DATE = ?",
            arrayOf(date)
        )

        val meals = mutableListOf<Meal>()
        if (cursor.moveToFirst()) {
            do {
                val meal = Meal(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                    protein = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROTEIN)),
                    carbs = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CARBS)),
                    fats = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FATS)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                )
                meals.add(meal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return meals
    }

    /**
     * Replaces the existing user goal with a new one.
     * Only one goal is stored at a time.
     */
    fun saveUserGoals(calories: String, protein: String, carbs: String, fat: String) {
        val db = writableDatabase
        db.delete(TABLE_GOALS, null, null) // Ensures only one entry

        val values = ContentValues().apply {
            put(COLUMN_GOAL_CALORIES, calories)
            put(COLUMN_GOAL_PROTEIN, protein)
            put(COLUMN_GOAL_CARBS, carbs)
            put(COLUMN_GOAL_FAT, fat)
        }

        Log.d("GOAL_SAVE", "Saving: Calories=$calories, Protein=$protein, Carbs=$carbs, Fat=$fat")

        db.insert(TABLE_GOALS, null, values)
    }

    /**
     * Returns all meals logged for today's date.
     */
    fun getMealsForToday(): List<Meal> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MEALS WHERE $COLUMN_DATE = ?", arrayOf(today))

        val meals = mutableListOf<Meal>()
        if (cursor.moveToFirst()) {
            do {
                val meal = Meal(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                    protein = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROTEIN)),
                    carbs = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CARBS)),
                    fats = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FATS)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                )
                meals.add(meal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return meals
    }

    /**
     * Deletes a meal by its ID.
     */
    fun deleteMeal(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_MEALS, "$COLUMN_ID=?", arrayOf(id.toString())) > 0
    }

    companion object {
        private const val DATABASE_NAME = "meals.db"
        private const val DATABASE_VERSION = 1

        // Meal table columns
        const val TABLE_MEALS = "meals"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CALORIES = "calories"
        const val COLUMN_PROTEIN = "protein"
        const val COLUMN_CARBS = "carbs"
        const val COLUMN_FATS = "fats"
        const val COLUMN_DATE = "date"

        // Weight table columns
        const val TABLE_WEIGHTS = "weights"
        const val COLUMN_WEIGHT_ID = "id"
        const val COLUMN_WEIGHT_VALUE = "weight"
        const val COLUMN_TIMESTAMP = "timestamp"

        // Goals table columns
        const val TABLE_GOALS = "user_goals"
        const val COLUMN_GOALS_ID = "id"
        const val COLUMN_GOAL_CALORIES = "calories"
        const val COLUMN_GOAL_PROTEIN = "protein"
        const val COLUMN_GOAL_CARBS = "carbs"
        const val COLUMN_GOAL_FAT = "fat"
    }
}