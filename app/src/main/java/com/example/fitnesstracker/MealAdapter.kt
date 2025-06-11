package com.example.fitnesstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.data.Meal

// Adapter to bind a list of Meal objects to a RecyclerView
class MealAdapter(private val mealList: List<Meal>) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    // ViewHolder describes the item layout and metadata
    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealNameText: TextView = itemView.findViewById(R.id.mealNameText)
        val macrosText: TextView = itemView.findViewById(R.id.macrosText)
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        // Inflate the layout for a single meal item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    // Called to bind data to a specific ViewHolder
    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]
        holder.mealNameText.text = meal.name
        holder.macrosText.text = "Cals: ${meal.calories}, P: ${meal.protein}, C: ${meal.carbs}, F: ${meal.fats}"
    }

    // Returns total number of items
    override fun getItemCount(): Int = mealList.size
}