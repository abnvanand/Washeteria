package github.abnvanand.washeteria.models.pojo;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import github.abnvanand.washeteria.ui.assistant.Interval;

public class AssistedEvent {
    @NonNull
    @SerializedName("userId")
    private String creator;

    @NonNull
    @SerializedName("locationId")
    private String locationId;

    @SerializedName("preferences")
    private List<Interval> intervals;

    @SerializedName("ignorePreference")
    private boolean reserveEvenIfNoMatch;

    @SerializedName("duration")
    private long duration;

//    private transient String token; // GSON excludes transient field from (de)serialization

    private AssistedEvent(Builder builder) {
        this.creator = builder.creator;
        this.locationId = builder.locationId;
        this.intervals = builder.intervals;
        this.reserveEvenIfNoMatch = builder.reserveEvenIfNoMatch;
        this.duration = builder.duration;
    }

    public static class Builder {
        private String creator;
        private String locationId;
        private List<Interval> intervals;
        private boolean reserveEvenIfNoMatch;
        private long duration;

        public Builder setIntervals(List<Interval> intervals) {
            this.intervals = intervals;
            return this;
        }


        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setReserveEvenIfNoMatch(boolean reserveEvenIfNoMatch) {
            this.reserveEvenIfNoMatch = reserveEvenIfNoMatch;
            return this;
        }


        public Builder setCreator(String creator) {
            this.creator = creator;
            return this;
        }

        public Builder setLocationId(String locationId) {
            this.locationId = locationId;
            return this;
        }

        public AssistedEvent build() throws InstantiationException {
            // TODO: Perform validation that all required fields are set
            if (this.creator == null)
                throw new InstantiationException("Event creator must be set");

            if (this.duration == 0)
                throw new InstantiationException("Duration can't be zero");

            if (this.locationId == null)
                throw new InstantiationException("Location must be specified");

            if (this.intervals == null || this.intervals.isEmpty()) {
                if (!this.reserveEvenIfNoMatch) {
                    throw new InstantiationException("Either intervals must be non empty " +
                            "or reserve even if no match must be true");
                }
            }

            return new AssistedEvent(this);
        }
    }
}
