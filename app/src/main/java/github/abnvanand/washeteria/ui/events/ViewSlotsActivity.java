package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.ui.dashboard.MainActivity;

public class ViewSlotsActivity extends AppCompatActivity {
    private WeekView<BookingEvent> weekView;
    private EventsViewModel mViewModel;
    ArrayList<Integer> eventColors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String locationId = getIntent().getStringExtra(MainActivity.EXTRA_SELECTED_LOCATION_ID);

        eventColors.add(ContextCompat.getColor(this, R.color.event_color_01));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_02));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_03));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_04));

        weekView = findViewById(R.id.weekView);

        limitWeekViewRange();

        initViewModel(locationId);

        setupListeners();
    }

    private void initViewModel(String locationId) {
        mViewModel = new ViewModelProvider(this)
                .get(EventsViewModel.class);
        mViewModel.getDataByLocation(locationId);
        mViewModel.getEventsByLocationObservable()
                .observe(this, events -> {
                    fillCalendarView(events);
                });

    }

    private void setupListeners() {
        weekView.setOnEventClickListener((bookingEvent, eventRect) -> {
            Toast.makeText(ViewSlotsActivity.this,
                    "Clicked event: " + bookingEvent.getStartsAt().getTime(), Toast.LENGTH_SHORT)
                    .show();

        });

        // Be notified whenever the user clicks on an area where no event is displayed
        weekView.setOnEmptyViewClickListener(calendar -> {
            Toast.makeText(ViewSlotsActivity.this, "Clicked slot: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        });
    }

    private void limitWeekViewRange() {
        Calendar now = Calendar.getInstance();

        Calendar max = (Calendar) now.clone();
        max.add(Calendar.DATE, 6);

        weekView.setMinDate(now);
        weekView.setMaxDate(max);
    }

    private void fillCalendarView(List<Event> events) {
        List<WeekViewDisplayable<BookingEvent>> weekViewDisplayableList = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            Calendar eventStartCal = Calendar.getInstance();
            eventStartCal.setTimeInMillis(event.getStartsAtMillis());
            Calendar eventEndCal = Calendar.getInstance();
            eventEndCal.setTimeInMillis(event.getEndsAtMillis());


            BookingEvent bookingEvent = new
                    BookingEvent(
                    i,
                    eventStartCal,
                    eventEndCal,
                    event.isCancelled(),
                    String.format("Event %s\nLocation %s\nMachine %s",
                            event.getId(),
                            event.getLocationId(),
                            event.getMachineId()),
                    event.getCreator(),
                    eventColors.get(i % eventColors.size()),
                    false
            );
            weekViewDisplayableList.add(bookingEvent);
        }

        weekView.submit(weekViewDisplayableList);
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
