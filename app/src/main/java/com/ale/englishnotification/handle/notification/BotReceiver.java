package com.ale.englishnotification.handle.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ale.englishnotification.MainActivity;
import com.ale.englishnotification.model.Mean;
import com.ale.englishnotification.model.Word;
import com.ale.englishnotification.model.database.Database;

import java.util.ArrayList;
import java.util.Random;

public class BotReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Database database = new Database(context);
        ArrayList<Word> list = database.getDataForNotification();
        ArrayList<Mean> means = database.getAllMean();
        MainActivity.addMeansToWord(list, means);
        Random random = new Random();
        int id[] = {-1, -1, -1};
        for (int i = 0; i < 3; i++){
            id[i] = random.nextInt(list.size());
            if(list.size() >= 3) {
                for (int j = i - 1; j >= 0; j--) {
                    while (id[i] == id[j]) {
                        id[i] = random.nextInt(list.size());
                        j = i - 1;
                    }
                }
            }
            Word word = list.get(id[i]);
            Bundle bundle = new Bundle();
            bundle.putString("english", word.english);
            String ms = MainActivity.meansToString(word);
            bundle.putString("vietnamese", ms);
            bundle.putInt("id", word.id);
            Notification notification = new Notification(context);
            NotificationCompat.Builder nb = notification.getChannelNotification(bundle, Notification.BOT_SETUP_NOTIFY);
            notification.getManager().notify(word.id, nb.build());
        }
    }
}
