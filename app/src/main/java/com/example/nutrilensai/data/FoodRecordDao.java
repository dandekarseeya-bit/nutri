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

    @Query("SELECT * FROM food_records ORDER BY id DESC")
    List<FoodRecord> getAllFoodRecords();

    @Query("SELECT * FROM food_records WHERE date = :date ORDER BY id DESC")
    List<FoodRecord> getFoodRecordsByDate(String date);

    @Query("DELETE FROM food_records")
    void deleteAll();
}