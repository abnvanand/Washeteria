package github.abnvanand.washeteria.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import github.abnvanand.washeteria.data.model.Location;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocation(Location location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Location> locations);

    @Query("SELECT * FROM locations WHERE id = :id")
    Location getLocationById(String id);

    @Query("SELECT * FROM locations")
    List<Location> getAll();

    @Delete
    void deleteLocation(Location location);

    @Query("DELETE FROM locations")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM locations")
    int getCount();

}
