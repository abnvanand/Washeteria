package github.abnvanand.washeteria;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.database.dao.MachineDao;
import github.abnvanand.washeteria.data.model.Machine;
import timber.log.Timber;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private AppDatabase mDb;
    private MachineDao mDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context,
                AppDatabase.class).build();
        mDao = mDb.machineDao();
        Timber.i("createDb");
    }

    @After
    public void closeDb() {
        mDb.close();
        Timber.i("closeDb");
    }

    /**
     * All @Test methods should return void and not have any arguments
     */
    @Test
    public void createAndRetrieveMachines() {
        List<Machine> machines = new ArrayList<>();
        machines.add(new Machine("1", "OBH1", "1", "vacant", "0"));
        machines.add(new Machine("2", "OBH2", "1", "occupied", "10"));
        machines.add(new Machine("3", "OBH3", "1", "occupied", "30"));
        machines.add(new Machine("4", "OBH4", "1", "malfunctioned", "0"));
        mDao.insertAll(machines);
        int count = mDao.getCount();
        Timber.i("createAndRetrieveMachines count:%s", count);
        assertEquals(4, count);
    }
}
