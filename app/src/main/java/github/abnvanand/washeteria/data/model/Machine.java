package github.abnvanand.washeteria.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "machines", primaryKeys = {"id", "locationId"})
public class Machine {
    @SerializedName("machineId")
    @NonNull
    private String id;

    @SerializedName("machineName")
    private String name;


    @SerializedName("locationId")
    @NonNull
    private String locationId;  // reverse mapping
    @SerializedName("status")
    private String status;  // Vacant / Occupied / Malfunctioned
    @SerializedName("remainingTime")
    private String remainingTime; // Set if status == Occupied
    // TODO: Add a list of events of this machine

    @Ignore
    public Machine() {

    }

    public Machine(String id, String name, String locationId, String status, String remainingTime) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
        this.status = status;
        this.remainingTime = remainingTime;
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

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
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
                ", remainingTime='" + remainingTime + '\'' +
                '}';
    }
}
