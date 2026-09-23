package com.example.nutrilensai.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NutritionDatabase {

    public static class Nutrition {

        public double calories;
        public double protein;
        public double carbs;
        public double fat;
        public double fiber;

        public Nutrition(
                double calories,
                double protein,
                double carbs,
                double fat,
                double fiber
        ) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
            this.fiber = fiber;
        }
    }

    private static final Map<String, Nutrition> FOOD_DATA =
            new HashMap<>();

    static {

        FOOD_DATA.put(
                "new york style pizza",
                new Nutrition(
                        285,
                        12.0,
                        36.0,
                        10.0,
                        2.0
                )
        );

        FOOD_DATA.put(
                "pizza",
                new Nutrition(
                        266,
                        11.0,
                        33.0,
                        10.0,
                        2.3
                )
        );

        FOOD_DATA.put(
                "apple",
                new Nutrition(
                        52,
                        0.3,
                        14.0,
                        0.2,
                        2.4
                )
        );

        FOOD_DATA.put(
                "banana",
                new Nutrition(
                        89,
                        1.1,
                        22.8,
                        0.3,
                        2.6
                )
        );

        FOOD_DATA.put(
                "white rice",
                new Nutrition(
                        130,
                        2.7,
                        28.2,
                        0.3,
                        0.4
                )
        );

        FOOD_DATA.put(
                "hamburger",
                new Nutrition(
                        295,
                        17.0,
                        24.0,
                        14.0,
                        1.3
                )
        );

        FOOD_DATA.put(
                "fried chicken",
                new Nutrition(
                        246,
                        18.0,
                        9.0,
                        15.0,
                        0.0
                )
        );

        FOOD_DATA.put(
                "salad",
                new Nutrition(
                        33,
                        2.0,
                        6.0,
                        0.4,
                        2.0
                )
        );
    }

    public static Nutrition getNutrition(
            String foodName
    ) {

        if (foodName == null) {
            return null;
        }

        String key =
                foodName
                        .trim()
                        .toLowerCase(Locale.ROOT);

        // New York Style Pizza
        if (key.contains("new york") &&
                key.contains("pizza")) {

            return new Nutrition(
                    285,
                    12.0,
                    36.0,
                    10.0,
                    2.0
            );
        }

        // Generic pizza
        if (key.contains("pizza")) {

            return new Nutrition(
                    266,
                    11.0,
                    33.0,
                    10.0,
                    2.3
            );
        }

        // Apple
        if (key.contains("apple")) {
            return FOOD_DATA.get("apple");
        }

        // Banana
        if (key.contains("banana")) {
            return FOOD_DATA.get("banana");
        }

        // Rice
        if (key.contains("rice")) {
            return FOOD_DATA.get("white rice");
        }

        // Hamburger / Cheeseburger
        if (key.contains("hamburger") ||
                key.contains("cheeseburger")) {

            return FOOD_DATA.get("hamburger");
        }

        // Fried chicken
        if (key.contains("fried chicken")) {
            return FOOD_DATA.get("fried chicken");
        }

        // Salad
        if (key.contains("salad")) {
            return FOOD_DATA.get("salad");
        }

        // Exact match for other foods
        return FOOD_DATA.get(key);
    }
}