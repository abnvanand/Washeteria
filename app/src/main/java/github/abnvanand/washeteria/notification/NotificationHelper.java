package github.abnvanand.washeteria.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import github.abnvanand.washeteria.models.Event;
import timber.log.Timber;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by ptyagi on 4/17/17.
 */

public class NotificationHelper {
    private static final int ALARM_TYPE_EXACT = 102;
    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManager;
    private static PendingIntent broadcast;

    public static void setOneTimeRTCNotification(Context context, Event event) {
        Calendar notifyAt = Calendar.getInstance();
        notifyAt.setTimeInMillis(event.getStartsAtMillis());
        notifyAt.add(Calendar.MINUTE, -5);// Notify 5 minutes before the event starts

        Timber.d("Setting exact notification for: %s", notifyAt.getTime());

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("EVENT_ID", event.getId());

        broadcast = PendingIntent.getBroadcast(context,
                ALARM_TYPE_EXACT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notifyAt.getTimeInMillis(),
                    broadcast);
        }
    }

    public static void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(broadcast);
        }
    }

}
