package com.ale.englishnotification.handle.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ale.englishnotification.model.database.Database;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Database database = new Database(context);
        Bundle bundle = intent.getBundleExtra("bundle");
        int flags = bundle.getInt("flags");
        int id = bundle.getInt("id");
        if (flags == Notification.PERSON_SETUP_NOTIFY) {
            database.updateOffNotify(id);
            destroyRepeatAlarm(context, id);
        } else {
            database.updateOffBotNotify(id);
        }
    }


    public void destroyRepeatAlarm(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
