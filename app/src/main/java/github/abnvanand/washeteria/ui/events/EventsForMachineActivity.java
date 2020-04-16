package github.abnvanand.washeteria.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import github.abnvanand.washeteria.ui.login.LoginViewModel;
import timber.log.Timber;

import static github.abnvanand.washeteria.ui.dashboard.MainActivity.EXTRA_SELECTED_MACHINE_ID;

public class EventsForMachineActivity extends AppCompatActivity {

    public static final String EXTRA_MACHINE_ID = "machine_id";
    public static final String EXTRA_CLICKED_MILLIS = "calendar_object";
    public static final String EXTRA_EVENT_ID = "clicked_event_id";
    public static final int REQUEST_EVENT_CREATION_STATUS = 1003;
    public static final int REQUEST_EVENT_CANCEL_STATUS = 1004;
    private LoggedInStatus mLoggedInStatus;

    private WeekView<BookingEvent> weekView;
    private EventsViewModel mViewModel;
    private LoginViewModel loginViewModel;

    ArrayList<Integer> eventColors = new ArrayList<>();
    String machineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        machineId = getIntent().getStringExtra(EXTRA_SELECTED_MACHINE_ID);

        eventColors.add(ContextCompat.getColor(this, R.color.event_color_01));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_02));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_03));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_04));

        weekView = findViewById(R.id.weekView);

        limitWeekViewRange();

        initViewModel(machineId);

        setupListeners();
    }

    private void initViewModel(String machineId) {
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        loginViewModel.getLoggedInStatusObservable()
                .observe(this, loggedInStatus -> {
                    mLoggedInStatus = loggedInStatus;
                });

        mViewModel = new ViewModelProvider(this)
                .get(EventsViewModel.class);

        mViewModel.getDataByMachine(machineId);

        mViewModel.getEventsByMachineObservable()
                .observe(this, events -> {
                    fillCalendarView(events);
                });

    }

    private void setupListeners() {
        weekView.setOnEventClickListener((bookingEvent, eventRect) -> {
            Toast.makeText(EventsForMachineActivity.this,
                    "Clicked event: " + bookingEvent.getStartsAt().getTime(), Toast.LENGTH_SHORT)
                    .show();
            String creator = bookingEvent.getCreator();


            // Open cancel Activity only if currently logged in user is the creator of this event
            if (mLoggedInStatus == null
                    || !mLoggedInStatus.isLoggedIn()
                    || mLoggedInStatus.getUser() == null
                    || !creator.equals(mLoggedInStatus.getUser().getUsername())) {
                Toast.makeText(this, "Only owner can edit a reservation", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CancelReservationActivity.class);
            intent.putExtra(EXTRA_EVENT_ID, String.valueOf(bookingEvent.getId())); // event object which contains event id
            startActivityForResult(intent, REQUEST_EVENT_CANCEL_STATUS);

        });

        // Be notified whenever the user clicks on an area where no event is displayed
        weekView.setOnEmptyViewClickListener(calendar -> {
            Toast.makeText(EventsForMachineActivity.this,
                    "Book this slot: " + calendar.getTime(),
                    Toast.LENGTH_SHORT).show();

            // TODO: make sure
            if (mLoggedInStatus == null
                    || !mLoggedInStatus.isLoggedIn()
                    || mLoggedInStatus.getUser() == null) {
                Toast.makeText(this,
                        "You must login to reserve a slot.", Toast.LENGTH_SHORT)
                        .show();
                // TODO: Send to LoginActivity
                return;
            }

            Intent intent = new Intent(this,
                    ReserveSlotActivity.class);
            intent.putExtra(EXTRA_MACHINE_ID, machineId);
            intent.putExtra(EXTRA_CLICKED_MILLIS, calendar.getTimeInMillis());
            startActivityForResult(intent, REQUEST_EVENT_CREATION_STATUS);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EVENT_CREATION_STATUS) {
                // Refresh  events
                mViewModel.getDataByMachine(machineId);
            } else if (requestCode == REQUEST_EVENT_CANCEL_STATUS) {
                mViewModel.getDataByMachine(machineId);
            }
        }
    }

    private void limitWeekViewRange() {
        Calendar now = Calendar.getInstance();

        Calendar max = (Calendar) now.clone();
        max.add(Calendar.DATE, 6);

        weekView.setMinDate(now);
        weekView.setMaxDate(max);
    }

    private void fillCalendarView(List<Event> events) {
        Timber.d("Events: %s", events);
        List<WeekViewDisplayable<BookingEvent>> weekViewDisplayableList = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            Calendar eventStartCal = Calendar.getInstance();
            eventStartCal.setTimeInMillis(event.getStartsAtMillis());
            Calendar eventEndCal = Calendar.getInstance();
            eventEndCal.setTimeInMillis(event.getEndsAtMillis());

            BookingEvent bookingEvent = new
                    BookingEvent(
                    event.getNumericId(),
                    eventStartCal,
                    eventEndCal,
                    event.isCancelled(),
                    String.format("Event %s\nLocation %s\nMachine %s",
                            event.getId(),
                            event.getLocationId(),
                            event.getMachineId()),
                    event.getCreator(),
                    eventColors.get(i % eventColors.size())
            );
            weekViewDisplayableList.add(bookingEvent);
        }

        // FIXME: gives NPE when a machine has no events
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
