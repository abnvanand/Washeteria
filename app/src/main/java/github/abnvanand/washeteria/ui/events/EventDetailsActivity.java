package github.abnvanand.washeteria.ui.events;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.concurrent.Executors;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.databinding.ActivityEventDetailsBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.utils.Constants;
import timber.log.Timber;

public class EventDetailsActivity extends AppCompatActivity {

    ActivityEventDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarInclude.toolbar);

        String eventId = getIntent().getStringExtra("EVENT_ID");
        Timber.d("eventId: %s", eventId);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase mDb = AppDatabase.getInstance(EventDetailsActivity.this);
                Event eventById = mDb.eventDao().getEventById(eventId);
                if (eventById == null) {
                    Timber.e("Event %s not found in db", eventId);
                    return;
                }

                Machine machineById = mDb.machineDao().getMachineById(eventById.getMachineId());
                Location locationById = mDb.locationDao().getLocationById(eventById.getLocationId());

                binding.includeEventDetails
                        .machineName.setText(machineById.getName());
                binding.includeEventDetails
                        .locationName.setText(locationById.getName());
                binding.includeEventDetails.startsAtWidget
                        .eventDate.setText(Constants.dateFormat.format(new Date(eventById.getStartsAtMillis())));
                binding.includeEventDetails.startsAtWidget
                        .eventTime.setText(Constants.timeFormat.format(new Date(eventById.getStartsAtMillis())));

                binding.includeEventDetails.endsAtWidget
                        .eventDate.setText(Constants.dateFormat.format(new Date(eventById.getEndsAtMillis())));
                binding.includeEventDetails.endsAtWidget
                        .eventTime.setText(Constants.timeFormat.format(new Date(eventById.getEndsAtMillis())));

            }
        });
    }
}
