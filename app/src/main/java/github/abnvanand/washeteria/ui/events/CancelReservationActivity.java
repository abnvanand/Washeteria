package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.shareprefs.SessionManager;
import retrofit2.Retrofit;
import timber.log.Timber;

import static github.abnvanand.washeteria.ui.events.EventsForMachineActivity.EXTRA_EVENT_ID;

public class CancelReservationActivity extends AppCompatActivity {
    Executor executor = Executors.newSingleThreadExecutor();
    String token;
    Event eventToCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);

        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);

        TextView machineNameTV = findViewById(R.id.machineName);
        EditText startsAtDate = findViewById(R.id.startsAtDate);
        EditText startsAtTime = findViewById(R.id.startsAtTime);
        EditText endsAtDate = findViewById(R.id.endsAtDate);
        EditText endsAtTime = findViewById(R.id.endsAtTime);
        Button btnCancelEvent = findViewById(R.id.btnCancelEvent);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase mdb = AppDatabase.getInstance(CancelReservationActivity.this);

                eventToCancel = mdb.eventDao().getEventById(eventId);
                String machineId = eventToCancel.getMachineId();
                String locationId = eventToCancel.getLocationId();
                String creator = eventToCancel.getCreator();
                String startsAt = eventToCancel.getStartsAt();
                String endsAt = eventToCancel.getEndsAt();
                Machine machineById = mdb.machineDao().getMachineById(machineId);
                mdb.locationDao().getLocationById(locationId);
                SessionManager sessionManager = new SessionManager(CancelReservationActivity.this);
                token = sessionManager.getToken();

                machineNameTV.setText(machineById.getName());
                machineNameTV.setText(machineById.getName());
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

                Retrofit authorizedInstance = RetrofitSingleton.getAuthorizedInstance(token);
                WebService webService = authorizedInstance.create(WebService.class);
                webService.cancelEvent(eventToCancel);
            }
        });
    }
}
