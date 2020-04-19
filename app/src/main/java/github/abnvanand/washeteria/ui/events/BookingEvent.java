package github.abnvanand.washeteria.ui.events;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import github.abnvanand.washeteria.R;

public class BookingEvent implements WeekViewDisplayable<BookingEvent> {
    private int id;
    private Calendar startsAt;
    private Calendar endsAt;
    private boolean cancelled;
    private String machineName;
    private String creator;
    private Integer color;
    private boolean isMyEvent;

    public BookingEvent() {
    }


    BookingEvent(int id,
                 Calendar startsAt, Calendar endsAt,
                 boolean cancelled,
                 String machineName,
                 String creator,
                 Integer color,
                 boolean isMyEvent) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.cancelled = cancelled;
        this.machineName = machineName;
        this.creator = creator;
        this.color = color;
        this.isMyEvent = isMyEvent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isMyEvent() {
        return isMyEvent;
    }

    public void setMyEvent(boolean myEvent) {
        isMyEvent = myEvent;
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

    @NotNull
    @Override
    public WeekViewEvent<BookingEvent> toWeekViewEvent() {
        int backgroundColor = (isMyEvent) ? color : Color.WHITE;
        int textColor = (isMyEvent) ? Color.WHITE : color;
        int borderWidthResId = (isMyEvent) ? R.dimen.no_border_width : R.dimen.border_width;

        WeekViewEvent.Style style = new WeekViewEvent.Style.Builder()
                .setTextColor(textColor)
                .setBackgroundColor(backgroundColor)
                .setTextStrikeThrough(cancelled)
                .setBorderWidthResource(borderWidthResId)
                .setBorderColor(color)
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
