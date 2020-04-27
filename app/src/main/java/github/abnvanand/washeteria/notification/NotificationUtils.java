package github.abnvanand.washeteria.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

import github.abnvanand.washeteria.R;

public class NotificationUtils extends ContextWrapper {

    public static final String CHANNEL_ID = "github.abnvanand.washeteria.notif.ANDROID";
    public static final String CHANNEL_NAME = "SCHEDULED EVENTS CHANNEL";
    private NotificationManager mManager;

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {
        // create android channel
        NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notification posted to this channel should vibrate.
        defaultChannel.enableVibration(true);

        // Sets whether notifications posted to this channel should display notification lights
        defaultChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this channel
        defaultChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        defaultChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(defaultChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }


    public Notification.Builder getAndroidChannelNotification(String title,
                                                              String body,
                                                              PendingIntent pendingIntent) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_washing_machine)
                .setAutoCancel(true);
    }
}