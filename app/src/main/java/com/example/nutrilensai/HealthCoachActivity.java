package com.example.nutrilensai;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealthCoachActivity extends AppCompatActivity {


    private TextView tvCoachCalories;
    private TextView tvCoachProtein;
    private TextView tvCoachCarbs;
    private TextView tvCoachFat;
    private TextView tvCoachResponse;

    private EditText etCoachQuestion;

    private FoodRepository repository;

    private double todayCalories = 0;
    private double todayProtein = 0;
    private double todayCarbs = 0;
    private double todayFat = 0;

    private static final double DAILY_CALORIE_GOAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_health_coach);

        // =====================================================
        // CONNECT UI
        // =====================================================

        tvCoachCalories =
                findViewById(R.id.tvCoachCalories);

        tvCoachProtein =
                findViewById(R.id.tvCoachProtein);

        tvCoachCarbs =
                findViewById(R.id.tvCoachCarbs);

        tvCoachFat =
                findViewById(R.id.tvCoachFat);

        tvCoachResponse =
                findViewById(R.id.tvCoachResponse);

        etCoachQuestion =
                findViewById(R.id.etCoachQuestion);

        // =====================================================
        // DATABASE
        // =====================================================

        repository =
                new FoodRepository(this);

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button btnCoachBack =
                findViewById(R.id.btnCoachBack);

        btnCoachBack.setOnClickListener(v -> {
            finish();
        });

        // =====================================================
        // ASK COACH BUTTON
        // =====================================================

        Button btnAskCoach =
                findViewById(R.id.btnAskCoach);

        btnAskCoach.setOnClickListener(v -> {

            String question =
                    etCoachQuestion
                            .getText()
                            .toString()
                            .trim();

            if (question.isEmpty()) {

                Toast.makeText(
                        HealthCoachActivity.this,
                        "Please enter a question.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            generateCoachResponse(question);

            // Clear question box
            etCoachQuestion.setText("");
        });

        // =====================================================
        // LOAD TODAY'S NUTRITION
        // =====================================================

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

                    for (FoodRecord food : records) {

                        calories += food.calories;
                        protein += food.protein;
                        carbs += food.carbs;
                        fat += food.fat;
                    }

                    todayCalories = calories;
                    todayProtein = protein;
                    todayCarbs = carbs;
                    todayFat = fat;

                    runOnUiThread(() -> {

                        tvCoachCalories.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "Calories\n%.0f kcal",
                                        todayCalories
                                )
                        );

                        tvCoachProtein.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "Protein\n%.1f g",
                                        todayProtein
                                )
                        );

                        tvCoachCarbs.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "Carbs\n%.1f g",
                                        todayCarbs
                                )
                        );

                        tvCoachFat.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "Fat\n%.1f g",
                                        todayFat
                                )
                        );
                    });
                }
        );
    }

