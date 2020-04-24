package github.abnvanand.washeteria.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final int MIN_DURATION = 1;
    public static final int MAX_DURATION = 60;
    public static final int DEFAULT_DURATION = 30;
    public static final String durationValueFormat = "%.0f min";

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
}
