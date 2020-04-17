package github.abnvanand.washeteria.utils;

import github.abnvanand.washeteria.R;

public class WeekViewType {
//    DAY_VIEW(1, R.id.action_day_view),
//    THREE_DAY_VIEW(3, R.id.action_three_day_view),
//    WEEK_VIEW(7, R.id.action_week_view);

    private WeekViewType() {
    }


    public static int getNumVisibleDays(int action) {
        switch (action) {
            case R.id.action_day_view:
                return 1;
            case R.id.action_three_day_view:
                return 3;
            case R.id.action_week_view:
                return 7;
        }
        return 1;
    }

    public static int getAction(int numVisibleDays) {
        switch (numVisibleDays) {
            case 1:
                return R.id.action_day_view;
            case 3:
                return R.id.action_three_day_view;
            case 7:
                return R.id.action_week_view;

        }
        return R.id.action_day_view;
    }
}