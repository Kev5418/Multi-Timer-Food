package com.yuquilema.multi_timerfood;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.yuquilema.multi_timerfood.data.dao.TimerDao;
import com.yuquilema.multi_timerfood.data.entity.Timer;
import com.yuquilema.multi_timerfood.data.TimerHistoryDao;
import com.yuquilema.multi_timerfood.data.TimerHistoryItem;

@Database(entities = {Usuario.class, TimerHistoryItem.class, Timer.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();

    public abstract TimerHistoryDao timerHistoryDao();

    public abstract TimerDao timerDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "multi_timer_food_db"
                    )
                    .allowMainThreadQueries() // Solo para proyectos académicos
                    .fallbackToDestructiveMigration() // Se agregó timer_history: version 1 -> 2 -> 3
                    .build();
        }

        return INSTANCE;
    }
}