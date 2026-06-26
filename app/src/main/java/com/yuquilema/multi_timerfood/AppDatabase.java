package com.yuquilema.multi_timerfood;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Usuario.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "multi_timer_food_db"
                    )
                    .allowMainThreadQueries() // Solo para proyectos académicos
                    .build();
        }

        return INSTANCE;
    }
}