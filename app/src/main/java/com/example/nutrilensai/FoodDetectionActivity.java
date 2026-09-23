package com.example.nutrilensai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;
import com.example.nutrilensai.data.NutritionDatabase;
import com.example.nutrilensai.ml.FoodClassifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodDetectionActivity extends AppCompatActivity {


    private ImageView ivFoodImage;

    private FoodRepository repository;
    private FoodClassifier foodClassifier;

    private EditText etFoodWeight;

    private TextView tvDetectedFood;
    private TextView tvDetectionCalories;
    private TextView tvDetectionProtein;
    private TextView tvDetectionCarbs;
    private TextView tvDetectionFat;
    private TextView tvDetectionFiber;

    private Spinner spinnerDetectionMealType;

    private Button btnSaveDetectedFood;

    private static final int GALLERY_REQUEST = 100;
    private static final int CAMERA_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_detection);

        // =====================================================
        // DATABASE AND AI
        // =====================================================

        repository =
                new FoodRepository(this);

        foodClassifier =
                new FoodClassifier(this);

        // =====================================================
        // CONNECT VIEWS
        // =====================================================

        ivFoodImage =
                findViewById(R.id.ivFoodImage);

        etFoodWeight =
                findViewById(R.id.etFoodWeight);

        tvDetectedFood =
                findViewById(R.id.tvDetectedFood);

        tvDetectionCalories =
                findViewById(R.id.tvDetectionCalories);

        tvDetectionProtein =
                findViewById(R.id.tvDetectionProtein);

        tvDetectionCarbs =
                findViewById(R.id.tvDetectionCarbs);

        tvDetectionFat =
                findViewById(R.id.tvDetectionFat);

        tvDetectionFiber =
                findViewById(R.id.tvDetectionFiber);

        spinnerDetectionMealType =
                findViewById(
                        R.id.spinnerDetectionMealType
                );

        Button btnCamera =
                findViewById(R.id.btnCamera);

        Button btnGallery =
                findViewById(R.id.btnGallery);

        Button btnBack =
                findViewById(R.id.btnBack);

        btnSaveDetectedFood =
                findViewById(R.id.btnSaveDetectedFood);

        // =====================================================
        // MEAL TYPES
        // =====================================================

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

        spinnerDetectionMealType.setAdapter(
                mealAdapter
        );

        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> finish());

        // =====================================================
        // WEIGHT INPUT
        // =====================================================

        etFoodWeight.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        updateNutritionForWeight();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // =====================================================
        // SAVE DETECTED FOOD
        // =====================================================

        btnSaveDetectedFood.setOnClickListener(v -> {

            String foodName =
                    tvDetectedFood
                            .getText()
                            .toString()
                            .trim();

            String weightText =
                    etFoodWeight
                            .getText()
                            .toString()
                            .trim();

            // Check food
            if (foodName.isEmpty() ||
                    foodName.equalsIgnoreCase(
                            "No food detected yet")) {

                Toast.makeText(
                        this,
                        "Please detect a food first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Check weight
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
                        Double.parseDouble(
                                weightText
                        );

            } catch (NumberFormatException e) {

                Toast.makeText(
                        this,
                        "Please enter a valid weight.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Weight validation
            if (weight <= 0) {

                Toast.makeText(
                        this,
                        "Weight must be greater than 0.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Meal type
            String mealType =
                    spinnerDetectionMealType
                            .getSelectedItem()
                            .toString();

            // Nutrition
            NutritionDatabase.Nutrition nutrition =
                    NutritionDatabase.getNutrition(
                            foodName
                    );

            if (nutrition == null) {

                Toast.makeText(
                        this,
                        "Nutrition data not available for "
                                + foodName,
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // =================================================
            // CALCULATE NUTRITION
            // =================================================

            double multiplier =
                    weight / 100.0;

            double calculatedCalories =
                    nutrition.calories * multiplier;

            double calculatedProtein =
                    nutrition.protein * multiplier;

            double calculatedCarbs =
                    nutrition.carbs * multiplier;

            double calculatedFat =
                    nutrition.fat * multiplier;

            double calculatedFiber =
                    nutrition.fiber * multiplier;

            // =================================================
            // DATE AND TIME
            // =================================================

            Date now = new Date();

            String date =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(now);

            String time =
                    new SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                    ).format(now);

            // =================================================
            // CREATE FOOD RECORD
            // =================================================

            FoodRecord foodRecord =
                    new FoodRecord(
                            foodName,
                            weight,
                            calculatedCalories,
                            calculatedProtein,
                            calculatedCarbs,
                            calculatedFat,
                            calculatedFiber,
                            mealType,
                            date,
                            time
                    );

            // =================================================
            // SAVE TO ROOM
            // =================================================

            repository.insert(foodRecord);

            // Prevent accidental duplicate taps
            btnSaveDetectedFood.setEnabled(false);

            btnSaveDetectedFood.setText(
                    "Meal Saved ✓"
            );

            Toast.makeText(
                    this,
                    mealType +
                            " saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // =====================================================
        // GALLERY
        // =====================================================

        btnGallery.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Intent.ACTION_GET_CONTENT
                    );

            intent.setType("image/*");

            intent.addCategory(
                    Intent.CATEGORY_OPENABLE
            );

            startActivityForResult(
                    intent,
                    GALLERY_REQUEST
            );
        });

        // =====================================================
        // CAMERA
        // =====================================================

        btnCamera.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.CAMERA
                        },
                        CAMERA_REQUEST
                );

            } else {

                openCamera();
            }
        });
    }

