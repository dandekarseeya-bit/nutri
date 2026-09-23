package com.example.nutrilensai;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;

public class AddFoodActivity extends AppCompatActivity {

    EditText etFoodName;
    EditText etWeight;
    EditText etCalories;
    EditText etProtein;
    EditText etCarbs;
    EditText etFat;
    EditText etFiber;

    Spinner spinnerMealType;

    Button btnSaveFood;
    TextView btnBack;

    FoodRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_food);

        // Initialize repository
        repository = new FoodRepository(this);

        // Find views
        etFoodName = findViewById(R.id.etFoodName);
        etWeight = findViewById(R.id.etWeight);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        etCarbs = findViewById(R.id.etCarbs);
        etFat = findViewById(R.id.etFat);
        etFiber = findViewById(R.id.etFiber);

        spinnerMealType = findViewById(R.id.spinnerMealType);

        btnSaveFood = findViewById(R.id.btnSaveFood);
        btnBack = findViewById(R.id.btnBack);

        // Meal type dropdown
        String[] mealTypes = {
                "Breakfast",
                "Lunch",
                "Dinner",
                "Snack"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mealTypes
        );

        spinnerMealType.setAdapter(adapter);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Save food
        btnSaveFood.setOnClickListener(v -> saveFood());
    }

    private void saveFood() {

        // Get food name
        String foodName = etFoodName.getText().toString().trim();

        if (foodName.isEmpty()) {
            etFoodName.setError("Enter food name");
            etFoodName.requestFocus();
            return;
        }

        // Get values
        String weightText = etWeight.getText().toString().trim();
        String caloriesText = etCalories.getText().toString().trim();
        String proteinText = etProtein.getText().toString().trim();
        String carbsText = etCarbs.getText().toString().trim();
        String fatText = etFat.getText().toString().trim();
        String fiberText = etFiber.getText().toString().trim();

        // Check empty fields
        if (weightText.isEmpty()) {
            etWeight.setError("Enter weight");
            etWeight.requestFocus();
            return;
        }

        if (caloriesText.isEmpty()) {
            etCalories.setError("Enter calories");
            etCalories.requestFocus();
            return;
        }

        if (proteinText.isEmpty()) {
            etProtein.setError("Enter protein");
            etProtein.requestFocus();
            return;
        }

        if (carbsText.isEmpty()) {
            etCarbs.setError("Enter carbohydrates");
            etCarbs.requestFocus();
            return;
        }

        if (fatText.isEmpty()) {
            etFat.setError("Enter fat");
            etFat.requestFocus();
            return;
        }

        if (fiberText.isEmpty()) {
            etFiber.setError("Enter fiber");
            etFiber.requestFocus();
            return;
        }

        // Convert text to numbers
        double weight = Double.parseDouble(weightText);
        double calories = Double.parseDouble(caloriesText);
        double protein = Double.parseDouble(proteinText);
        double carbs = Double.parseDouble(carbsText);
        double fat = Double.parseDouble(fatText);
        double fiber = Double.parseDouble(fiberText);

        // Get selected meal type
        String mealType = spinnerMealType.getSelectedItem().toString();

        // Get current date and time
        String date = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        String time = new SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
        ).format(new Date());

        // Create FoodRecord
        FoodRecord foodRecord = new FoodRecord(
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

        // Save to Room database
        repository.insert(foodRecord);

        Toast.makeText(
                this,
                "Food saved successfully!",
                Toast.LENGTH_SHORT
        ).show();

        // Return to dashboard
        finish();
    }
}