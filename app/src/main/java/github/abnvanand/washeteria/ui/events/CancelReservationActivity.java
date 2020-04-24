package github.abnvanand.washeteria.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.databinding.ActivityCancelReservationBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.APIError;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.ui.login.LoginViewModel;
import github.abnvanand.washeteria.utils.Constants;
import github.abnvanand.washeteria.utils.ErrorUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_EVENT_ID;

public class CancelReservationActivity extends AppCompatActivity {
    Executor executor = Executors.newSingleThreadExecutor();
    //    String token;
    Event eventToCancel;
    LoginViewModel loginViewModel;
    LoggedInStatus mLoggedInStatus;

    private Snackbar loginSnackbar;
    private ActivityCancelReservationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCancelReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarInclude.toolbar);

        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        initViewModel();

        loginSnackbar = Snackbar
                .make(binding.getRoot(),
                        "You must login to perform this action.",
                        Snackbar.LENGTH_LONG)
                .setAction("LOGIN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CancelReservationActivity.this,
                                LoginActivity.class));
                    }
                });


        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase mdb = AppDatabase.getInstance(CancelReservationActivity.this);

                eventToCancel = mdb.eventDao().getEventById(eventId);
                if (eventToCancel == null) {
                    Timber.wtf("eventToCancel for id %s does not exist in local db", eventId);
                    return;
                }

                Timber.d("eventToCancel %s", eventToCancel);
                // cancel button is available only if the event is not yet cancelled
                binding.btnCancelEvent.setEnabled(!eventToCancel.isCancelled());

                Machine machineById = mdb.machineDao().getMachineById(eventToCancel.getMachineId());
                Location locationById = mdb.locationDao().getLocationById(eventToCancel.getLocationId());

                binding.eventDetails.machineName.setText(machineById.getName());
                binding.eventDetails.locationName.setText(locationById.getName());

                binding.eventDetails
                        .startsAtWidget
                        .eventDate.setText(Constants.dateFormat.format(eventToCancel.getStartsAtMillis()));
                binding.eventDetails
                        .startsAtWidget
                        .eventTime.setText(Constants.timeFormat.format(eventToCancel.getStartsAtMillis()));

                binding.eventDetails
                        .endsAtWidget
                        .eventDate.setText(Constants.dateFormat.format(eventToCancel.getEndsAtMillis()));
                binding.eventDetails
                        .endsAtWidget
                        .eventTime.setText(Constants.timeFormat.format(eventToCancel.getEndsAtMillis()));


            }
        });


        binding.btnCancelEvent.setOnClickListener(v -> {
            if (mLoggedInStatus == null || !mLoggedInStatus.isLoggedIn() || mLoggedInStatus.getUser() == null) {
                loginSnackbar
                        .setText("You must be logged in to reserve a slot.")
                        .show();
                return;
            }

            if (eventToCancel == null) {
                Timber.wtf("EventToCancel can't be null");
                Toast.makeText(CancelReservationActivity.this,
                        "This event cannot be cancelled! NULL",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            eventToCancel.setCancelled(true);

            sendAPIRequest(mLoggedInStatus.getUser().getToken());
        });
    }

    private void sendAPIRequest(String token) {
        binding.loading.setVisibility(View.VISIBLE);
        Retrofit authorizedInstance = RetrofitSingleton.getAuthorizedInstance(token);
        WebService webService = authorizedInstance.create(WebService.class);
        webService.cancelEvent(eventToCancel).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                binding.loading.setVisibility(View.INVISIBLE);

                if (!response.isSuccessful()) {
                    APIError error = ErrorUtils.parseError(response, RetrofitSingleton.authErrorConverter);
                    handleUnsuccessfulResponse(error);
                    return;
                }

                Event body = response.body();
                if (body == null)
                    Timber.wtf("Event cancel response code is %s but body is null",
                            response.code());

                Toast.makeText(CancelReservationActivity.this,
                        "Cancelled event: " + eventToCancel.getId(),
                        Toast.LENGTH_SHORT)
                        .show();

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                binding.loading.setVisibility(View.INVISIBLE);

                Toast.makeText(CancelReservationActivity.this,
                        "Error: " + t.getLocalizedMessage(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void handleUnsuccessfulResponse(APIError error) {
        switch (error.getHttpStatusCode()) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                loginViewModel.logout();
                loginSnackbar
                        .setText("Token has expired. Please login again")
                        .show();
                break;
            default:
                Toast.makeText(CancelReservationActivity.this,
                        "Err: "
                                + (!TextUtils.isEmpty(error.getMesssage())
                                ? error.getMesssage() : error.getHttpStatusCode()),
                        Toast.LENGTH_SHORT)
                        .show();
        }
    }

    private void initViewModel() {
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        loginViewModel.getLoggedInStatusObservable()
                .observe(this, loggedInStatus -> {
                    mLoggedInStatus = loggedInStatus;
                });
    }
}
