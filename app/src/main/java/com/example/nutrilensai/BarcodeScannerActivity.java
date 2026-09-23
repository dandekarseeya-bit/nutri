package com.example.nutrilensai;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarcodeScannerActivity extends AppCompatActivity {


    private static final int CAMERA_PERMISSION_REQUEST = 200;

    private PreviewView previewView;

    private BarcodeScanner barcodeScanner;

    private ExecutorService cameraExecutor;
    private ExecutorService networkExecutor;

    private boolean barcodeFound = false;

    private TextView tvBarcodeNumber;
    private TextView tvBarcodeProduct;
    private TextView tvBarcodeCalories;
    private TextView tvBarcodeProtein;
    private TextView tvBarcodeCarbs;
    private TextView tvBarcodeFat;

    private EditText etBarcodeWeight;

    private Spinner spinnerBarcodeMealType;

    private String scannedBarcode = "";

    private double caloriesPer100g = 0;
    private double proteinPer100g = 0;
    private double carbsPer100g = 0;
    private double fatPer100g = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_scanner);

        // -----------------------------
        // CAMERA PREVIEW
        // -----------------------------

        previewView = findViewById(R.id.previewView);

        // -----------------------------
        // TEXT VIEWS
        // -----------------------------

        tvBarcodeNumber =
                findViewById(R.id.tvBarcodeNumber);

        tvBarcodeProduct =
                findViewById(R.id.tvBarcodeProduct);

        tvBarcodeCalories =
                findViewById(R.id.tvBarcodeCalories);

        tvBarcodeProtein =
                findViewById(R.id.tvBarcodeProtein);

        tvBarcodeCarbs =
                findViewById(R.id.tvBarcodeCarbs);

        tvBarcodeFat =
                findViewById(R.id.tvBarcodeFat);

        // -----------------------------
        // WEIGHT
        // -----------------------------

        etBarcodeWeight =
                findViewById(R.id.etBarcodeWeight);

        // -----------------------------
        // MEAL TYPE
        // -----------------------------

        spinnerBarcodeMealType =
                findViewById(R.id.spinnerBarcodeMealType);

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

        spinnerBarcodeMealType.setAdapter(
                mealAdapter
        );

        // -----------------------------
        // BACK BUTTON
        // -----------------------------

        Button btnBarcodeBack =
                findViewById(R.id.btnBarcodeBack);

        btnBarcodeBack.setOnClickListener(v -> finish());

        // -----------------------------
        // SCAN BUTTON
        // -----------------------------

        Button btnStartBarcodeScan =
                findViewById(R.id.btnStartBarcodeScan);

        btnStartBarcodeScan.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.CAMERA
                        },
                        CAMERA_PERMISSION_REQUEST
                );

            } else {

                startBarcodeScanner();

            }

        });

        // -----------------------------
        // SAVE BUTTON
        // -----------------------------

        Button btnSaveBarcodeMeal =
                findViewById(R.id.btnSaveBarcodeMeal);

        btnSaveBarcodeMeal.setOnClickListener(v -> {
            saveBarcodeMeal();
        });

        // -----------------------------
        // EXECUTORS
        // -----------------------------

        cameraExecutor =
                Executors.newSingleThreadExecutor();

        networkExecutor =
                Executors.newSingleThreadExecutor();

        // -----------------------------
        // ML KIT BARCODE SCANNER
        // -----------------------------

        barcodeScanner =
                BarcodeScanning.getClient();
    }

// =========================================================
// START BARCODE SCANNER
// =========================================================

    private void startBarcodeScanner() {

        barcodeFound = false;

        scannedBarcode = "";

        tvBarcodeNumber.setText(
                "Barcode: Scanning..."
        );

        tvBarcodeProduct.setText(
                "Product: Searching..."
        );

        tvBarcodeCalories.setText(
                "Calories: --"
        );

        tvBarcodeProtein.setText(
                "Protein: --"
        );

        tvBarcodeCarbs.setText(
                "Carbs: --"
        );

        tvBarcodeFat.setText(
                "Fat: --"
        );

        ListenableFuture<ProcessCameraProvider>
                cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                // -----------------------------
                // CAMERA PREVIEW
                // -----------------------------

                Preview preview =
                        new Preview.Builder().build();

                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider()
                );

                // -----------------------------
                // CAMERA
                // -----------------------------

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                // -----------------------------
                // IMAGE ANALYSIS
                // -----------------------------

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis
                                                .STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();

                imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        this::analyzeImage
                );

                // -----------------------------
                // CONNECT CAMERA
                // -----------------------------

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Unable to start camera: "
                                + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }

        }, ContextCompat.getMainExecutor(this));
    }

