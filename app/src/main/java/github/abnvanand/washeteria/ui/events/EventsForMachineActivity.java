package github.abnvanand.washeteria.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.databinding.ActivityEventsBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.ui.login.LoginViewModel;
import github.abnvanand.washeteria.utils.WeekViewType;
import timber.log.Timber;


public class EventsForMachineActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_MACHINE_ID = "EXTRA_SELECTED_MACHINE";

    public static final int REQUEST_EVENT_CREATION_STATUS = 1003;
    public static final int REQUEST_EVENT_CANCEL_STATUS = 1004;

    private ActivityEventsBinding binding;
    private LoggedInStatus mLoggedInStatus;
    private Snackbar loginSnackbar;
    private EventsViewModel mViewModel;

    private ArrayList<Integer> eventColors = new ArrayList<>();
    private Map<Integer, String> bookingIdToEventIdMapping = new HashMap<>();
    private String machineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        setSupportActionBar(binding.toolbarInclude.toolbar);

//        rootLayout = findViewById(R.id.rootLayout);

        // TODO: Add action to go to login screen
        loginSnackbar = Snackbar
                .make(rootView, "You must login to perform this action.", Snackbar.LENGTH_LONG)
                .setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(EventsForMachineActivity.this, LoginActivity.class));

                    }
                });

        machineId = getIntent().getStringExtra(EXTRA_SELECTED_MACHINE_ID);

        processColorResources();

        WeekView<BookingEvent> weekView = binding.weekView;
        limitWeekViewRange(weekView);

        initViewModel(machineId, weekView);

        setupListeners(weekView);
    }

    private void processColorResources() {
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_01));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_02));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_03));
        eventColors.add(ContextCompat.getColor(this, R.color.event_color_04));
    }

    private void initViewModel(String machineId, WeekView<BookingEvent> weekView) {
        LoginViewModel loginViewModel = new ViewModelProvider(this)
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
                    fillCalendarView(events, weekView);
                });

    }

    private void setupListeners(WeekView<BookingEvent> weekView) {
        weekView.setOnEventClickListener((bookingEvent, eventRect) -> {
            String creator = bookingEvent.getCreator();

            if (mLoggedInStatus == null
                    || !mLoggedInStatus.isLoggedIn()
                    || mLoggedInStatus.getUser() == null) {
                loginSnackbar.setText("Please login to edit a reservation.")
                        .show();
                return;
            }

            // Open cancel Activity only if currently logged in user is the creator of this event
            if (!creator.equals(mLoggedInStatus.getUser().getUsername())) {
                Toast.makeText(this,
                        "Only owner can edit a reservation",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CancelReservationActivity.class);
            String eventId = bookingIdToEventIdMapping.get(bookingEvent.getId());
            intent.putExtra(CancelReservationActivity.EXTRA_EVENT_ID, eventId); // event object which contains event id
            startActivityForResult(intent, REQUEST_EVENT_CANCEL_STATUS);

        });

        // Be notified whenever the user clicks on an area where no event is displayed
        weekView.setOnEmptyViewClickListener(calendar -> {
            // TODO: make sure
            if (mLoggedInStatus == null || !mLoggedInStatus.isLoggedIn() || mLoggedInStatus.getUser() == null) {
                loginSnackbar
                        .setText("You must be logged in to reserve a slot.")
                        .show();
                return;
            }

            Intent intent = new Intent(this,
                    ReserveSlotActivity.class);
            intent.putExtra(ReserveSlotActivity.EXTRA_MACHINE_ID, machineId);
            intent.putExtra(ReserveSlotActivity.EXTRA_CLICKED_MILLIS, calendar.getTimeInMillis());
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

    private void limitWeekViewRange(WeekView<BookingEvent> weekView) {
        Calendar now = Calendar.getInstance();

        Calendar max = (Calendar) now.clone();
        max.add(Calendar.DATE, 6);

        weekView.setMinDate(now);
        weekView.setMaxDate(max);
    }

    private void fillCalendarView(List<Event> events, WeekView<BookingEvent> weekView) {
        Timber.d("Events: %s", events);
        List<WeekViewDisplayable<BookingEvent>> weekViewDisplayableList = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            Calendar eventStartCal = Calendar.getInstance();
            eventStartCal.setTimeInMillis(event.getStartsAtMillis());
            Calendar eventEndCal = Calendar.getInstance();
            eventEndCal.setTimeInMillis(event.getEndsAtMillis());

            boolean iAmTheCreator = mLoggedInStatus != null
                    && mLoggedInStatus.getUser() != null
                    && Objects.equals(mLoggedInStatus.getUser().getUsername(), event.getCreator());

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
                    iAmTheCreator
            );
            bookingIdToEventIdMapping.put(i, event.getId());
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
