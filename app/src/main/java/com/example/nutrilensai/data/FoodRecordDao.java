package com.example.nutrilensai.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodRecordDao {

    @Insert
    void insert(FoodRecord foodRecord);

    @Delete
    void delete(FoodRecord foodRecord);

    @Query("SELECT * FROM food_records WHERE username = :username ORDER BY id DESC")
    List<FoodRecord> getAllFoodRecords(String username);

    @Query("SELECT * FROM food_records WHERE username = :username AND date = :date ORDER BY id DESC")
    List<FoodRecord> getFoodRecordsByDate(String username, String date);

    @Query("DELETE FROM food_records WHERE username = :username")
    void deleteAll(String username);
}