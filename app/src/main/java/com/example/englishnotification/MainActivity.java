package com.example.englishnotification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Serializable {

    private ItemListAdapter adapter;
    private RecyclerView rcListWord;
    private ImageView imAdd, imSearch;
    private EditText edSearch;
    private TextView txTitle;
    private ConstraintLayout ctMainLayout;
    public Database database;
    public ArrayList<ItemData> listData;
    public TextToSpeech textToSpeechEnglish;
    public TextToSpeech textToSpeechVietnamese;
    public Translator translator;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setView();

        // Create an English-German translator:
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build();
        translator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        // Model downloaded successfully. Okay to start translating.
                        // (Set a flag, unhide the translation UI, etc.)
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                Toast.makeText(MainActivity.this, "Model couldn’t be downloaded or other internal error.", Toast.LENGTH_LONG).show();
                            }
                        });

        database = new Database(this);
        listData = database.getAll();

        adapter = new ItemListAdapter(listData, MainActivity.this);
        rcListWord.setAdapter(adapter);
        rcListWord.setLayoutManager(new LinearLayoutManager(this));

        imAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(MainActivity.this);
                dialogAddEditWord.show(fm, "fragment_edit_name");
            }
        });

        imSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandButtonSearch();
            }
        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<ItemData> list = new ArrayList<>();
                for (ItemData itemData : listData) {
                    String english = itemData.english.toLowerCase();
                    String vietnamese = itemData.vietnamese.toLowerCase();

                    if (english.indexOf(s.toString().toLowerCase()) != -1 ||
                            vietnamese.indexOf(s.toString().toLowerCase()) != -1) {
                        list.add(itemData);
                    }
                }
                adapter = new ItemListAdapter(list, MainActivity.this);
                rcListWord.setAdapter(adapter);
            }
        });

        ctMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shrinkButtonSearch();
            }
        });
    }

    private void expandButtonSearch() {
        imSearch.setVisibility(View.GONE);
        txTitle.setVisibility(View.GONE);
        imAdd.setVisibility(View.GONE);
        edSearch.setVisibility(View.VISIBLE);
    }

    public void shrinkButtonSearch() {
        imSearch.setVisibility(View.VISIBLE);
        txTitle.setVisibility(View.VISIBLE);
        imAdd.setVisibility(View.VISIBLE);
        edSearch.setVisibility(View.GONE);
        hideKeyboard(this, edSearch);
        edSearch.setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void reloadList() {
        adapter = new ItemListAdapter(listData, MainActivity.this);
        rcListWord.setAdapter(adapter);
        shrinkButtonSearch();
    }

    private void setView() {
        rcListWord = findViewById(R.id.rc_list_item);
        imAdd = findViewById(R.id.im_add_new_word);
        edSearch = findViewById(R.id.ed_search);
        txTitle = findViewById(R.id.tx_title);
        imSearch = findViewById(R.id.im_search);
        ctMainLayout = findViewById(R.id.ct_main_layout);
    }

    public void startAlarm(ArrayList<ItemData> list) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < list.size(); i++) {
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