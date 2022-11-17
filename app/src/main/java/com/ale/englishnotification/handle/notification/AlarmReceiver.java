package com.ale.englishnotification.handle.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
//        String english = bundle.getString("english");
//        String vietnamese = bundle.getString("vietnamese");
        int id = bundle.getInt("id");
        Notification notification = new Notification(context);
        NotificationCompat.Builder nb = notification.getChannelNotification(bundle, Notification.PERSON_SETUP_NOTIFY);
        notification.getManager().notify(id, nb.build());
    }
}