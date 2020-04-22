package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;
import android.text.TextUtils;
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
import github.abnvanand.washeteria.databinding.ActivityEventsBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.ui.dashboard.MainActivity;
import github.abnvanand.washeteria.utils.WeekViewType;

public class ViewSlotsActivity extends AppCompatActivity {

    ArrayList<Integer> eventColors = new ArrayList<>();
    private ActivityEventsBinding binding;
    private EventsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarInclude.toolbar);

        String locationId = getIntent().getStringExtra(MainActivity.EXTRA_SELECTED_LOCATION_ID);
        if (TextUtils.isEmpty(locationId)) {
            Toast.makeText(this, "You must select a location first", Toast.LENGTH_SHORT).show();
            return;
        }

        processColorResources();

        WeekView<BookingEvent> weekView = binding.weekView;
        limitWeekViewRange(weekView);

        initViewModel(locationId, weekView);

        setupListeners(weekView);
    }

    private void processColorResources() {
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_01));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_02));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_03));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_04));
    }

    private void initViewModel(String locationId, WeekView<BookingEvent> weekView) {
        mViewModel = new ViewModelProvider(this)
                .get(EventsViewModel.class);
        mViewModel.getDataByLocation(locationId);
        mViewModel.getEventsByLocationObservable()
                .observe(this, events -> {
                    fillCalendarView(events, weekView);
                });

    }

    private void setupListeners(WeekView<BookingEvent> weekView) {
        weekView.setOnEventClickListener((bookingEvent, eventRect) -> {
            Toast.makeText(ViewSlotsActivity.this,
                    "Event: " + bookingEvent.getStartsAt().getTime(), Toast.LENGTH_SHORT)
                    .show();

        });

        // Be notified whenever the user clicks on an area where no event is displayed
        weekView.setOnEmptyViewClickListener(calendar -> {
            Toast.makeText(ViewSlotsActivity.this, "Free slot: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        });
    }

    private void limitWeekViewRange(WeekView<BookingEvent> weekView) {
        Calendar now = Calendar.getInstance();

        Calendar max = (Calendar) now.clone();
        max.add(Calendar.DATE, 6);

        weekView.setMinDate(now);
        weekView.setMaxDate(max);
    }

    private void fillCalendarView(List<Event> events, WeekView<BookingEvent> weekView) {
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
            binding.weekView.goToToday();
            return true;
        } else {
            if (id != WeekViewType.getAction(binding.weekView.getNumberOfVisibleDays())) {
                item.setChecked(!item.isChecked());
                binding.weekView.setNumberOfVisibleDays(WeekViewType.getNumVisibleDays(item.getItemId()));
            }
            return true;
        }
    }
}
