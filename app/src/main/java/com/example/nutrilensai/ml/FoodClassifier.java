package com.example.nutrilensai.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

public class FoodClassifier {

    private final ImageLabeler labeler;

    public FoodClassifier(Context context) {

        LocalModel localModel =
                new LocalModel.Builder()
                        .setAssetFilePath("food_model.tflite")
                        .build();

        CustomImageLabelerOptions options =
                new CustomImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.1f)
                        .setMaxResultCount(1)
                        .build();

        labeler =
                ImageLabeling.getClient(options);
    }

    public interface ClassificationCallback {
        void onResult(String foodName);
        void onError(Exception e);
    }

    public void classify(
            Bitmap bitmap,
            ClassificationCallback callback) {

        InputImage image =
                InputImage.fromBitmap(bitmap, 0);

        labeler.process(image)
                .addOnSuccessListener(labels -> {

                    if (labels == null ||
                            labels.isEmpty()) {

                        callback.onResult(
                                "Unknown food"
                        );

                        return;
                    }

                    ImageLabel bestLabel =
                            labels.get(0);

                    callback.onResult(
                            bestLabel.getText()
                    );
                })
                .addOnFailureListener(
                        callback::onError
                );
    }

    public void classify(
            Uri imageUri,
            Context context,
            ClassificationCallback callback) {

        try {

            InputImage image =
                    InputImage.fromFilePath(
                            context,
                            imageUri
                    );

            labeler.process(image)
                    .addOnSuccessListener(labels -> {

                        if (labels == null ||
                                labels.isEmpty()) {

                            callback.onResult(
                                    "Unknown food"
                            );

                            return;
                        }

                        ImageLabel bestLabel =
                                labels.get(0);

                        callback.onResult(
                                bestLabel.getText()
                        );
                    })
                    .addOnFailureListener(
                            callback::onError
                    );

        } catch (Exception e) {

            callback.onError(e);
        }
    }

    public void close() {
        labeler.close();
    }
}