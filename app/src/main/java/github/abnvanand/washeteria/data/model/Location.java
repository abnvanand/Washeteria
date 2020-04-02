package github.abnvanand.washeteria.data.model;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("locationId")
    private String locationId;
    @SerializedName("locationName")
    private String locationName;

    // TODO: Add a list of machines

    public Location(String locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationId='" + locationId + '\'' +
                ", locationName='" + locationName + '\'' +
                '}';
    }
}
