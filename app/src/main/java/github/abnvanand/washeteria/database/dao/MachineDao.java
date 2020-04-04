package github.abnvanand.washeteria.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import github.abnvanand.washeteria.data.model.Machine;

@Dao
public interface MachineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMachine(Machine machine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Machine> machines);

    @Query("SELECT * FROM machines WHERE id = :id")
    Machine getMachineById(String id);

    @Query("SELECT * FROM machines WHERE locationId=:locationId")
    List<Machine> getAllByLocationId(String locationId);

    @Query("SELECT * FROM machines")
    List<Machine> getAll();

    @Delete
    void deleteMachine(Machine machine);

    @Query("DELETE FROM machines")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM machines")
    int getCount();

}
