package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.models.pojo.EventCreateBody;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.shareprefs.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_CLICKED_MILLIS;
import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_MACHINE_ID;

public class ReserveSlotActivity extends AppCompatActivity {
    private int SLOT_MAX_LIMIT_MINUTES = 50;

    Executor executor = Executors.newSingleThreadExecutor();
    EventCreateBody eventCreateBody;
    String token;
    String username;
    Calendar startsAt;
    Calendar endsAt;
    EditText startsAtEditText;
    EditText endsAtEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_slot);

        TextView machineNameTV = findViewById(R.id.machineName);
        startsAtEditText = findViewById(R.id.startsAtDate);
        endsAtEditText = findViewById(R.id.endsAtDate);
        ImageButton editStartsAt = findViewById(R.id.btnEditStartsAt);
        ImageButton editEndsAt = findViewById(R.id.btnEditEndsAt);

        Button btnSubmit = findViewById(R.id.btnReserveSlot);

        String machineId = getIntent().getStringExtra(EXTRA_MACHINE_ID);
        long millis = getIntent().getLongExtra(EXTRA_CLICKED_MILLIS, -1);

        if (millis == -1)
            Timber.wtf("This is fucked up");


        startsAt = Calendar.getInstance();
        startsAt.setTime(new Date(millis));

        endsAt = (Calendar) startsAt.clone();
        endsAt.add(Calendar.MINUTE, SLOT_MAX_LIMIT_MINUTES);

        executor.execute(() -> {
            SessionManager sessionManager = new SessionManager(this);
            token = sessionManager.getToken();
            username = sessionManager.getUsername();


            AppDatabase mdb = AppDatabase.getInstance(ReserveSlotActivity.this);
            Machine machine = mdb.machineDao().getMachineById(machineId);

            machineNameTV.setText(machine.getName());

            eventCreateBody = new EventCreateBody(machine.getId(),
                    machine.getLocationId(),
                    startsAt.getTimeInMillis(),
                    endsAt.getTimeInMillis(),
                    false,
                    username
            );

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Enable the button after everything is ready to be submitted
                    btnSubmit.setEnabled(true);
                }
            });
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        startsAtEditText.setText(dateFormat.format(startsAt.getTime()));
        endsAtEditText.setText(dateFormat.format(endsAt.getTime()));


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebService webService = RetrofitSingleton.getAuthorizedInstance(token)
                        .create(WebService.class);
                webService.createEvent(eventCreateBody)
                        .enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(Call<Event> call, Response<Event> response) {
                                Toast.makeText(ReserveSlotActivity.this, "OnResponse", Toast.LENGTH_SHORT).show();
                                if (!response.isSuccessful()) {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                                Event body = response.body();

                                if (body == null) {
                                    Timber.wtf("event create API response body MUST NOT be empty");
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
                            public void onFailure(Call<Event> call, Throwable t) {
                                Toast.makeText(ReserveSlotActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        editStartsAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker("start");
            }
        });

        editEndsAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker("end");
            }
        });
    }


    void picker(String type) {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();


        dialogView.findViewById(R.id.btn_set)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                        Calendar newCalendar = new GregorianCalendar(startsAt.get(Calendar.YEAR),
                                startsAt.get(Calendar.MONTH),
                                startsAt.get(Calendar.DAY_OF_MONTH),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        alertDialog.dismiss();
                        if (type.equalsIgnoreCase("start")) {
                            startsAt = newCalendar;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());

                            startsAtEditText.setText(dateFormat.format(startsAt.getTime()));
                        } else if (type.equalsIgnoreCase("end")) {
                            endsAt = newCalendar;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
                            endsAtEditText.setText(dateFormat.format(endsAt.getTime()));
                        }
                    }
                });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
}
