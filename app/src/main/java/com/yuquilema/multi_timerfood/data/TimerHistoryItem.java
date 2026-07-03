package com.yuquilema.multi_timerfood.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timer_history")
public class TimerHistoryItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String foodName;
    private String category;
    private int totalSeconds;
    private boolean completed;
    private long dateMillis;

    public TimerHistoryItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }
}
