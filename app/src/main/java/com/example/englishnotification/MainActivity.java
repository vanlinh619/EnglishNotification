package com.example.englishnotification;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Serializable {

    private ItemListAdapter adapter;
    private RecyclerView rcListWord;
    private ImageView imAdd;
    public Database database;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setView();

        database = new Database(this);
        ArrayList<ItemData> list = database.getAll();

        adapter = new ItemListAdapter(list, MainActivity.this);
        rcListWord.setAdapter(adapter);
        rcListWord.setLayoutManager(new LinearLayoutManager(this));

        imAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(MainActivity.this);
                dialogAddEditWord.show(fm, "fragment_edit_name");

                /*Notification notification = new Notification(MainActivity.this);
                Random random = new Random();
                int i1 = random.nextInt(80 - 65) + 65;
                NotificationCompat.Builder nb = notification.getChannelNotification("english" + i1, "vietnamese" + i1);
                notification.getManager().notify(i1, nb.build());*/
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void reloadList(Database database){
        ArrayList<ItemData> list = database.getAll();
        adapter = new ItemListAdapter(list, MainActivity.this);
        rcListWord.setAdapter(adapter);
    }

    private void setView() {
        rcListWord = findViewById(R.id.rc_list_item);
        imAdd = findViewById(R.id.im_add_new_word);
    }

    public void startAlarm(ArrayList<ItemData> list) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < list.size(); i++){
            ItemData itemData = list.get(i);

            Intent intent = new Intent(this, AlarmReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putString("english", itemData.english);
            bundle.putString("vietnamese", itemData.vietnamese);
            bundle.putInt("index", i);
            intent.putExtra("bundle", bundle);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, i * 3, intent, 0);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, i * 3 + 1, intent, 0);
            PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, i * 3 + 2, intent, 0);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.set(Calendar.HOUR_OF_DAY, 7);
            calendar1.set(Calendar.MINUTE, 00);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(System.currentTimeMillis());
            calendar2.set(Calendar.HOUR_OF_DAY, 12);
            calendar2.set(Calendar.MINUTE, 00);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTimeInMillis(System.currentTimeMillis());
            calendar3.set(Calendar.HOUR_OF_DAY, 18);
            calendar3.set(Calendar.MINUTE, 00);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis() + 86400000, pendingIntent1);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis() + 86400000, pendingIntent2);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis() + 86400000, pendingIntent3);
        }
    }
}