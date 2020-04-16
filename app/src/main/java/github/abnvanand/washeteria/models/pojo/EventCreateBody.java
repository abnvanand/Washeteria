package github.abnvanand.washeteria.models.pojo;

import com.google.gson.annotations.SerializedName;

public class EventCreateBody {
    String machineId;
    String locationId;
    @SerializedName("startsAt")
    long startsAtSeconds;
    @SerializedName("endsAt")
    long endsAtSeconds;
    boolean cancelled;
    @SerializedName("userId")
    String creator;

    public EventCreateBody(String machineId, String locationId, long startsAtMillis, long endsAtMillis, boolean cancelled, String creator) {
        this.machineId = machineId;
        this.locationId = locationId;
        this.startsAtSeconds = startsAtMillis / 1000;
        this.endsAtSeconds = endsAtMillis / 1000;
        this.cancelled = cancelled;
        this.creator = creator;
    }
}
