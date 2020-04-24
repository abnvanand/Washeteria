package github.abnvanand.washeteria.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.databinding.ActivityReserveSlotBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.APIError;
import github.abnvanand.washeteria.models.pojo.EventCreateBody;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.shareprefs.SessionManager;
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.ui.login.LoginViewModel;
import github.abnvanand.washeteria.utils.Constants;
import github.abnvanand.washeteria.utils.ErrorUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_CLICKED_MILLIS;
import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_MACHINE_ID;
import static github.abnvanand.washeteria.utils.ErrorUtils.CustomCodes.NETWORK_ERROR;

public class ReserveSlotActivity extends AppCompatActivity {
    private static final String durationValueFormat = "%.0f";
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private ActivityReserveSlotBinding binding;
    private LoginViewModel loginViewModel;

    private Executor executor = Executors.newSingleThreadExecutor();
//    private EventCreateBody eventCreateBody;

    private String token;
    private String username;

    private Calendar startsAtCalendarObject;
    private Calendar endsAtCalendarObject;

    private LoggedInStatus mLoggedInStatus;
    private Machine machine;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReserveSlotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarInclude.toolbar);

        String machineId = getIntent().getStringExtra(EXTRA_MACHINE_ID);
        long millis = getIntent().getLongExtra(EXTRA_CLICKED_MILLIS, -1);

        if (millis == -1) {
            Timber.wtf("Millis can't be empty");
            millis = new Date().getTime();
        }


        binding.durationSlider.setValueFrom(Constants.MIN_DURATION);
        binding.durationSlider.setValueTo(Constants.MAX_DURATION);
        binding.durationSlider.setValue(Constants.DEFAULT_DURATION);
        binding.durationSliderValue
                .setText(String.format(Locale.ENGLISH, durationValueFormat,
                        binding.durationSlider.getValue()));


        startsAtCalendarObject = Calendar.getInstance();
        startsAtCalendarObject.setTime(new Date(millis));
        updateUiWithStartEndTime(startsAtCalendarObject, binding.durationSlider.getValue());

        executor.execute(() -> {
            SessionManager sessionManager = new SessionManager(this);
            token = sessionManager.getToken();
            username = sessionManager.getUsername();

            AppDatabase mdb = AppDatabase.getInstance(ReserveSlotActivity.this);

            machine = mdb.machineDao().getMachineById(machineId);
            location = mdb.locationDao().getLocationById(machine.getLocationId());

            binding.machineName.setText(machine.getName());
            binding.locationName.setText(location.getName());

            runOnUiThread(() -> {
                // Enable the button after everything is ready to be submitted
                binding.btnReserveSlot.setEnabled(true);
            });
        });

        setupListeners();

        initViewModel();
    }

    private void setupListeners() {
        binding.durationSlider.addOnChangeListener((slider, value, fromUser) -> {
            binding.durationSliderValue
                    .setText(String.format(Locale.ENGLISH, durationValueFormat, value));

            updateUiWithStartEndTime(startsAtCalendarObject, value);
        });

        View.OnClickListener btnEditStartTime = v -> picker();
        binding.startsAtWidget.eventTime.setOnClickListener(btnEditStartTime);
        binding.startsAtWidget.btnEditStartsAt.setOnClickListener(btnEditStartTime);

        View.OnClickListener btnSubmitClickListener = v -> {
            binding.loading.setVisibility(View.VISIBLE);

            EventCreateBody eventCreateBody =
                    new EventCreateBody(
                            machine.getId(),
                            machine.getLocationId(),
                            startsAtCalendarObject.getTimeInMillis(),
                            endsAtCalendarObject.getTimeInMillis(),
                            false,
                            username
                    );

            sendAPIRequest(eventCreateBody);
        };

        binding.btnReserveSlot.setOnClickListener(btnSubmitClickListener);
    }

    private void sendAPIRequest(EventCreateBody eventCreateBody) {
        WebService webService = RetrofitSingleton.getAuthorizedInstance(token)
                .create(WebService.class);

        webService.createEvent(eventCreateBody).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> response) {
                binding.loading.setVisibility(View.INVISIBLE);

                if (!response.isSuccessful()) {
                    APIError error = ErrorUtils.parseError(response, RetrofitSingleton.authErrorConverter);
                    handleUnsuccessfulResponse(error);
                    return;
                }

                Event body = response.body();

                if (body == null) {
                    Timber.wtf("Event create response %s but body is null", response.code());
                    Toast.makeText(ReserveSlotActivity.this,
                            "Could not find any free slot. Try later or use manual reservation",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ReserveSlotActivity.this,
                        "Created event: " + body.getId(),
                        Toast.LENGTH_SHORT)
                        .show();

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                binding.loading.setVisibility(View.INVISIBLE);

                handleUnsuccessfulResponse(new APIError(NETWORK_ERROR, 0, t.getLocalizedMessage()));
            }
        });
    }

    private void handleUnsuccessfulResponse(APIError error) {
        Timber.e("APIError: %s", error);

        switch (error.getHttpStatusCode()) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                loginViewModel.logout();
                Snackbar.make(
                        binding.getRoot(),
                        "Token has expired. Please login again",
                        Snackbar.LENGTH_LONG)
                        .setAction("LOGIN",
                                v1 -> startActivity(
                                        new Intent(ReserveSlotActivity.this,
                                                LoginActivity.class)))
                        .show();
                break;
            default:
                Toast.makeText(ReserveSlotActivity.this,
                        "Err: " +
                                (!TextUtils.isEmpty(error.getMesssage()) ?
                                        error.getMesssage() :
                                        error.getHttpStatusCode()),
                        Toast.LENGTH_SHORT).show();

        }
    }

    private void initViewModel() {
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        loginViewModel.getLoggedInStatusObservable()
                .observe(this, loggedInStatus -> {
                    mLoggedInStatus = loggedInStatus;
                    if (mLoggedInStatus != null
                            && mLoggedInStatus.isLoggedIn()
                            && mLoggedInStatus.getUser() != null) {
                        token = mLoggedInStatus.getUser().getToken();
                        username = mLoggedInStatus.getUser().getUsername();
                    }
                });

    }


    void picker() {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
        timePicker.setHour(startsAtCalendarObject.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(startsAtCalendarObject.get(Calendar.MINUTE));


        dialogView.findViewById(R.id.btn_set)
                .setOnClickListener(view -> {
//                    TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

                    Calendar newCalendar = new GregorianCalendar(startsAtCalendarObject.get(Calendar.YEAR),
                            startsAtCalendarObject.get(Calendar.MONTH),
                            startsAtCalendarObject.get(Calendar.DAY_OF_MONTH),
                            timePicker.getHour(),
                            timePicker.getMinute());

                    alertDialog.dismiss();

                    startsAtCalendarObject = newCalendar;

                    updateUiWithStartEndTime(startsAtCalendarObject, binding.durationSlider.getValue());
                });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void updateUiWithStartEndTime(Calendar startsAtCalendarObject, float durationSliderValue) {
        endsAtCalendarObject = (Calendar) startsAtCalendarObject.clone();
        endsAtCalendarObject.add(Calendar.MINUTE, (int) durationSliderValue);

        binding.startsAtWidget.eventDate
                .setText(dateFormat.format(startsAtCalendarObject.getTime()));
        binding.startsAtWidget.eventTime
                .setText(timeFormat.format(startsAtCalendarObject.getTime()));


        binding.endsAtWidget.eventDate
                .setText(dateFormat.format(endsAtCalendarObject.getTime()));
        binding.endsAtWidget.eventTime
                .setText(timeFormat.format(endsAtCalendarObject.getTime()));

        Timber.d("StartsAtCalendar Object %s %s",
                dateFormat.format(startsAtCalendarObject.getTime()),
                timeFormat.format(startsAtCalendarObject.getTime()));

        Timber.d("EndsAtCalendar Object %s %s",
                dateFormat.format(endsAtCalendarObject.getTime()),
                timeFormat.format(endsAtCalendarObject.getTime()));
    }
}
