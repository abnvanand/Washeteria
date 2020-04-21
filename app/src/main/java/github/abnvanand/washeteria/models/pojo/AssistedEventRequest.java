package github.abnvanand.washeteria.models.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import github.abnvanand.washeteria.ui.assistant.Interval;

public class AssistedEventRequest {
    @SerializedName("userId")
    private String creator;

    @SerializedName("locationId")
    private String locationId;

    @SerializedName("preferences")
    private List<List<Long>> intervals;

    @SerializedName("ignorePreference")
    private boolean reserveEvenIfNoMatch;

    @SerializedName("durationRange")
    private List<Float> durationRange;

    private transient String token; // GSON excludes transient field from (de)serialization

    public List<Interval> getIntervals() {
        List<Interval> intervals = new ArrayList<>();
        this.intervals.forEach(pair -> {
            intervals.add(new Interval().setStartSeconds(pair.get(0)).setEndSeconds(pair.get(0)));
        });

        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = new ArrayList<>();

        for (Interval interval : intervals) {
            this.intervals.add(
                    Arrays.asList(
                            interval.getStartSeconds(),
                            interval.getEndSeconds()));
        }
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

    public List<Float> getDurationRange() {
        return durationRange;
    }

    public void setDurationRange(List<Float> durationRange) {
        this.durationRange = durationRange;
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
