package com.example.nutrilensai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrilensai.data.FoodRecord;
import com.example.nutrilensai.data.FoodRepository;

import java.util.List;

public class FoodHistoryAdapter
        extends RecyclerView.Adapter<FoodHistoryAdapter.FoodViewHolder> {

    private List<FoodRecord> foodList;
    private FoodRepository repository;

    public FoodHistoryAdapter(
            List<FoodRecord> foodList,
            FoodRepository repository) {

        this.foodList = foodList;
        this.repository = repository;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_history, parent, false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FoodViewHolder holder,
            int position) {

        FoodRecord food = foodList.get(position);

        holder.tvFoodName.setText(food.foodName);

        holder.tvCalories.setText(
                food.calories + " kcal"
        );

        holder.tvMealType.setText(
                food.mealType
        );

        holder.tvDetails.setText(
                food.weight + " g • " +
                        "P: " + food.protein + " g • " +
                        "C: " + food.carbs + " g • " +
                        "F: " + food.fat + " g"
        );

        holder.tvTime.setText(
                food.date + " • " + food.time
        );

        // DELETE BUTTON
        holder.btnDelete.setOnClickListener(v -> {

            int currentPosition =
                    holder.getBindingAdapterPosition();

            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            FoodRecord selectedFood =
                    foodList.get(currentPosition);

            repository.delete(selectedFood);

            foodList.remove(currentPosition);

            notifyItemRemoved(currentPosition);

            Toast.makeText(
                    v.getContext(),
                    "Food deleted",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvFoodName;
        TextView tvCalories;
        TextView tvMealType;
        TextView tvDetails;
        TextView tvTime;

        Button btnDelete;

        public FoodViewHolder(@NonNull View itemView) {

            super(itemView);

            tvFoodName =
                    itemView.findViewById(R.id.tvFoodName);

            tvCalories =
                    itemView.findViewById(R.id.tvCalories);

            tvMealType =
                    itemView.findViewById(R.id.tvMealType);

            tvDetails =
                    itemView.findViewById(R.id.tvDetails);

            tvTime =
                    itemView.findViewById(R.id.tvTime);

            btnDelete =
                    itemView.findViewById(R.id.btnDelete);
        }
    }
}