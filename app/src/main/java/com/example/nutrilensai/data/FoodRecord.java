package com.example.nutrilensai.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_records")
public class FoodRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String foodName;
    public double weight;
    public double calories;
    public double protein;
    public double carbs;
    public double fat;
    public double fiber;
    public String mealType;
    public String date;
    public String time;

    public FoodRecord(
            String foodName,
            double weight,
            double calories,
            double protein,
            double carbs,
            double fat,
            double fiber,
            String mealType,
            String date,
            String time
    ) {
        this.foodName = foodName;
        this.weight = weight;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.mealType = mealType;
        this.date = date;
        this.time = time;
    }
}