// =========================================================
// ANALYZE CAMERA IMAGE
// =========================================================

    private void analyzeImage(
            ImageProxy imageProxy
    ) {

        // If barcode was already found,
        // stop processing new frames.

        if (barcodeFound) {

            imageProxy.close();

            return;
        }

        // Get the actual Android camera image.

        android.media.Image mediaImage =
                imageProxy.getImage();

        // Sometimes the camera frame can be empty.

        if (mediaImage == null) {

            imageProxy.close();

            return;
        }

        // Convert camera image into
        // an ML Kit InputImage.

        InputImage image =
                InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy
                                .getImageInfo()
                                .getRotationDegrees()
                );

        // Send image to ML Kit.

        barcodeScanner
                .process(image)

                .addOnSuccessListener(barcodes -> {

                    // No barcode found in this frame.

                    if (barcodes.isEmpty()) {
                        return;
                    }

                    // Get first detected barcode.

                    String barcodeValue =
                            barcodes
                                    .get(0)
                                    .getRawValue();

                    // Ignore empty barcode.

                    if (barcodeValue == null ||
                            barcodeValue.isEmpty()) {

                        return;
                    }

                    // Barcode successfully found.

                    barcodeFound = true;

                    scannedBarcode =
                            barcodeValue;

                    // Display barcode number.

                    runOnUiThread(() -> {

                        tvBarcodeNumber.setText(
                                "Barcode: "
                                        + barcodeValue
                        );

                        tvBarcodeProduct.setText(
                                "Product: Looking up..."
                        );

                    });

                    // Search Open Food Facts.

                    fetchProductFromOpenFoodFacts(
                            barcodeValue
                    );

                })

                .addOnFailureListener(e -> {

                    // Ignore individual camera-frame errors.

                })

                .addOnCompleteListener(task -> {

                    // VERY IMPORTANT:
                    // Always release the camera frame.

                    imageProxy.close();

                });
    }

// =========================================================
// OPEN FOOD FACTS LOOKUP
// =========================================================

    private void fetchProductFromOpenFoodFacts(
            String barcode
    ) {

        networkExecutor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String apiUrl =
                        "https://world.openfoodfacts.org/api/v2/product/"
                                + barcode
                                + ".json";

                URL url =
                        new URL(apiUrl);

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod(
                        "GET"
                );

                connection.setConnectTimeout(
                        10000
                );

                connection.setReadTimeout(
                        10000
                );

                connection.setRequestProperty(
                        "User-Agent",
                        "NutriLensAI/1.0 Android"
                );

                int responseCode =
                        connection.getResponseCode();

                if (responseCode !=
                        HttpURLConnection.HTTP_OK) {

                    showProductError(
                            "Unable to contact food database."
                    );

                    return;
                }

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }

                reader.close();

                // Convert response into JSON.

                JSONObject json =
                        new JSONObject(
                                response.toString()
                        );

                // Check whether product exists.

                int status =
                        json.optInt(
                                "status",
                                0
                        );

                if (status != 1) {

                    showProductError(
                            "Product not found in Open Food Facts."
                    );

                    return;
                }

                // Get product object.

                JSONObject product =
                        json.optJSONObject(
                                "product"
                        );

                if (product == null) {

                    showProductError(
                            "Product information unavailable."
                    );

                    return;
                }

                // Product name.

                String productName =
                        product.optString(
                                "product_name",
                                "Unknown product"
                        );

                // Nutrition information.

                JSONObject nutriments =
                        product.optJSONObject(
                                "nutriments"
                        );

                if (nutriments == null) {

                    showProductError(
                            "Nutrition information unavailable."
                    );

                    return;
                }

                // Nutrition per 100g.

                double calories =
                        getNutritionValue(
                                nutriments,
                                "energy-kcal_100g"
                        );

                double protein =
                        getNutritionValue(
                                nutriments,
                                "proteins_100g"
                        );

                double carbs =
                        getNutritionValue(
                                nutriments,
                                "carbohydrates_100g"
                        );

                double fat =
                        getNutritionValue(
                                nutriments,
                                "fat_100g"
                        );

                // Store nutrition values.

                caloriesPer100g =
                        calories;

                proteinPer100g =
                        protein;

                carbsPer100g =
                        carbs;

                fatPer100g =
                        fat;

                // Display product information.

                runOnUiThread(() -> {

                    tvBarcodeProduct.setText(
                            "Product: "
                                    + productName
                    );

                    tvBarcodeCalories.setText(
                            "Calories: "
                                    + formatNumber(
                                    calories
                            )
                                    + " kcal / 100 g"
                    );

                    tvBarcodeProtein.setText(
                            "Protein: "
                                    + formatNumber(
                                    protein
                            )
                                    + " g / 100 g"
                    );

                    tvBarcodeCarbs.setText(
                            "Carbs: "
                                    + formatNumber(
                                    carbs
                            )
                                    + " g / 100 g"
                    );

                    tvBarcodeFat.setText(
                            "Fat: "
                                    + formatNumber(
                                    fat
                            )
                                    + " g / 100 g"
                    );

                    Toast.makeText(
                            this,
                            "Product found!",
                            Toast.LENGTH_SHORT
                    ).show();

                });

            } catch (Exception e) {

                showProductError(
                        "Error loading product data."
                );

            } finally {

                if (connection != null) {

                    connection.disconnect();

                }
            }

        });
    }

