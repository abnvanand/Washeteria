package github.abnvanand.washeteria.models.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import github.abnvanand.washeteria.ui.assistant.Interval;

public class AssistedReservation {
    @SerializedName("intervals")
    List<Interval> intervals;
    @SerializedName("reserveEvenIfNoMatch")
    boolean reserveEvenIfNoMatch;
    @SerializedName("durationRange")
    List<Float> durationRange;

    public AssistedReservation(List<Interval> intervals, boolean reserveEvenIfNoMatch, List<Float> durationRange) {
        this.intervals = intervals;
        this.reserveEvenIfNoMatch = reserveEvenIfNoMatch;
        this.durationRange = durationRange;
    }
}