// =====================================================
// GENERATE COACH RESPONSE
// =====================================================

    private void generateCoachResponse(String question) {

        String q =
                question.toLowerCase(Locale.ROOT);

        double remainingCalories =
                DAILY_CALORIE_GOAL - todayCalories;

        if (remainingCalories < 0) {
            remainingCalories = 0;
        }

        String response;

        // =====================================================
        // CALORIES
        // =====================================================

        if (q.contains("calorie") ||
                q.contains("kcal") ||
                q.contains("how much have i eaten")) {

            if (todayCalories == 0) {

                response =
                        "🍽️ You haven't logged any food today yet.\n\n"
                                + "Add a meal using Add Food, "
                                + "Food Detection, Smart Scale, "
                                + "or Barcode Scanner and I'll track it.";

            } else if (todayCalories >= DAILY_CALORIE_GOAL) {

                response =
                        "🔥 You've consumed "
                                + formatNumber(todayCalories)
                                + " kcal today.\n\n"
                                + "Daily goal: "
                                + formatNumber(DAILY_CALORIE_GOAL)
                                + " kcal\n\n"
                                + "You've reached your calorie goal "
                                + "for today. If you eat more, consider "
                                + "choosing nutrient-rich foods and "
                                + "reasonable portions.";

            } else {

                response =
                        "🍎 You've consumed "
                                + formatNumber(todayCalories)
                                + " kcal today.\n\n"
                                + "Daily goal: "
                                + formatNumber(DAILY_CALORIE_GOAL)
                                + " kcal\n\n"
                                + "You have approximately "
                                + formatNumber(remainingCalories)
                                + " kcal remaining.";
            }
        }

        // =====================================================
        // PROTEIN
        // =====================================================

        else if (q.contains("protein")) {

            if (todayProtein < 40) {

                response =
                        "💪 Your protein intake today is "
                                + formatNumber(todayProtein)
                                + " g.\n\n"
                                + "Your logged protein is relatively "
                                + "low so far.\n\n"
                                + "You could add foods such as eggs, "
                                + "paneer, curd, milk, lentils, beans, "
                                + "soy, fish or chicken.";

            } else {

                response =
                        "💪 You've consumed "
                                + formatNumber(todayProtein)
                                + " g of protein today.\n\n"
                                + "Continue including protein sources "
                                + "throughout your meals, such as eggs, "
                                + "dairy, lentils, beans, soy, fish "
                                + "or chicken.";
            }
        }

        // =====================================================
        // CARBOHYDRATES
        // =====================================================

        else if (q.contains("carb") ||
                q.contains("carbohydrate")) {

            response =
                    "🍚 Your carbohydrate intake today is "
                            + formatNumber(todayCarbs)
                            + " g.\n\n"
                            + "For balanced meals, consider fruits, "
                            + "vegetables, whole grains, beans and "
                            + "lentils as nutrient-rich carbohydrate "
                            + "sources.";
        }

        // =====================================================
        // FAT
        // =====================================================

        else if (q.contains("fat")) {

            response =
                    "🥑 Your fat intake today is "
                            + formatNumber(todayFat)
                            + " g.\n\n"
                            + "Sources of unsaturated fats include "
                            + "nuts, seeds, avocado, olive oil "
                            + "and fish.";
        }

        // =====================================================
        // DINNER
        // =====================================================

        else if (q.contains("dinner")) {

            response =
                    "🍽️ Here's a balanced dinner idea:\n\n"
                            + "🥗 Vegetables or salad\n"
                            + "💪 Protein such as paneer, dal, eggs, "
                            + "tofu, fish or chicken\n"
                            + "🍚 A moderate portion of rice or roti\n"
                            + "🥛 Optional curd or yogurt\n\n"
                            + "You have approximately "
                            + formatNumber(remainingCalories)
                            + " kcal remaining today.";
        }

        // =====================================================
        // BREAKFAST
        // =====================================================

        else if (q.contains("breakfast")) {

            response =
                    "🌅 Here are some balanced breakfast ideas:\n\n"
                            + "🥚 Eggs with whole-grain toast\n"
                            + "🥣 Oats with milk and fruit\n"
                            + "🥛 Curd with fruit and nuts\n"
                            + "🥞 Besan chilla with vegetables\n\n"
                            + "Choose an option that fits your "
                            + "remaining calorie needs.";
        }

        // =====================================================
        // LUNCH
        // =====================================================

        else if (q.contains("lunch")) {

            response =
                    "☀️ A balanced lunch could include:\n\n"
                            + "🥗 Vegetables or salad\n"
                            + "💪 Dal, paneer, beans, eggs, "
                            + "fish or chicken\n"
                            + "🍚 Rice or roti\n"
                            + "🥛 Curd or yogurt\n\n"
                            + "Aim for a combination of protein, "
                            + "vegetables and carbohydrates.";
        }

        // =====================================================
        // WHAT SHOULD I EAT?
        // =====================================================

        else if (q.contains("what should i eat") ||
                q.contains("what can i eat") ||
                q.contains("what to eat") ||
                q.contains("suggest a meal")) {

            response =
                    "🥗 Based on your nutrition today:\n\n"
                            + "You have approximately "
                            + formatNumber(remainingCalories)
                            + " kcal remaining.\n\n"
                            + "A balanced meal could include:\n"
                            + "💪 Protein\n"
                            + "🥦 Vegetables\n"
                            + "🍚 A moderate carbohydrate portion\n"
                            + "🥑 A small amount of healthy fat.";
        }

        // =====================================================
        // HEALTHY FOOD
        // =====================================================

        else if (q.contains("healthy")) {

            response =
                    "🥗 Nutritious choices include:\n\n"
                            + "• Fruits and vegetables\n"
                            + "• Eggs and dairy\n"
                            + "• Lentils and beans\n"
                            + "• Whole grains\n"
                            + "• Nuts and seeds\n"
                            + "• Fish and lean protein\n\n"
                            + "Try to include a variety of "
                            + "food groups in your meals.";
        }

        // =====================================================
        // DAILY SUMMARY
        // =====================================================

        else if (q.contains("summary") ||
                q.contains("today") ||
                q.contains("diet") ||
                q.contains("nutrition")) {

            response =
                    "📊 Your nutrition summary today:\n\n"
                            + "🔥 Calories: "
                            + formatNumber(todayCalories)
                            + " kcal\n"
                            + "💪 Protein: "
                            + formatNumber(todayProtein)
                            + " g\n"
                            + "🍚 Carbs: "
                            + formatNumber(todayCarbs)
                            + " g\n"
                            + "🥑 Fat: "
                            + formatNumber(todayFat)
                            + " g\n\n"
                            + "🎯 Daily calorie goal: "
                            + formatNumber(DAILY_CALORIE_GOAL)
                            + " kcal\n"
                            + "Remaining: "
                            + formatNumber(remainingCalories)
                            + " kcal";
        }

        // =====================================================
        // GENERAL QUESTION
        // =====================================================

        else {

            response =
                    "👩‍⚕️ I'm your NutriLens AI Health Coach.\n\n"
                            + "I can help you with:\n\n"
                            + "🔥 Calories\n"
                            + "💪 Protein\n"
                            + "🍚 Carbohydrates\n"
                            + "🥑 Fat\n"
                            + "🍽️ Meal suggestions\n"
                            + "📊 Daily nutrition summary\n\n"
                            + "Try asking:\n"
                            + "\"How many calories have I eaten?\"\n"
                            + "\"What should I eat for dinner?\"\n"
                            + "\"How much protein have I eaten?\"";
        }

        // =====================================================
        // SHOW RESPONSE
        // =====================================================

        tvCoachResponse.setText(response);

        tvCoachResponse.setVisibility(
                TextView.VISIBLE
        );
    }

// =====================================================
// FORMAT NUMBERS
// =====================================================

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

// =====================================================
// REFRESH WHEN RETURNING
// =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (repository != null) {
            loadTodayNutrition();
        }
    }


}
