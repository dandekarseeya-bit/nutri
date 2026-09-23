package com.example.nutrilensai.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodRepository {

    private final FoodRecordDao foodRecordDao;
    private final String currentUsername;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public FoodRepository(Context context) {

        AppDatabase database =
                AppDatabase.getDatabase(context);

        foodRecordDao = database.foodRecordDao();
        
        SharedPreferences prefs = context.getSharedPreferences("NutriLensPrefs", Context.MODE_PRIVATE);
        currentUsername = prefs.getString("username", "default");
    }

    public void insert(FoodRecord foodRecord) {
        foodRecord.username = currentUsername;
        executorService.execute(() -> {
            foodRecordDao.insert(foodRecord);
        });
    }

    public void delete(FoodRecord foodRecord) {

        executorService.execute(() -> {
            foodRecordDao.delete(foodRecord);
        });
    }

    public void deleteAll() {

        executorService.execute(() -> {
            foodRecordDao.deleteAll(currentUsername);
        });
    }

    public void getAllFoodRecords(
            RepositoryCallback<List<FoodRecord>> callback) {

        executorService.execute(() -> {

            List<FoodRecord> records =
                    foodRecordDao.getAllFoodRecords(currentUsername);

            callback.onComplete(records);
        });
    }
    public void getFoodRecordsByDate(
            String date,
            RepositoryCallback<List<FoodRecord>> callback) {

        executorService.execute(() -> {

            List<FoodRecord> records =
                    foodRecordDao.getFoodRecordsByDate(currentUsername, date);

            callback.onComplete(records);
        });
    }

    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }
}