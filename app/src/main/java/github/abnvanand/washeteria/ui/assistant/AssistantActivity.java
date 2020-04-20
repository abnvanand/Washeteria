package github.abnvanand.washeteria.ui.assistant;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.databinding.ActivityAssistantBinding;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.models.pojo.AssistedReservation;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AssistantActivity extends AppCompatActivity {

    ActivityAssistantBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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

        binding.durationSlider.rangeSlider.setValues((float) Constants.MAX_DURATION / 2, (float) Constants.MAX_DURATION);
        binding.durationSlider.valueStart.setText(String.format(Locale.ENGLISH, "%d", Constants.MAX_DURATION / 2));

        binding.durationSlider.valueEnd.setText(String.format(Locale.ENGLISH, "%d", Constants.MAX_DURATION));
        binding.durationSlider.rangeSlider.setValueFrom(Constants.MIN_DURATION);
        binding.durationSlider.rangeSlider.setValueTo(Constants.MAX_DURATION);
        binding.durationSlider.rangeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                binding.durationSlider.valueStart.setText(String.format(Locale.ENGLISH, "%.0f", Collections.min(values)));
                binding.durationSlider.valueEnd.setText(String.format(Locale.ENGLISH, "%.0f", Collections.max(values)));
            }
        });

        HashMap<Integer, Integer> chipIdToDayMap = new HashMap<>();
        chipIdToDayMap.put(R.id.chipSun, Calendar.SUNDAY);
        chipIdToDayMap.put(R.id.chipMon, Calendar.MONDAY);
        chipIdToDayMap.put(R.id.chipTue, Calendar.TUESDAY);
        chipIdToDayMap.put(R.id.chipWed, Calendar.WEDNESDAY);
        chipIdToDayMap.put(R.id.chipThu, Calendar.THURSDAY);
        chipIdToDayMap.put(R.id.chipFri, Calendar.FRIDAY);
        chipIdToDayMap.put(R.id.chipSat, Calendar.SATURDAY);

        HashMap<Integer, Prahar> chipIdToTimeMap = new HashMap<>();
        chipIdToTimeMap.put(R.id.chipMorning, Prahar.MORNING);
        chipIdToTimeMap.put(R.id.chipAfternoon, Prahar.AFTERNOON);
        chipIdToTimeMap.put(R.id.chipEvening, Prahar.EVENING);
        chipIdToTimeMap.put(R.id.chipNight, Prahar.NIGHT);

        binding.btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loading.setVisibility(View.VISIBLE);

                ArrayList<Integer> days = new ArrayList<>();
                ArrayList<Prahar> selectedDayTimes = new ArrayList<>();

                binding.dayChipGroup.getCheckedChipIds().forEach(id ->
                        days.add(chipIdToDayMap.get(id)));

                binding.timeChipGroup.getCheckedChipIds().forEach(id -> {
                    selectedDayTimes.add(chipIdToTimeMap.get(id));
                });


                Calendar now = Calendar.getInstance();
                int today = now.get(Calendar.DAY_OF_WEEK);

                ArrayList<Integer> dayOffsets = new ArrayList<>();
                days.forEach(day -> {
                    int diff = (day - today + 7) % 7;
                    Timber.d("day: %s, today: %s, diff: %s", day, today, diff);
                    dayOffsets.add(diff);
                });

                ArrayList<Interval> intervals = buildTimestampRanges(dayOffsets, selectedDayTimes);
                Timber.d("Ranges are: %s", intervals);


                // FIXME: GET Token
                RetrofitSingleton.getAuthorizedInstance("token")
                        .create(WebService.class)
                        .createAssistedEvent(new AssistedReservation(intervals,
                                binding.switchConsent.isChecked(),
                                binding.durationSlider.rangeSlider.getValues().stream().sorted().collect(Collectors.toList())))

                        .enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(Call<Event> call, Response<Event> response) {
                                binding.loading.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onFailure(Call<Event> call, Throwable t) {
                                binding.loading.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });

    }


    private ArrayList<Interval> buildTimestampRanges(ArrayList<Integer> dayOffsets,
                                                     ArrayList<Prahar> validTimes) {
        ArrayList<Interval> intervals = new ArrayList<>();

        for (Integer day : dayOffsets) {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY, 0);   // Reset all time fields
            now.set(Calendar.MINUTE, 0);        // So now we only have the date values set
            now.set(Calendar.SECOND, 0);

            now.add(Calendar.DAY_OF_WEEK, day); // Move date to correct offset day

            Calendar start = (Calendar) now.clone();
            Calendar end = (Calendar) now.clone();

            for (Prahar time : validTimes) {
                start.setTime(now.getTime());
                start.set(Calendar.HOUR_OF_DAY, time.getStartHour());

                end.setTime(now.getTime());
                end.set(Calendar.HOUR_OF_DAY, time.getEndHour());

                intervals.add(new Interval(start.getTimeInMillis(),
                        end.getTimeInMillis()));

            }
        }

        return intervals;
    }
}
