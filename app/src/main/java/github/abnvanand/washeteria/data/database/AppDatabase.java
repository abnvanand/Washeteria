package github.abnvanand.washeteria.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import github.abnvanand.washeteria.data.database.converters.DateConverter;
import github.abnvanand.washeteria.data.database.dao.MachineDao;
import github.abnvanand.washeteria.data.model.Machine;

@Database(entities = {Machine.class}, version = 1)
@TypeConverters(DateConverter.class)

public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "AppDatabase.db";
    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();    // For synchronization

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract MachineDao machineDao();
}
