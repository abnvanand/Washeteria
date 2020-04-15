package github.abnvanand.washeteria.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class DateConverters {
    public static Calendar getCalendarFromString(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            Timber.e(e.getLocalizedMessage());
        }
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }

        return null;
    }
}
