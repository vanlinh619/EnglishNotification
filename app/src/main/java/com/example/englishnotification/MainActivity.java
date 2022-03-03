package com.example.englishnotification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Serializable {

    private ItemListAdapter adapter;
    private RecyclerView rcListWord;
    private ImageView imAdd, imSearch, imExpandOption, imImport, imExport;
    private EditText edSearch;
    private TextView txTitle;
    private ConstraintLayout ctMainLayout, ctOption, ctHead;
    private Switch swAutoNotify;
    private final String fileName = "english.en";
    private final String dirName = "English";
    public Database database;
    public ArrayList<ItemData> listData;
    public TextToSpeech textToSpeechEnglish;
    public TextToSpeech textToSpeechVietnamese;
    public Translator translator;
    public Config config;
    private static AlertDialog alertDialog;

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

        //Config
        config = database.getConfig();
        if (config.autoNotify == 0) {
            swAutoNotify.setChecked(false);
        } else {
            swAutoNotify.setChecked(true);
        }

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

        swAutoNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    config.autoNotify = 1;
                    setAutoNotify();
                } else {
                    config.autoNotify = 0;
                    destroyAutoNotify();
                }
                database.updateConfig(config);
                reloadList();
            }
        });

        imExpandOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctOption.getVisibility() == View.VISIBLE) {
                    ctOption.setVisibility(View.GONE);
                    imExpandOption.setImageResource(R.drawable.right);
                } else {
                    ctOption.setVisibility(View.VISIBLE);
                    imExpandOption.setImageResource(R.drawable.left);
                }
            }
        });

        ctHead.setOnTouchListener(new View.OnTouchListener() {
            float x0 = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() - x0 > 100) {
                            ctOption.setVisibility(View.VISIBLE);
                            imExpandOption.setImageResource(R.drawable.left);
                        } else if (event.getX() - x0 < -100) {
                            ctOption.setVisibility(View.GONE);
                            imExpandOption.setImageResource(R.drawable.right);
                        } else {
                            shrinkButtonSearch();
                        }
                        break;
                }
                return true;
            }
        });

        imExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Exporting...")
                            .setNegativeButton("Close", null)
                            .show();
                    File dir = getExternalFilesDir(null);
                    File file = new File(dir.getAbsolutePath(), fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        FileOutputStream stream = new FileOutputStream(file);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
                        for (ItemData itemData : listData) {
                            objectOutputStream.writeObject(itemData);
                        }
                        objectOutputStream.close();
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    alertDialog.cancel();
                }
            }
        });

        imImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Importing...")
                            .setNegativeButton("Close", null)
                            .show();
                    File dir = getExternalFilesDir(null);
                    File file = new File(dir.getAbsolutePath(), fileName);
                    ArrayList<ItemData> list = new ArrayList<>();
                    try {
                        FileInputStream stream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
                        while (true) {
                            ItemData itemData = (ItemData) objectInputStream.readObject();
                            if (itemData != null) {
                                list.add(itemData);
                            } else {
                                break;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        alertDialog.cancel();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Please move the file english.en to the download folder!")
                                .setNegativeButton("Close", null)
                                .show();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (list.size() > 0) {
                        for (int i = list.size() - 1; i >= 0; i--) {
                            boolean added = database.addData(list.get(i));
                            if (added){
                                ItemData item = database.getNewItem();
                                listData.add(item);
                            }
                        }
                        sortList(listData);
                        reloadList();
                    }
                    alertDialog.cancel();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void sortList(ArrayList<ItemData> list) {
        list.sort(new Comparator<ItemData>() {
            @Override
            public int compare(ItemData o1, ItemData o2) {
                return o1.id <= o2.id ? 1 : -1;
            }
        });
    }

    public void showAlertDialog(Context context, String message) {
        alertDialog = null;
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(message)
                .setNegativeButton("Close", null)
                .show();
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
        swAutoNotify = findViewById(R.id.sw_auto_notify);
        imExpandOption = findViewById(R.id.im_expand_option);
        ctOption = findViewById(R.id.ct_option);
        ctHead = findViewById(R.id.ct_header_title);
        imExport = findViewById(R.id.im_export);
        imImport = findViewById(R.id.im_import);
    }

    public void setAutoNotify() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BotReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (6 * 60 * 60 * 1000), 6 * 60 * 60 * 1000, pendingIntent);
    }

    public void destroyAutoNotify() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BotReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, -1, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void setRepeatAlarm(ItemData itemData) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("english", itemData.english);
        bundle.putString("vietnamese", itemData.vietnamese);
        bundle.putInt("id", itemData.id);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, itemData.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (60 * 60 * 1000), 60 * 60 * 1000, pendingIntent);
    }

    public void destroyRepeatAlarm(ItemData itemData) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, itemData.id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}