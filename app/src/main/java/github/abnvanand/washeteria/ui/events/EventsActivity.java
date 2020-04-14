package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import github.abnvanand.washeteria.R;

public class EventsActivity extends AppCompatActivity {

    WeekView<MyEvent> weekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        weekView = findViewById(R.id.weekView);

        limitWeekViewRange();

        // TODO: setup ViewModel to get events
        loadDummyEvents();

        setupListeners();
    }

    private void setupListeners() {
        weekView.setOnEventClickListener((myEvent, eventRect) -> {
            Toast.makeText(EventsActivity.this,
                    "Clicked event: " + myEvent.startTime.getTime(), Toast.LENGTH_LONG).show();

        });

        // Be notified whenever the user clicks on an area where no event is displayed
        weekView.setOnEmptyViewClickListener(calendar -> {
            Toast.makeText(EventsActivity.this, "Clicked slot: " + calendar.getTime(), Toast.LENGTH_LONG).show();
        });
    }

    private void limitWeekViewRange() {
        Calendar now = Calendar.getInstance();

        Calendar max = (Calendar) now.clone();
        max.add(Calendar.DATE, 6);

        weekView.setMinDate(now);
        weekView.setMaxDate(max);
    }

    private void loadDummyEvents() {
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
        weekView.submit(events);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_week_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_today) {
            weekView.goToToday();
            return true;
        } else {
            if (currentViewTypeId() != item.getItemId()) {
                item.setChecked(!item.isChecked());
                weekView.setNumberOfVisibleDays(getSelectedItemNumberOfVisibleDays(item));
            }
            return true;
        }
    }

    // TODO: refactor logic into Enums
    private int getSelectedItemNumberOfVisibleDays(MenuItem item) {
        if (item.getItemId() == R.id.action_day_view)
            return 1;
        else if (item.getItemId() == R.id.action_three_day_view)
            return 3;
        else
            return 7;
    }

    private int currentViewTypeId() {
        int numberOfVisibleDays = weekView.getNumberOfVisibleDays();
        if (numberOfVisibleDays == 1)
            return R.id.action_day_view;
        else if (numberOfVisibleDays == 3)
            return R.id.action_three_day_view;
        else
            return R.id.action_week_view;
    }
}
