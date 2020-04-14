package github.abnvanand.washeteria.ui.events;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

public class MyEvent implements WeekViewDisplayable<MyEvent> {
    long id;
    String title;
    Calendar startTime;
    Calendar endTime;
    String location;
    int color;
    boolean isAllDay;
    boolean isCanceled;

    public MyEvent(){}

    public MyEvent(long id, String title, Calendar startTime, Calendar endTime, String location, int color, boolean isAllDay, boolean isCanceled) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.color = color;
        this.isAllDay = isAllDay;
        this.isCanceled = isCanceled;
    }

    @Override
    public WeekViewEvent<MyEvent> toWeekViewEvent() {
        WeekViewEvent.Style style = new WeekViewEvent.Style.Builder()
                .setBackgroundColor(color)
                .setTextStrikeThrough(isCanceled)
                .build();

        return new WeekViewEvent.Builder<>(this)
                .setId(id)
                .setTitle(title)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setLocation(location)
                .setAllDay(isAllDay)
                .setStyle(style)
                .build();
    }
}
