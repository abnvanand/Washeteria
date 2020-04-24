package github.abnvanand.washeteria.ui.assistant;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Interval {
    @SerializedName("startTime")
    private long startSeconds;
    @SerializedName("endTime")
    private long endSeconds;

    public long getStartSeconds() {
        return startSeconds;
    }

    public Interval setStartSeconds(long startSeconds) {
        this.startSeconds = startSeconds;
        return this;
    }

    public long getEndSeconds() {
        return endSeconds;
    }

    public Interval setEndSeconds(long endSeconds) {
        this.endSeconds = endSeconds;
        return this;
    }

    public long getEndMillis() {
        return endSeconds * 1000;
    }

    Interval setEndMillis(long millis) {
        this.endSeconds = millis / 1000;
        return this;
    }

    public long getStartMillis() {
        return startSeconds * 1000;
    }

    Interval setStartMillis(long millis) {
        this.startSeconds = millis / 1000;
        return this;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "start=" + new Date(getStartMillis()) +
                ", end=" + new Date(getEndMillis()) +
                '}';
    }
}
