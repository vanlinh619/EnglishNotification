package com.ale.englishnotification.handle.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.ale.englishnotification.MainActivity;
import com.ale.englishnotification.R;

public class Notification extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    public static final int PERSON_SETUP_NOTIFY = 0;
    public static final int BOT_SETUP_NOTIFY = 1;
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

    public NotificationCompat.Builder getChannelNotification(Bundle bundle, int flags) {
        String english = bundle.getString("english");
        String vietnamese = bundle.getString("vietnamese");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        bundle.putInt("flags", flags);
        notifyIntent.putExtra("bundle", bundle);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String strAction = "";
        if (flags == PERSON_SETUP_NOTIFY) {
            strAction = "OFF NOTIFY";
        } else {
            strAction = "OFF AUTO NOTIFY";
        }

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(english)
                .setContentText(vietnamese)
                .setSmallIcon(R.drawable.en)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.icon_dot_blue, strAction, notifyPendingIntent);

    }
}

