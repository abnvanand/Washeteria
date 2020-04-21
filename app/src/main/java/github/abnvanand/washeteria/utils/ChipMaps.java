package github.abnvanand.washeteria.utils;

import java.util.Calendar;
import java.util.HashMap;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.ui.assistant.Prahar;

public class ChipMaps {
    private static HashMap<Integer, Integer> chipIdToDayMap = new HashMap<>();
    private static HashMap<Integer, Prahar> chipIdToTimeMap = new HashMap<>();

    static {
        chipIdToDayMap.put(R.id.chipSun, Calendar.SUNDAY);
        chipIdToDayMap.put(R.id.chipMon, Calendar.MONDAY);
        chipIdToDayMap.put(R.id.chipTue, Calendar.TUESDAY);
        chipIdToDayMap.put(R.id.chipWed, Calendar.WEDNESDAY);
        chipIdToDayMap.put(R.id.chipThu, Calendar.THURSDAY);
        chipIdToDayMap.put(R.id.chipFri, Calendar.FRIDAY);
        chipIdToDayMap.put(R.id.chipSat, Calendar.SATURDAY);

        chipIdToTimeMap.put(R.id.chipMorning, Prahar.MORNING);
        chipIdToTimeMap.put(R.id.chipAfternoon, Prahar.AFTERNOON);
        chipIdToTimeMap.put(R.id.chipEvening, Prahar.EVENING);
        chipIdToTimeMap.put(R.id.chipNight, Prahar.NIGHT);
    }


    public static HashMap<Integer, Integer> getChipIdToDayMap() {
        return chipIdToDayMap;
    }

    public static HashMap<Integer, Prahar> getChipIdToTimeMap() {
        return chipIdToTimeMap;
    }
}
