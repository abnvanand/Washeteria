package github.abnvanand.washeteria.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import github.abnvanand.washeteria.models.Event;

@Dao
public interface EventDao {

    // FIXME: On conflict compare the modifiedAt timestamp of both records
    //  Always persist the newer one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(Event event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Event> events);

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(String id);

    @Query("SELECT * FROM events WHERE locationId=:locationId")
    List<Event> getAllByLocationId(String locationId);

    @Query("SELECT * FROM events WHERE machineId=:machineId")
    List<Event> getAllByMachineId(String machineId);

    @Query("SELECT * FROM events WHERE machineId=:machineId AND cancelled=0")
    List<Event> getNonCancelledByMachineId(String machineId);

    @Query("SELECT * FROM events")
    List<Event> getAll();

    @Delete
    void deleteEvent(Event event);

    @Query("DELETE FROM events")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM events")
    int getCount();

    @Query("SELECT MAX(modifiedAt) FROM events")
    Long getLastModifiedAt();
}
