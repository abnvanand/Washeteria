package github.abnvanand.washeteria.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import github.abnvanand.washeteria.database.AppDatabase;
import github.abnvanand.washeteria.models.Event;
import github.abnvanand.washeteria.ui.events.EventDetailsActivity;
import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventId = intent.getStringExtra("EVENT_ID");

        Timber.d("inside onReceive eventId: %s", eventId);
        //Get notification manager to manage/send notifications


        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        Intent intentToFire = new Intent(context, EventDetailsActivity.class);
        intentToFire.putExtra("EVENT_ID", eventId);
        //set flag to restart/relaunch the app
        intentToFire.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context,
                        NotificationHelper.ALARM_TYPE_RTC,
                        intentToFire,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "Laundry Time! ";
        String body = "Be ready with your bucket.\n" +
                "Click for details.";

        NotificationUtils mNotificationUtils;
        mNotificationUtils = new NotificationUtils(context);
        Notification.Builder nb = mNotificationUtils.
                getAndroidChannelNotification(title, body, pendingIntent);
        mNotificationUtils.getManager().notify(101, nb.build());

        Timber.d("Here6");
    }


}
