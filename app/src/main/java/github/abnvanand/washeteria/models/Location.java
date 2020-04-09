package github.abnvanand.washeteria.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "locations")
public class Location {
    @PrimaryKey
    @NonNull
    @SerializedName("locationId")
    private String id;

    @SerializedName("locationName")
    private String name;

    // TODO: Add a list of machines

    public Location(@NotNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Simplify tostring so that Spinner dropdown shows a human readable name
    // A better solution could be https://stackoverflow.com/a/8116756/5463404 TODO
    @Override
    public String toString() {
        return this.name;
    }

    // Added as a replacement for our old toString()
    public String toRepr() {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
