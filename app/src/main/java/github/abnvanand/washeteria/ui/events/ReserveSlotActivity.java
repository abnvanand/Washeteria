package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_slot);

        TextView machineNameTV = findViewById(R.id.machineName);
        EditText startsAtDate = findViewById(R.id.startsAtDate);
        EditText startsAtTime = findViewById(R.id.startsAtTime);
        EditText endsAtDate = findViewById(R.id.endsAtDate);
        EditText endsAtTime = findViewById(R.id.endsAtTime);
        Button btnSubmit = findViewById(R.id.btnReserveSlot);

        String machineId = getIntent().getStringExtra(EXTRA_MACHINE_ID);
        long millis = getIntent().getLongExtra(EXTRA_CLICKED_MILLIS, -1);

        if (millis == -1)
            Timber.wtf("This is fucked up");

        Calendar startsAt = Calendar.getInstance();
        startsAt.setTime(new Date(millis));

        Calendar endsAt = (Calendar) startsAt.clone();
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

        startsAtDate.setText(startsAt.getTime().toString());
        endsAtDate.setText(endsAt.getTime().toString());


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
//                                Event body = response.body();
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
    }
}
