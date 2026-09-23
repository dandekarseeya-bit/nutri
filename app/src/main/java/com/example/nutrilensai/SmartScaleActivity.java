package com.example.nutrilensai;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;
import com.example.nutrilensai.data.NutritionDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmartScaleActivity extends AppCompatActivity {

    private EditText etScaleWeight;
    private EditText etScaleFoodName;

    private Spinner spinnerScaleMealType;

    private TextView tvScaleWeight;
    private TextView tvScaleNutrition;
    private TextView tvScaleStatus;

    private FoodRepository repository;

    private double calculatedCalories = 0;
    private double calculatedProtein = 0;
    private double calculatedCarbs = 0;
    private double calculatedFat = 0;
    private double calculatedFiber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smart_scale);

        repository = new FoodRepository(this);

        // Find views

        etScaleWeight =
                findViewById(R.id.etScaleWeight);

        etScaleFoodName =
                findViewById(R.id.etScaleFoodName);

        spinnerScaleMealType =
                findViewById(R.id.spinnerScaleMealType);

        tvScaleWeight =
                findViewById(R.id.tvScaleWeight);

        tvScaleNutrition =
                findViewById(R.id.tvScaleNutrition);

        tvScaleStatus =
                findViewById(R.id.tvScaleStatus);

        Button btnScaleBack =
                findViewById(R.id.btnScaleBack);

        Button btnConnectScale =
                findViewById(R.id.btnConnectScale);

        Button btnCalculateScale =
                findViewById(R.id.btnCalculateScale);

        Button btnSaveScaleMeal =
                findViewById(R.id.btnSaveScaleMeal);


        // Meal types

        String[] mealTypes = {
                "Breakfast",
                "Lunch",
                "Dinner",
                "Snack"
        };

        ArrayAdapter<String> mealAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        mealTypes
                );

        mealAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerScaleMealType.setAdapter(
                mealAdapter
        );


        // Back button

        btnScaleBack.setOnClickListener(v -> finish());


        // Connect scale

        btnConnectScale.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Bluetooth scale connection will be added next.",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // Calculate nutrition

        btnCalculateScale.setOnClickListener(v ->
                calculateNutrition()
        );


        // Save meal

        btnSaveScaleMeal.setOnClickListener(v ->
                saveMeal()
        );
    }


    private void calculateNutrition() {

        String foodName =
                etScaleFoodName
                        .getText()
                        .toString()
                        .trim();

        String weightText =
                etScaleWeight
                        .getText()
                        .toString()
                        .trim();


        if (foodName.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter food name.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (weightText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter food weight.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        double weight;

        try {

            weight =
                    Double.parseDouble(weightText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid weight.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (weight <= 0) {

            Toast.makeText(
                    this,
                    "Weight must be greater than 0.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        NutritionDatabase.Nutrition nutrition =
                NutritionDatabase.getNutrition(
                        foodName
                );


        if (nutrition == null) {

            tvScaleNutrition.setText(
                    "Nutrition data not available for:\n"
                            + foodName
            );

            Toast.makeText(
                    this,
                    "Food not found in nutrition database.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Calculate based on 100 grams

        double multiplier =
                weight / 100.0;


        calculatedCalories =
                nutrition.calories * multiplier;

        calculatedProtein =
                nutrition.protein * multiplier;

        calculatedCarbs =
                nutrition.carbs * multiplier;

        calculatedFat =
                nutrition.fat * multiplier;

        calculatedFiber =
                nutrition.fiber * multiplier;


        // Show weight

        tvScaleWeight.setText(
                formatNumber(weight) + " g"
        );


        // Show nutrition

        String nutritionText =
                "Calories: "
                        + formatNumber(calculatedCalories)
                        + " kcal\n\n"

                        + "Protein: "
                        + formatNumber(calculatedProtein)
                        + " g\n\n"

                        + "Carbs: "
                        + formatNumber(calculatedCarbs)
                        + " g\n\n"

                        + "Fat: "
                        + formatNumber(calculatedFat)
                        + " g\n\n"

                        + "Fiber: "
                        + formatNumber(calculatedFiber)
                        + " g";


        tvScaleNutrition.setText(
                nutritionText
        );


        Toast.makeText(
                this,
                "Nutrition calculated!",
                Toast.LENGTH_SHORT
        ).show();
    }


    private void saveMeal() {

        String foodName =
                etScaleFoodName
                        .getText()
                        .toString()
                        .trim();

        String weightText =
                etScaleWeight
                        .getText()
                        .toString()
                        .trim();


        if (foodName.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter food name.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (weightText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter food weight.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        double weight;

        try {

            weight =
                    Double.parseDouble(weightText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid weight.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (weight <= 0) {

            Toast.makeText(
                    this,
                    "Weight must be greater than 0.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        NutritionDatabase.Nutrition nutrition =
                NutritionDatabase.getNutrition(
                        foodName
                );


        if (nutrition == null) {

            Toast.makeText(
                    this,
                    "Nutrition data not available.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Calculate again before saving

        double multiplier =
                weight / 100.0;


        double calories =
                nutrition.calories * multiplier;

        double protein =
                nutrition.protein * multiplier;

        double carbs =
                nutrition.carbs * multiplier;

        double fat =
                nutrition.fat * multiplier;

        double fiber =
                nutrition.fiber * multiplier;


        String mealType =
                spinnerScaleMealType
                        .getSelectedItem()
                        .toString();


        String date =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());


        String time =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                ).format(new Date());


        FoodRecord foodRecord =
                new FoodRecord(
                        foodName,
                        weight,
                        calories,
                        protein,
                        carbs,
                        fat,
                        fiber,
                        mealType,
                        date,
                        time
                );


        repository.insert(foodRecord);


        Toast.makeText(
                this,
                mealType + " saved successfully!",
                Toast.LENGTH_SHORT
        ).show();
    }


    private String formatNumber(double value) {

        if (value == Math.floor(value)) {

            return String.format(
                    Locale.getDefault(),
                    "%.0f",
                    value
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.1f",
                value
        );
    }
}