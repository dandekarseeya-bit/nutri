package com.example.nutrilensai;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodHistoryActivity extends AppCompatActivity {


    private RecyclerView recyclerFoodHistory;

    private TextView tvHistoryInfo;

    private TextView tvHistoryCalories;
    private TextView tvHistoryProtein;
    private TextView tvHistoryCarbs;
    private TextView tvHistoryFat;

    private Button btnBack;

    private FoodRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_history);

        // =====================================================
        // CONNECT VIEWS
        // =====================================================

        recyclerFoodHistory =
                findViewById(R.id.recyclerFoodHistory);

        tvHistoryInfo =
                findViewById(R.id.tvHistoryInfo);

        tvHistoryCalories =
                findViewById(R.id.tvHistoryCalories);

        tvHistoryProtein =
                findViewById(R.id.tvHistoryProtein);

        tvHistoryCarbs =
                findViewById(R.id.tvHistoryCarbs);

        tvHistoryFat =
                findViewById(R.id.tvHistoryFat);

        btnBack =
                findViewById(R.id.btnBack);

        // =====================================================
        // DATABASE
        // =====================================================

        repository =
                new FoodRepository(this);

        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerFoodHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> finish());

        // =====================================================
        // LOAD HISTORY
        // =====================================================

        loadFoodHistory();
    }

// =========================================================
// LOAD FOOD HISTORY
// =========================================================

    private void loadFoodHistory() {

        repository.getAllFoodRecords(
                new FoodRepository.RepositoryCallback<List<FoodRecord>>() {

                    @Override
                    public void onComplete(
                            List<FoodRecord> records
                    ) {

                        runOnUiThread(() -> {

                            // =================================
                            // SET RECYCLER VIEW
                            // =================================

                            FoodHistoryAdapter adapter =
                                    new FoodHistoryAdapter(
                                            records,
                                            repository
                                    );

                            recyclerFoodHistory.setAdapter(
                                    adapter
                            );

                            // =================================
                            // NUMBER OF RECORDS
                            // =================================

                            tvHistoryInfo.setText(
                                    records.size()
                                            + " food records saved"
                            );

                            // =================================
                            // CALCULATE TODAY'S TOTALS
                            // =================================

                            calculateTodaySummary(
                                    records
                            );
                        });
                    }
                }
        );
    }

// =========================================================
// CALCULATE TODAY'S NUTRITION
// =========================================================

    private void calculateTodaySummary(
            List<FoodRecord> records
    ) {

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        double calories = 0;
        double protein = 0;
        double carbs = 0;
        double fat = 0;

        for (FoodRecord food : records) {

            if (food.date != null &&
                    food.date.equals(today)) {

                calories += food.calories;
                protein += food.protein;
                carbs += food.carbs;
                fat += food.fat;
            }
        }

        // =================================
        // UPDATE CALORIES
        // =================================

        tvHistoryCalories.setText(
                formatNumber(calories)
                        + " kcal"
        );

        // =================================
        // UPDATE PROTEIN
        // =================================

        tvHistoryProtein.setText(
                formatNumber(protein)
                        + " g\nProtein"
        );

        // =================================
        // UPDATE CARBS
        // =================================

        tvHistoryCarbs.setText(
                formatNumber(carbs)
                        + " g\nCarbs"
        );

        // =================================
        // UPDATE FAT
        // =================================

        tvHistoryFat.setText(
                formatNumber(fat)
                        + " g\nFat"
        );
    }

// =========================================================
// FORMAT NUMBERS
// =========================================================

    private String formatNumber(
            double value
    ) {

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

// =========================================================
// REFRESH WHEN RETURNING TO SCREEN
// =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (repository != null) {
            loadFoodHistory();
        }
    }


}
