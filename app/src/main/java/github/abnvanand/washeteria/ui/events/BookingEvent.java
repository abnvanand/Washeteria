package github.abnvanand.washeteria.ui.events;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

public class BookingEvent implements WeekViewDisplayable<BookingEvent> {
    private long id;
    private Calendar startsAt;
    private Calendar endsAt;
    boolean cancelled;
    private String machineName;
    private String creator;
    private Integer color;

    public BookingEvent() {
    }


    public BookingEvent(long id,
                        Calendar startsAt,
                        Calendar endsAt,
                        boolean cancelled,
                        String machineName,
                        String creator,
                        Integer color) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.cancelled = cancelled;
        this.machineName = machineName;
        this.creator = creator;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Calendar startsAt) {
        this.startsAt = startsAt;
    }

    public Calendar getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Calendar endsAt) {
        this.endsAt = endsAt;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public WeekViewEvent toWeekViewEvent() {
        WeekViewEvent.Style style = new WeekViewEvent.Style.Builder()
                .setBackgroundColor(color)
                .setTextStrikeThrough(cancelled)
                .build();

        return new WeekViewEvent.Builder<>(this)
                .setId(id)
                .setTitle(machineName)
                .setStartTime(startsAt)
                .setEndTime(endsAt)
                .setLocation(creator)
                .setAllDay(false)
                .setStyle(style)
                .build();
    }
}
