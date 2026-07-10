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

    // Ahora devuelve el id generado (antes era void); lo necesitamos para
    // crear el ActiveTimer en memoria con el mismo id que Room le asignó.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Timer timer);

    @Update
    void update(Timer timer);

    @Delete
    void delete(Timer timer);

    @Query("SELECT * FROM timers ORDER BY id DESC")
    LiveData<List<Timer>> getAll();

    @Query("SELECT * FROM timers WHERE id = :timerId")
    Timer getById(int timerId);

    // NUEVO: timers que quedaron corriendo o pausados la última vez que se cerró la app.
    @Query("SELECT * FROM timers WHERE estado = 'RUNNING' OR estado = 'PAUSED' ORDER BY id ASC")
    List<Timer> getActivos();
}
