package github.abnvanand.washeteria.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "machines", primaryKeys = {"id", "locationId"})
public class Machine {
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("name")
    private String name;


    @SerializedName("locationId")
    @NonNull
    private String locationId;  // reverse mapping

    @SerializedName("status")
    private String status;  // Vacant / Occupied / Malfunctioned

    @SerializedName("nextAvaiableAt")
    private Long nextAvailableAt;
    // TODO: Add a list of events of this machine

    @Ignore
    public Machine() {

    }

    public Machine(@NonNull String id, String name, @NonNull String locationId,
                   String status, Long nextAvailableAt) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
        this.status = status;
        this.nextAvailableAt = nextAvailableAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getNextAvailableAt() {
        return nextAvailableAt;
    }

    public Long getNextAvailableAtMillis() {
        return nextAvailableAt * 1000;
    }

    public void setNextAvailableAt(Long nextAvailableAt) {
        this.nextAvailableAt = nextAvailableAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locationId='" + locationId + '\'' +
                ", status='" + status + '\'' +
                ", remainingTime='" + nextAvailableAt + '\'' +
                '}';
    }
}
