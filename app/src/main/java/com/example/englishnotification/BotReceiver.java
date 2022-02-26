package com.example.englishnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Random;

public class BotReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Database database = new Database(context);
        ArrayList<ItemData> list = database.getAll();
        Random random = new Random();
        int id[] = {-1, -1, -1};
        ItemData itemData[] = new ItemData[3];
        for (int i = 0; i < 3; i++){
            id[i] = random.nextInt(list.size());
            for (int j = i - 1; j >= 0; j--){
                while (id[i] == id[j]){
                    id[i] = random.nextInt(list.size());
                    if(list.size() > 3){
                        j = i - 1;
                    }
                }
            }
            itemData[i] = list.get(id[i]);
            Notification notification = new Notification(context);
            NotificationCompat.Builder nb = notification.getChannelNotification(itemData[i].english, itemData[i].vietnamese);
            notification.getManager().notify(itemData[i].id, nb.build());
        }
    }
}
