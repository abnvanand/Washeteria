package github.abnvanand.washeteria.ui.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.databinding.ActivityAssistantBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.AssistedEventRequest;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.ui.dashboard.MainActivity;
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.ui.login.LoginViewModel;
import github.abnvanand.washeteria.utils.ChipMaps;
import github.abnvanand.washeteria.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AssistantActivity extends AppCompatActivity {

    private ActivityAssistantBinding binding;

    private LoggedInStatus mLoggedInStatus;
    private LoginViewModel loginViewModel;

    private String locationId;

    private Snackbar loginSnackbar;
    private AssistedEventRequest assistedEventRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarInclude.toolbar);

        locationId = getIntent().getStringExtra(MainActivity.EXTRA_SELECTED_LOCATION_ID);
        if (TextUtils.isEmpty(locationId)) {
            Toast.makeText(this, "You must select a location first", Toast.LENGTH_SHORT).show();
            return;
        }

        loginSnackbar = Snackbar
                .make(binding.getRoot(),
                        "You must login to perform this action.",
                        Snackbar.LENGTH_LONG)
                .setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AssistantActivity.this, LoginActivity.class));
                    }
                });


        assistedEventRequest = new AssistedEventRequest();

        setupSliderUI();

        setupListeners();

        registerWithLoginViewModel();
    }

    private void registerWithLoginViewModel() {
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        loginViewModel.getLoggedInStatusObservable()
                .observe(this, loggedInStatus -> {
                    mLoggedInStatus = loggedInStatus;
                });

    }

    private void setupSliderUI() {
        binding.durationSlider.rangeSlider.setValues((float) Constants.MAX_DURATION / 2, (float) Constants.MAX_DURATION);
        binding.durationSlider.valueStart.setText(String.format(Locale.ENGLISH, "%d", Constants.MAX_DURATION / 2));
        binding.durationSlider.valueEnd.setText(String.format(Locale.ENGLISH, "%d", Constants.MAX_DURATION));
        binding.durationSlider.rangeSlider.setValueFrom(Constants.MIN_DURATION);
        binding.durationSlider.rangeSlider.setValueTo(Constants.MAX_DURATION);
    }

    private void sendAPIRequest(AssistedEventRequest assistedEventRequest) {
        // FIXME: GET Token
        RetrofitSingleton.getAuthorizedInstance(assistedEventRequest.getToken())
                .create(WebService.class)
                .createAssistedEvent(assistedEventRequest)

                .enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        binding.loading.setVisibility(View.INVISIBLE);
                        if (!response.isSuccessful()) {
                            handleUnsuccessfulResponse(response);
                            return;
                        }

                        Event body = response.body();
                        if (body == null) {
                            Timber.wtf("Event create response code is: %s but body is null", response.code());
                            Toast.makeText(AssistantActivity.this,
                                    "Could not find any free slot. Try later or use manual reservation",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        displayCreatedEvent(body);

                    }

                    @Override
                    public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                        binding.loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(AssistantActivity.this,
                                "Err: " + t.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayCreatedEvent(Event event) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            AppDatabase mDb = AppDatabase.getInstance(AssistantActivity.this);
            Machine machineById = mDb.machineDao().getMachineById(event.getMachineId());
            Location locationById = mDb.locationDao().getLocationById(event.getLocationId());

            runOnUiThread(() -> showEventDialog(event, machineById, locationById));
        });
    }

    private void showEventDialog(Event event, Machine machine, Location location) {
        // title, custom view, actions dialog
        View dialogView = View.inflate(this, R.layout.content_event_details, null);

        TextView machineNameTV = dialogView.findViewById(R.id.machineName);
        TextView locationNameTV = dialogView.findViewById(R.id.locationName);
        View startsAtWidget = dialogView.findViewById(R.id.startsAtWidget);
        TextView startsAtDateTV = startsAtWidget.findViewById(R.id.eventDate);
        TextView startsAtTimeTV = startsAtWidget.findViewById(R.id.eventTime);
        View endsAtWidget = dialogView.findViewById(R.id.endsAtWidget);
        TextView endsAtDateTV = endsAtWidget.findViewById(R.id.eventDate);
        TextView endsAtTimeTV = endsAtWidget.findViewById(R.id.eventTime);

        machineNameTV.setText(machine.getName());
        locationNameTV.setText(location.getName());

        startsAtDateTV
                .setText(Constants.dateFormat.format(event.getStartsAtMillis()));
        startsAtTimeTV
                .setText(Constants.timeFormat.format(event.getStartsAtMillis()));
        endsAtDateTV
                .setText(Constants.dateFormat.format(event.getEndsAtMillis()));
        endsAtTimeTV
                .setText(Constants.timeFormat.format(event.getEndsAtMillis()));

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Event Details")
                .setPositiveButton("Done", null)
                .setView(dialogView);

        AlertDialog alertDialog = materialAlertDialogBuilder
                .create();

        alertDialog
                .show();
    }

    private void handleUnsuccessfulResponse(Response<Event> response) {
        switch (response.code()) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                loginViewModel.logout();
                Snackbar.make(
                        binding.getRoot(),
                        "Token has expired. Please login again",
                        Snackbar.LENGTH_LONG)
                        .setAction("LOGIN",
                                v1 -> startActivity(
                                        new Intent(this,
                                                LoginActivity.class)))
                        .show();
                break;
            default:
                Toast.makeText(this,
                        "Err: " +
                                (!TextUtils.isEmpty(response.message()) ?
                                        response.message() :
                                        response.code()),
                        Toast.LENGTH_SHORT).show();

        }
    }

    @NotNull
    private ArrayList<Integer> calculateDayOffsets(ArrayList<Integer> days) {
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_WEEK);

        ArrayList<Integer> dayOffsets = new ArrayList<>();
        days.forEach(day -> {
            int diff = (day - today + 7) % 7;
            dayOffsets.add(diff);
        });
        return dayOffsets;
    }

    private void setupListeners() {
        binding.btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoggedInStatus == null || !mLoggedInStatus.isLoggedIn() || mLoggedInStatus.getUser() == null) {
                    loginSnackbar
                            .setText("You must be logged in to reserve a slot.")
                            .show();
                    return;
                }

                binding.loading.setVisibility(View.VISIBLE);

                ArrayList<Integer> days = new ArrayList<>();
                ArrayList<Prahar> selectedTimeRanges = new ArrayList<>();

                binding.dayChipGroup.getCheckedChipIds().forEach(id ->
                        days.add(ChipMaps.getChipIdToDayMap().get(id)));

                binding.timeChipGroup.getCheckedChipIds().forEach(id -> {
                    selectedTimeRanges.add(ChipMaps.getChipIdToTimeMap().get(id));
                });


                ArrayList<Integer> dayOffsets = calculateDayOffsets(days);

                ArrayList<Interval> intervals = buildTimestampRanges(dayOffsets, selectedTimeRanges);
                Timber.d("Ranges are: %s", intervals);

                // TODO: Use builder pattern
                assistedEventRequest.setToken(mLoggedInStatus.getUser().getToken());
                assistedEventRequest.setIntervals(intervals);
                assistedEventRequest.setReserveEvenIfNoMatch(binding.switchConsent.isChecked());
                assistedEventRequest.setDurationRange(binding.durationSlider.rangeSlider.getValues().stream().sorted().collect(Collectors.toList()));
                assistedEventRequest.setLocationId(locationId);
                assistedEventRequest.setCreator(mLoggedInStatus.getUser().getUsername());

                sendAPIRequest(assistedEventRequest);
            }
        });

        binding.durationSlider.rangeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                binding.durationSlider.valueStart.setText(String.format(Locale.ENGLISH, "%.0f", Collections.min(values)));
                binding.durationSlider.valueEnd.setText(String.format(Locale.ENGLISH, "%.0f", Collections.max(values)));
            }
        });


        binding.allDays.setOnClickListener(v -> {
            binding.chipMon.setChecked(true);
            binding.chipTue.setChecked(true);
            binding.chipWed.setChecked(true);
            binding.chipThu.setChecked(true);
            binding.chipFri.setChecked(true);
            binding.chipSat.setChecked(true);
            binding.chipSun.setChecked(true);
        });
        binding.weekEnds.setOnClickListener(v -> {
            binding.chipSat.setChecked(true);
            binding.chipSun.setChecked(true);
            binding.chipMon.setChecked(false);
            binding.chipTue.setChecked(false);
            binding.chipWed.setChecked(false);
            binding.chipThu.setChecked(false);
            binding.chipFri.setChecked(false);

        });
        binding.weekDays.setOnClickListener(v -> {
            binding.chipMon.setChecked(true);
            binding.chipTue.setChecked(true);
            binding.chipWed.setChecked(true);
            binding.chipThu.setChecked(true);
            binding.chipFri.setChecked(true);
            binding.chipSat.setChecked(false);
            binding.chipSun.setChecked(false);
        });

    }


    private ArrayList<Interval> buildTimestampRanges(ArrayList<Integer> dayOffsets,
                                                     ArrayList<Prahar> selectedPrahars) {
        ArrayList<Interval> intervals = new ArrayList<>();

        for (Integer day : dayOffsets) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY, 0);   // Reset all time fields
            now.set(Calendar.MINUTE, 0);        // So now we only have the date values set
            now.set(Calendar.SECOND, 0);

            now.add(Calendar.DAY_OF_WEEK, day); // Move date to correct offset day

            Calendar start = (Calendar) now.clone();
            Calendar end = (Calendar) now.clone();

            for (Prahar time : selectedPrahars) {
                start.setTime(now.getTime());
                start.set(Calendar.HOUR_OF_DAY, time.getStartHour());

                end.setTime(now.getTime());
                end.set(Calendar.HOUR_OF_DAY, time.getEndHour());

                intervals.add(
                        new Interval()
                                .setStartMillis(start.getTimeInMillis())
                                .setEndMillis(end.getTimeInMillis()));

            }
        }

        return intervals;
    }
}
