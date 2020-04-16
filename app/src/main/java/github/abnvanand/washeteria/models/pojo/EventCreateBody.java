package github.abnvanand.washeteria.models.pojo;

public class EventCreateBody {
    String machineId;
    String locationId;
    long startsAt;
    long endsAt;
    boolean cancelled;

    public EventCreateBody(String machineId, String locationId, long startsAt, long endsAt, boolean cancelled) {
        this.machineId = machineId;
        this.locationId = locationId;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.cancelled = cancelled;
    }
}
