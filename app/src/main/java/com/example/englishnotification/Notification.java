package com.example.englishnotification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notification extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    String GROUP_KEY = "GROUP_KEY";

    public Notification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String english, String vietnamese) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(english)
                .setContentText(vietnamese)
                .setSmallIcon(R.drawable.en)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true);
    }
}

