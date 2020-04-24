package github.abnvanand.washeteria.models.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import github.abnvanand.washeteria.ui.assistant.Interval;

public class AssistedEventRequest {
    @SerializedName("userId")
    private String creator;

    @SerializedName("locationId")
    private String locationId;

    @SerializedName("preferences")
    private List<Interval> intervals;

    @SerializedName("ignorePreference")
    private boolean reserveEvenIfNoMatch;

    @SerializedName("duration")
    private long duration;

    private transient String token; // GSON excludes transient field from (de)serialization

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isReserveEvenIfNoMatch() {
        return reserveEvenIfNoMatch;
    }

    public void setReserveEvenIfNoMatch(boolean reserveEvenIfNoMatch) {
        this.reserveEvenIfNoMatch = reserveEvenIfNoMatch;
    }


    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

}
