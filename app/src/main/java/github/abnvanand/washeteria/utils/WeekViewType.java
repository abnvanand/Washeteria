package github.abnvanand.washeteria.utils;

import github.abnvanand.washeteria.R;

public class WeekViewType {
    private static BidirectionalMap<Integer, Integer> typeToDays =
            new BidirectionalMap<>();

    static {
        typeToDays.put(R.id.action_day_view, 1);
        typeToDays.put(R.id.action_three_day_view, 3);
        typeToDays.put(R.id.action_week_view, 7);
    }

    private WeekViewType() {
    }


    public static Integer getNumVisibleDays(Integer action) {
        return typeToDays.get(action);
    }

    public static int getAction(int numVisibleDays) {
        return typeToDays.getKey(numVisibleDays);
    }
}