// =====================================================
// UPDATE NUTRITION FOR WEIGHT
// =====================================================

    private void updateNutritionForWeight() {

        String foodName =
                tvDetectedFood
                        .getText()
                        .toString()
                        .trim();

        String weightText =
                etFoodWeight
                        .getText()
                        .toString()
                        .trim();

        if (foodName.isEmpty() ||
                foodName.equalsIgnoreCase(
                        "No food detected yet")) {

            return;
        }

        if (weightText.isEmpty()) {
            return;
        }

        double weight;

        try {

            weight =
                    Double.parseDouble(
                            weightText
                    );

        } catch (NumberFormatException e) {

            return;
        }

        if (weight <= 0) {
            return;
        }

        NutritionDatabase.Nutrition nutrition =
                NutritionDatabase.getNutrition(
                        foodName
                );

        if (nutrition == null) {
            return;
        }

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

        tvDetectionCalories.setText(
                formatNumber(calories) +
                        " kcal"
        );

        tvDetectionProtein.setText(
                formatNumber(protein) +
                        " g\nProtein"
        );

        tvDetectionCarbs.setText(
                formatNumber(carbs) +
                        " g\nCarbs"
        );

        tvDetectionFat.setText(
                formatNumber(fat) +
                        " g\nFat"
        );

        tvDetectionFiber.setText(
                formatNumber(fiber) +
                        " g\nFiber"
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
// OPEN CAMERA
// =====================================================

    private void openCamera() {

        Intent intent =
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );

        if (intent.resolveActivity(
                getPackageManager()
        ) != null) {

            startActivityForResult(
                    intent,
                    CAMERA_REQUEST
            );

        } else {

            Toast.makeText(
                    this,
                    "No camera app is available on this emulator.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

// =====================================================
// ACTIVITY RESULT
// =====================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode != RESULT_OK ||
                data == null) {

            return;
        }

        // Gallery
        if (requestCode == GALLERY_REQUEST) {

            Uri imageUri =
                    data.getData();

            if (imageUri != null) {

                ivFoodImage.setImageURI(
                        imageUri
                );

                analyzeGalleryImage(
                        imageUri
                );
            }
        }

        // Camera
        else if (requestCode == CAMERA_REQUEST) {

            if (data.getExtras() != null) {

                Object image =
                        data.getExtras()
                                .get("data");

                if (image instanceof Bitmap) {

                    Bitmap bitmap =
                            (Bitmap) image;

                    ivFoodImage.setImageBitmap(
                            bitmap
                    );

                    analyzeCameraImage(
                            bitmap
                    );
                }
            }
        }
    }

// =====================================================
// ANALYZE GALLERY IMAGE
// =====================================================

    private void analyzeGalleryImage(
            Uri imageUri
    ) {

        Toast.makeText(
                this,
                "Analyzing food...",
                Toast.LENGTH_SHORT
        ).show();

        foodClassifier.classify(
                imageUri,
                this,
                new FoodClassifier.ClassificationCallback() {

                    @Override
                    public void onResult(
                            String foodName
                    ) {

                        tvDetectedFood.setText(
                                foodName
                        );

                        // Allow saving a newly detected food
                        btnSaveDetectedFood.setEnabled(
                                true
                        );

                        btnSaveDetectedFood.setText(
                                "Save Meal"
                        );

                        showNutritionPer100g(
                                foodName
                        );

                        Toast.makeText(
                                FoodDetectionActivity.this,
                                "Detected: " +
                                        foodName,
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onError(
                            Exception e
                    ) {

                        Toast.makeText(
                                FoodDetectionActivity.this,
                                "Could not analyze image.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

// =====================================================
// ANALYZE CAMERA IMAGE
// =====================================================

    private void analyzeCameraImage(
            Bitmap bitmap
    ) {

        Toast.makeText(
                this,
                "Analyzing food...",
                Toast.LENGTH_SHORT
        ).show();

        foodClassifier.classify(
                bitmap,
                new FoodClassifier.ClassificationCallback() {

                    @Override
                    public void onResult(
                            String foodName
                    ) {

                        tvDetectedFood.setText(
                                foodName
                        );

                        // Allow saving newly detected food
                        btnSaveDetectedFood.setEnabled(
                                true
                        );

                        btnSaveDetectedFood.setText(
                                "Save Meal"
                        );

                        showNutritionPer100g(
                                foodName
                        );

                        Toast.makeText(
                                FoodDetectionActivity.this,
                                "Detected: " +
                                        foodName,
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onError(
                            Exception e
                    ) {

                        Toast.makeText(
                                FoodDetectionActivity.this,
                                "Could not analyze image.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

// =====================================================
// SHOW NUTRITION
// =====================================================

    private void showNutritionPer100g(
            String foodName
    ) {

        NutritionDatabase.Nutrition nutrition =
                NutritionDatabase.getNutrition(
                        foodName
                );

        if (nutrition == null) {

            Toast.makeText(
                    this,
                    "Nutrition data not available for "
                            + foodName,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String weightText =
                etFoodWeight
                        .getText()
                        .toString()
                        .trim();

        if (weightText.isEmpty()) {

            tvDetectionCalories.setText(
                    formatNumber(
                            nutrition.calories
                    ) +
                            " kcal\nCalories"
            );

            tvDetectionProtein.setText(
                    formatNumber(
                            nutrition.protein
                    ) +
                            " g\nProtein"
            );

            tvDetectionCarbs.setText(
                    formatNumber(
                            nutrition.carbs
                    ) +
                            " g\nCarbs"
            );

            tvDetectionFat.setText(
                    formatNumber(
                            nutrition.fat
                    ) +
                            " g\nFat"
            );

            tvDetectionFiber.setText(
                    formatNumber(
                            nutrition.fiber
                    ) +
                            " g\nFiber"
            );

            return;
        }

        updateNutritionForWeight();
    }

// =====================================================
// CAMERA PERMISSION
// =====================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == CAMERA_REQUEST) {

            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

// =====================================================
// DESTROY
// =====================================================

    @Override
    protected void onDestroy() {

        if (foodClassifier != null) {
            foodClassifier.close();
        }

        super.onDestroy();
    }

}
