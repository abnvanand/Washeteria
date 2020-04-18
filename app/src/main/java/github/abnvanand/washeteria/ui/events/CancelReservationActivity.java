package github.abnvanand.washeteria.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.shareprefs.SessionManager;
import github.abnvanand.washeteria.ui.login.LoggedInStatus;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.ui.login.LoginViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_EVENT_ID;

public class CancelReservationActivity extends AppCompatActivity {
    Executor executor = Executors.newSingleThreadExecutor();
    String token;
    Event eventToCancel;
    LoginViewModel loginViewModel;
    LoggedInStatus mLoggedInStatus;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootView = findViewById(R.id.rootView);
        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        TextView machineNameTV = findViewById(R.id.machineName);
        EditText startsAtEditText = findViewById(R.id.startsAtDate);
        EditText endsAtEditText = findViewById(R.id.endsAtDate);
        Button btnCancelEvent = findViewById(R.id.btnCancelEvent);

        initViewModel();


        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase mdb = AppDatabase.getInstance(CancelReservationActivity.this);

                eventToCancel = mdb.eventDao().getEventById(eventId);
                if (eventToCancel == null)
                    Timber.wtf("eventToCancel for id %s does not exist", eventId);
                else
                    Timber.d("eventToCancel %s", eventToCancel);

                String machineId = eventToCancel.getMachineId();
                String locationId = eventToCancel.getLocationId();
                String creator = eventToCancel.getCreator();
                Long startsAt = eventToCancel.getStartsAt();
                Long endsAt = eventToCancel.getEndsAt();
                SessionManager sessionManager = new SessionManager(CancelReservationActivity.this);
                token = sessionManager.getToken();


                Machine machineById = mdb.machineDao().getMachineById(machineId);
                mdb.locationDao().getLocationById(locationId);

                machineNameTV.setText(machineById.getName());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
                startsAtEditText.setText(dateFormat.format(new Date(eventToCancel.getStartsAtMillis())));
                endsAtEditText.setText(dateFormat.format(new Date(eventToCancel.getEndsAtMillis())));
            }
        });


        btnCancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get authInstance from Retrofit singleton
                // and send PUT request with event.cancelled = true;
                if (TextUtils.isEmpty(token))
                    Timber.wtf("Token can't be empty");

                if (eventToCancel == null) {
                    Timber.wtf("EventToCancel can't be null");
                }

                eventToCancel.setCancelled(true);


                Retrofit authorizedInstance = RetrofitSingleton.getAuthorizedInstance(token);
                WebService webService = authorizedInstance.create(WebService.class);
                webService.cancelEvent(eventToCancel).enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (!response.isSuccessful()) {
                            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                loginViewModel.logout();
                                Snackbar
                                        .make(rootView,
                                                "Token has expired. Please login again",
                                                Snackbar.LENGTH_LONG)
                                        .setAction("LOGIN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(CancelReservationActivity.this, LoginActivity.class));
                                            }
                                        }).show();
                                return;
                            } else {

                                Toast.makeText(CancelReservationActivity.this,
                                        "Err: " + (!TextUtils.isEmpty(response.message()) ? response.message() : response.code()),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            setResult(RESULT_CANCELED);
                            return;
                        }

                        Event body = response.body();
                        if (body == null) {
                            Timber.wtf("events cancel API response body MUST NOT be empty");
                            return;
                        }

                        Toast.makeText(CancelReservationActivity.this,
                                "Cancelled event: " + body.getId(),
                                Toast.LENGTH_SHORT)
                                .show();

                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        Toast.makeText(CancelReservationActivity.this,
                                "Error: " + t.getLocalizedMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
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
                    }
                });

    }
}
