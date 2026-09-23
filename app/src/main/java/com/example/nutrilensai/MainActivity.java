package com.example.nutrilensai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private FoodRepository repository;

    private TextView tvCalories;
    private TextView tvCaloriePercent;
    private TextView tvProtein;
    private TextView tvCarbs;
    private TextView tvFat;

    private ProgressBar calorieProgress;

    private static final int DAILY_CALORIE_GOAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        repository = new FoodRepository(this);

        // =====================================================
        // ADD FOOD
        // =====================================================

        Button btnAddFood =
                findViewById(R.id.btnAddFood);

        btnAddFood.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddFoodActivity.class
            );

            startActivity(intent);
        });

        // =====================================================
        // FOOD HISTORY
        // =====================================================

        Button btnFoodHistory =
                findViewById(R.id.btnFoodHistory);

        btnFoodHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    FoodHistoryActivity.class
            );

            startActivity(intent);
        });

        // =====================================================
        // AI FOOD DETECTION
        // =====================================================

        TextView navFood =
                findViewById(R.id.navFood);

        navFood.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    FoodDetectionActivity.class
            );

            startActivity(intent);
        });

        // =====================================================
        // SMART SCALE
        // =====================================================

        TextView navScale =
                findViewById(R.id.navScale);

        navScale.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SmartScaleActivity.class
            );

            startActivity(intent);
        });

        // =====================================================
        // AI HEALTH COACH
        // =====================================================

        TextView navCoach =
                findViewById(R.id.navCoach);

        navCoach.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    HealthCoachActivity.class
            );

            startActivity(intent);
        });

        // =====================================================
        // DASHBOARD VIEWS
        // =====================================================

        TextView btnProfileLogout = findViewById(R.id.btnProfileLogout);
        btnProfileLogout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        tvCalories =
                findViewById(R.id.tvCalories);

        tvCaloriePercent =
                findViewById(R.id.tvCaloriePercent);

        tvProtein =
                findViewById(R.id.tvProtein);

        tvCarbs =
                findViewById(R.id.tvCarbs);

        tvFat =
                findViewById(R.id.tvFat);

        calorieProgress =
                findViewById(R.id.calorieProgress);

        // Set progress bar maximum
        calorieProgress.setMax(DAILY_CALORIE_GOAL);

        // Load today's nutrition
        loadTodayNutrition();
    }

// =====================================================
// LOAD TODAY'S NUTRITION
// =====================================================

    private void loadTodayNutrition() {

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        repository.getFoodRecordsByDate(
                today,
                records -> {

                    double calories = 0;
                    double protein = 0;
                    double carbs = 0;
                    double fat = 0;

                    // Add all today's food
                    for (FoodRecord food : records) {

                        calories += food.calories;
                        protein += food.protein;
                        carbs += food.carbs;
                        fat += food.fat;
                    }

                    double finalCalories = calories;
                    double finalProtein = protein;
                    double finalCarbs = carbs;
                    double finalFat = fat;

                    runOnUiThread(() -> {

                        // =================================================
                        // CALORIES
                        // =================================================

                        tvCalories.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.0f / %d kcal",
                                        finalCalories,
                                        DAILY_CALORIE_GOAL
                                )
                        );

                        // Calculate percentage
                        int percentage =
                                (int) (
                                        (finalCalories /
                                                DAILY_CALORIE_GOAL)
                                                * 100
                                );

                        // Don't allow percentage below 0 or above 100
                        percentage =
                                Math.max(
                                        0,
                                        Math.min(
                                                percentage,
                                                100
                                        )
                                );

                        tvCaloriePercent.setText(
                                percentage
                                        + "% of Daily Calories"
                        );

                        // =================================================
                        // PROGRESS BAR
                        // =================================================

                        int progress =
                                (int) Math.min(
                                        finalCalories,
                                        DAILY_CALORIE_GOAL
                                );

                        calorieProgress.setProgress(
                                Math.max(0, progress)
                        );

                        // =================================================
                        // PROTEIN
                        // =================================================

                        tvProtein.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.1f g",
                                        finalProtein
                                )
                        );

                        // =================================================
                        // CARBS
                        // =================================================

                        tvCarbs.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.1f g",
                                        finalCarbs
                                )
                        );

                        // =================================================
                        // FAT
                        // =================================================

                        tvFat.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.1f g",
                                        finalFat
                                )
                        );
                    });
                }
        );
    }

// =====================================================
// REFRESH DASHBOARD
// =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (repository != null) {

            loadTodayNutrition();
        }
    }


}
