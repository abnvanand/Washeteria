package github.abnvanand.washeteria.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "events", primaryKeys = {"id"})
public class Event {
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("startsAt")
    private String startsAt;
    @SerializedName("endsAt")
    private String endsAt;
    @SerializedName("modifiedAt")
    private String modifiedAt;

    @SerializedName("cancelled")
    private boolean cancelled;
    @SerializedName("machineId")
    private String machineId;

    @SerializedName("locationId")
    private String locationId;

    @SerializedName("creator")
    private String creator;

    @Ignore
    public Event() {

    }

    public Event(@NonNull String id, String startsAt, String endsAt, String modifiedAt, boolean cancelled, String machineId, String locationId, String creator) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.modifiedAt = modifiedAt;
        this.cancelled = cancelled;
        this.machineId = machineId;
        this.locationId = locationId;
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", startsAt='" + startsAt + '\'' +
                ", endsAt='" + endsAt + '\'' +
                ", modifiedAt='" + modifiedAt + '\'' +
                ", cancelled=" + cancelled +
                ", machineId='" + machineId + '\'' +
                ", locationId='" + locationId + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}
