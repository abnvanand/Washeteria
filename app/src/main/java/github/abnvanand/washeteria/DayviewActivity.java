package github.abnvanand.washeteria;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.alamkanak.weekview.OnEmptyViewClickListener;
import com.alamkanak.weekview.OnEventClickListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayview);

        WeekView<MyEvent> weekView = (WeekView<MyEvent>) findViewById(R.id.weekView);
        if (weekView == null)
            Log.e("Here", "Weekview is null");

        List<WeekViewDisplayable<MyEvent>> events = loadEvents();
        weekView.submit(events);

        weekView.setOnEventClickListener(new OnEventClickListener<MyEvent>() {
            @Override
            public void onEventClick(MyEvent myEvent, RectF eventRect) {
                Toast.makeText(DayviewActivity.this,
                        "Clicked event: " + myEvent.startTime.getTime(), Toast.LENGTH_LONG).show();

            }
        });

        // Be notified whenever the user clicks on an area where no event is displayed
        weekView.setOnEmptyViewClickListener(new OnEmptyViewClickListener() {
            @Override
            public void onEmptyViewClicked(Calendar calendar) {
                Toast.makeText(DayviewActivity.this, "Clicked slot: " + calendar.getTime(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<WeekViewDisplayable<MyEvent>> loadEvents() {
        List<WeekViewDisplayable<MyEvent>> events = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, +2);

        events.add(new MyEvent(1,
                "Got to gym",
                startDate,
                endDate,
                "Home",
                R.color.colorAccent,
                false,
                false));
        return events;
    }

}
