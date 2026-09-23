package com.example.nutrilensai;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {

    private FoodRepository repository;

    private TextView tvWeeklyCalories;
    private TextView tvAverageCalories;

    private TextView tvWeeklyGoalStatus;

    private TextView tvWeeklyProtein;
    private TextView tvWeeklyCarbs;
    private TextView tvWeeklyFat;

    private TextView tvDay1;
    private TextView tvDay2;
    private TextView tvDay3;
    private TextView tvDay4;
    private TextView tvDay5;
    private TextView tvDay6;
    private TextView tvDay7;

    private Button btnAnalyticsBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analytics);

        repository = new FoodRepository(this);

        btnAnalyticsBack =
                findViewById(R.id.btnAnalyticsBack);

        tvWeeklyCalories =
                findViewById(R.id.tvWeeklyCalories);

        tvAverageCalories =
                findViewById(R.id.tvAverageCalories);

        tvWeeklyGoalStatus =
                findViewById(R.id.tvWeeklyGoalStatus);

        tvWeeklyProtein =
                findViewById(R.id.tvWeeklyProtein);

        tvWeeklyCarbs =
                findViewById(R.id.tvWeeklyCarbs);

        tvWeeklyFat =
                findViewById(R.id.tvWeeklyFat);

        tvDay1 =
                findViewById(R.id.tvDay1);

        tvDay2 =
                findViewById(R.id.tvDay2);

        tvDay3 =
                findViewById(R.id.tvDay3);

        tvDay4 =
                findViewById(R.id.tvDay4);

        tvDay5 =
                findViewById(R.id.tvDay5);

        tvDay6 =
                findViewById(R.id.tvDay6);

        tvDay7 =
                findViewById(R.id.tvDay7);

        btnAnalyticsBack.setOnClickListener(v -> finish());

        loadWeeklyAnalytics();
    }

    private void loadWeeklyAnalytics() {

        repository.getAllFoodRecords(
                new FoodRepository.RepositoryCallback<List<FoodRecord>>() {

                    @Override
                    public void onComplete(List<FoodRecord> records) {

                        double[] dailyCalories =
                                new double[7];

                        double totalProtein = 0;
                        double totalCarbs = 0;
                        double totalFat = 0;

                        for (FoodRecord food : records) {

                            if (food.date == null) {
                                continue;
                            }

                            for (int i = 0; i < 7; i++) {

                                Calendar target =
                                        Calendar.getInstance();

                                target.add(
                                        Calendar.DAY_OF_YEAR,
                                        i - 6
                                );

                                String targetDate =
                                        new SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.getDefault()
                                        ).format(
                                                target.getTime()
                                        );

                                if (food.date.equals(targetDate)) {

                                    dailyCalories[i] +=
                                            food.calories;

                                    totalProtein +=
                                            food.protein;

                                    totalCarbs +=
                                            food.carbs;

                                    totalFat +=
                                            food.fat;

                                    break;
                                }
                            }
                        }

                        double totalCalories = 0;

                        for (double calories : dailyCalories) {
                            totalCalories += calories;
                        }

                        double averageCalories =
                                totalCalories / 7.0;

                        // Final values for UI thread

                        final double finalTotalCalories =
                                totalCalories;

                        final double finalAverageCalories =
                                averageCalories;

                        final double finalTotalProtein =
                                totalProtein;

                        final double finalTotalCarbs =
                                totalCarbs;

                        final double finalTotalFat =
                                totalFat;

                        final double[] finalDailyCalories =
                                dailyCalories;

                        runOnUiThread(() -> {

                            // -------------------------
                            // CALORIE SUMMARY
                            // -------------------------

                            tvWeeklyCalories.setText(
                                    formatNumber(
                                            finalTotalCalories
                                    )
                                            + " kcal\nTotal Calories"
                            );

                            tvAverageCalories.setText(
                                    formatNumber(
                                            finalAverageCalories
                                    )
                                            + " kcal\nDaily Average"
                            );

                            // -------------------------
                            // DAILY CALORIE GOAL
                            // -------------------------

                            double goalDifference =
                                    finalAverageCalories - 2000;

                            if (goalDifference > 0) {

                                tvWeeklyGoalStatus.setText(
                                        "Average is "
                                                + formatNumber(
                                                goalDifference
                                        )
                                                + " kcal above your daily goal"
                                );

                            } else if (goalDifference < 0) {

                                tvWeeklyGoalStatus.setText(
                                        "Average is "
                                                + formatNumber(
                                                Math.abs(
                                                        goalDifference
                                                )
                                        )
                                                + " kcal below your daily goal"
                                );

                            } else {

                                tvWeeklyGoalStatus.setText(
                                        "Average matches your 2000 kcal daily goal"
                                );
                            }

                            // -------------------------
                            // MACRONUTRIENTS
                            // -------------------------

                            tvWeeklyProtein.setText(
                                    formatNumber(
                                            finalTotalProtein
                                    )
                                            + " g\nProtein"
                            );

                            tvWeeklyCarbs.setText(
                                    formatNumber(
                                            finalTotalCarbs
                                    )
                                            + " g\nCarbs"
                            );

                            tvWeeklyFat.setText(
                                    formatNumber(
                                            finalTotalFat
                                    )
                                            + " g\nFat"
                            );

                            // -------------------------
                            // LAST 7 DAYS
                            // -------------------------

                            updateDayText(
                                    tvDay1,
                                    finalDailyCalories[0],
                                    6
                            );

                            updateDayText(
                                    tvDay2,
                                    finalDailyCalories[1],
                                    5
                            );

                            updateDayText(
                                    tvDay3,
                                    finalDailyCalories[2],
                                    4
                            );

                            updateDayText(
                                    tvDay4,
                                    finalDailyCalories[3],
                                    3
                            );

                            updateDayText(
                                    tvDay5,
                                    finalDailyCalories[4],
                                    2
                            );

                            updateDayText(
                                    tvDay6,
                                    finalDailyCalories[5],
                                    1
                            );

                            updateDayText(
                                    tvDay7,
                                    finalDailyCalories[6],
                                    0
                            );
                        });
                    }
                }
        );
    }

    private void updateDayText(
            TextView textView,
            double calories,
            int daysAgo
    ) {

        Calendar calendar =
                Calendar.getInstance();

        calendar.add(
                Calendar.DAY_OF_YEAR,
                -daysAgo
        );

        String dayName =
                new SimpleDateFormat(
                        "EEE, dd MMM",
                        Locale.getDefault()
                ).format(
                        calendar.getTime()
                );

        textView.setText(
                dayName
                        + "    "
                        + formatNumber(calories)
                        + " kcal"
        );
    }

    private String formatNumber(double value) {

        return String.format(
                Locale.getDefault(),
                "%.0f",
                value
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (repository != null) {
            loadWeeklyAnalytics();
        }
    }
}
