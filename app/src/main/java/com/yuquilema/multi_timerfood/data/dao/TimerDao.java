package com.yuquilema.multi_timerfood.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yuquilema.multi_timerfood.data.entity.Timer;

import java.util.List;

@Dao
public interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Timer timer);

    @Update
    void update(Timer timer);

    @Delete
    void delete(Timer timer);

    @Query("SELECT * FROM timers ORDER BY id DESC")
    LiveData<List<Timer>> getAll();

    @Query("SELECT * FROM timers WHERE id = :timerId")
    Timer getById(int timerId);
}
