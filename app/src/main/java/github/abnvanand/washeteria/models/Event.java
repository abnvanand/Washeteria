package github.abnvanand.washeteria.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey
    @SerializedName("eventId")
    @NonNull
    private String id;

    @SerializedName("startsAt")
    private Long startsAt;
    @SerializedName("endsAt")
    private Long endsAt;
    @SerializedName("modifiedAt")
    private Long modifiedAt;

    @SerializedName("cancelled")
    private boolean cancelled;
    @SerializedName("machineId")
    private String machineId;

    @SerializedName("locationId")
    private String locationId;

    @SerializedName("userId")
    private String creator;

    @Ignore
    public Event(@NonNull String id) {
        this.id = id;
    }

    public Event(@NonNull String id, Long startsAt, Long endsAt, Long modifiedAt, boolean cancelled, String machineId, String locationId, String creator) {
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

    public Long getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Long startsAt) {
        this.startsAt = startsAt;
    }

    public Long getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Long endsAt) {
        this.endsAt = endsAt;
    }

    public Long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Long modifiedAt) {
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

    public long getEndsAtMillis() {
        return getEndsAt() * 1000;
    }

    public long getStartsAtMillis() {
        return getStartsAt() * 1000;
    }

    public long getNumericId() {
        return Long.parseLong(getId());
    }
}