// =========================================================
// GET NUTRITION VALUE
// =========================================================

    private double getNutritionValue(
            JSONObject nutriments,
            String key
    ) {

        if (!nutriments.has(key)) {

            return 0;
        }

        return nutriments.optDouble(
                key,
                0
        );
    }

// =========================================================
// PRODUCT ERROR
// =========================================================

    private void showProductError(
            String message
    ) {

        runOnUiThread(() -> {

            tvBarcodeProduct.setText(
                    "Product: Not found"
            );

            tvBarcodeCalories.setText(
                    "Calories: --"
            );

            tvBarcodeProtein.setText(
                    "Protein: --"
            );

            tvBarcodeCarbs.setText(
                    "Carbs: --"
            );

            tvBarcodeFat.setText(
                    "Fat: --"
            );

            Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
            ).show();

        });
    }

// =========================================================
// SAVE BARCODE MEAL
// =========================================================

    private void saveBarcodeMeal() {

        // Make sure barcode was scanned.

        if (scannedBarcode.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please scan a barcode first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get product name.

        String productText =
                tvBarcodeProduct
                        .getText()
                        .toString();

        if (productText.equals(
                "Product: Not found"
        ) ||
                productText.equals(
                        "Product: Searching..."
                ) ||
                productText.equals(
                        "Product: Looking up..."
                )) {

            Toast.makeText(
                    this,
                    "Please wait for product information.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get serving weight.

        String weightText =
                etBarcodeWeight
                        .getText()
                        .toString()
                        .trim();

        if (weightText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter serving weight.",
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

        if (weight <= 0) {

            Toast.makeText(
                    this,
                    "Weight must be greater than 0.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Calculate nutrition
        // for selected serving size.

        double multiplier =
                weight / 100.0;

        double calories =
                caloriesPer100g * multiplier;

        double protein =
                proteinPer100g * multiplier;

        double carbs =
                carbsPer100g * multiplier;

        double fat =
                fatPer100g * multiplier;

        // Remove "Product: " from name.

        String foodName =
                productText.replace(
                        "Product: ",
                        ""
                );

        // Get meal type.

        String mealType =
                spinnerBarcodeMealType
                        .getSelectedItem()
                        .toString();

        // Current date.

        String date =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        // Current time.

        String time =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        // Create Room database record.

        FoodRecord foodRecord =
                new FoodRecord(
                        foodName,
                        weight,
                        calories,
                        protein,
                        carbs,
                        fat,
                        0,
                        mealType,
                        date,
                        time
                );

        // Insert into database.

        FoodRepository repository =
                new FoodRepository(this);

        repository.insert(
                foodRecord
        );

        Toast.makeText(
                this,
                "Meal saved successfully!",
                Toast.LENGTH_SHORT
        ).show();
    }

// =========================================================
// FORMAT NUMBER
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
// CAMERA PERMISSION RESULT
// =========================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {

                startBarcodeScanner();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

// =========================================================
// CLEAN UP
// =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (barcodeScanner != null) {

            barcodeScanner.close();
        }

        if (cameraExecutor != null) {

            cameraExecutor.shutdown();
        }

        if (networkExecutor != null) {

            networkExecutor.shutdown();
        }
    }

}
