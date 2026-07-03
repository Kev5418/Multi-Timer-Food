package com.yuquilema.multi_timerfood.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimerHistoryDao {

    @Insert
    void insertar(TimerHistoryItem item);

    @Query("SELECT * FROM timer_history ORDER BY dateMillis DESC")
    List<TimerHistoryItem> obtenerTodos();

    @Delete
    void eliminar(TimerHistoryItem item);

    @Query("DELETE FROM timer_history")
    void eliminarTodos();
}
