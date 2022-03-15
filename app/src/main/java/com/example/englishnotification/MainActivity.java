package com.example.englishnotification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Serializable {

    public static final int ENGLISH_ITEM = 0;
    public static final int NOTIFY_ITEM = 1;
    public static final int BOT_ITEM = 2;
    public static final int FILTER_1 = 3;
    public static final int FILTER_2 = 4;
    public static final int FILTER_3 = 5;
    public static final int ADD_WORD = 6;
    public static final int TRANSLATE = 7;
    public static final int GAME = 8;
    public static final int UPDATE = 9;
    private int englishSort = FILTER_1;
    private int notifyFilter = FILTER_1;
    private int botFilter = FILTER_1;
    private ItemListAdapter adapter;
    private RecyclerView rcListWord;
    private ImageView imAdd, imSearch, imExpandOption, imImport, imExport, imTranslate, imGame;
    private EditText edSearch;
    private TextView txTitle, txEnglishSort, txNotifyFilter, txBotFilter;
    private ConstraintLayout ctOption, ctHead, ctHeaderButton;
    private Switch swAutoNotify;
    private SwipeRefreshLayout srRefresh;
    private final String fileName = "english.en";
    private final String dirName = "English";
    public Database database;
    public ArrayList<ItemData> listData, listTmp;
    public TextToSpeech textToSpeechEnglish;
    public TextToSpeech textToSpeechVietnamese;
    public Translator translatorEnglish, translatorVietnamese;
    public Config config;
    private static AlertDialog alertDialog;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        setView();

        // Create an English-German translator:
        TranslatorOptions optionsEnglish =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build();
        TranslatorOptions optionsVietnamese =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();

        translatorEnglish = Translation.getClient(optionsEnglish);
        translatorVietnamese = Translation.getClient(optionsVietnamese);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translatorEnglish.downloadModelIfNeeded(conditions)
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

        translatorVietnamese.downloadModelIfNeeded(conditions)
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
        listTmp = new ArrayList<>();

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
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(MainActivity.this, null, ADD_WORD);
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
                } else {
                    ctOption.setVisibility(View.VISIBLE);
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
                            txTitle.setText("");
                        } else if (event.getX() - x0 < -100) {
                            ctOption.setVisibility(View.GONE);
                            txTitle.setText("English Notification");
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
                    showAlertDialog(MainActivity.this, "Exporting...");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog(MainActivity.this, "Exported!");
                                }
                            });
                        }

                    });
                    thread.start();
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
                    showAlertDialog(MainActivity.this, "Importing...");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
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
                                    if (added) {
                                        ItemData item = database.getNewItem();
                                        listData.add(item);
                                    }
                                }
                                sortList(listData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reloadList();
                                    }
                                });
                            }
                            alertDialog.cancel();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog(MainActivity.this, "Imported!");
                                }
                            });
                        }
                    });
                    thread.start();
                }
            }
        });

        txEnglishSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewItemFilter(ENGLISH_ITEM);
            }
        });

        txNotifyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewItemFilter(NOTIFY_ITEM);
            }
        });

        txBotFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewItemFilter(BOT_ITEM);
            }
        });

        imTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(MainActivity.this, null, TRANSLATE);
                dialogAddEditWord.show(fm, "fragment_edit_name");
            }
        });

        imGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCheckWord();
            }
        });

        srRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listData = database.getAll();
                reloadList();
                srRefresh.setRefreshing(false);
            }
        });

        edSearch.setOnTouchListener(deleteText());
    }

    public void showDialogCheckWord(){
        Random random = new Random();
        int index = random.nextInt(listData.size());
        ItemData itemData = listData.get(index);
        FragmentManager fm = getSupportFragmentManager();
        DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(MainActivity.this, itemData, GAME);
        dialogAddEditWord.show(fm, "fragment_edit_name");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    public void changeViewItemFilter(int flags) {
        if (flags == ENGLISH_ITEM) {
            switch (englishSort) {
                case FILTER_1:
                    notifyFilter = FILTER_1;
                    txNotifyFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txNotifyFilter.setTextColor(R.color.black);
                    botFilter = FILTER_1;
                    txBotFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txBotFilter.setTextColor(R.color.black);
                    englishSort = FILTER_2;
                    txEnglishSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0);
                    txEnglishSort.setTextColor(R.color.blue);
                    refreshListData();
                    sortByName(listData, 0);
                    reloadListFilter();
                    break;
                case FILTER_2:
                    englishSort = FILTER_3;
                    txEnglishSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0);
                    sortByName(listData, 1);
                    reloadListFilter();
                    break;
                case FILTER_3:
                    englishSort = FILTER_1;
                    txEnglishSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txEnglishSort.setTextColor(R.color.black);
                    sortList(listData);
                    reloadListFilter();
                    break;
            }
        } else if (flags == NOTIFY_ITEM) {
            switch (notifyFilter) {
                case FILTER_1:
                    englishSort = FILTER_1;
                    txEnglishSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txEnglishSort.setTextColor(R.color.black);
                    botFilter = FILTER_1;
                    txBotFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txBotFilter.setTextColor(R.color.black);
                    notifyFilter = FILTER_2;
                    txNotifyFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_dot_blue, 0);
                    txNotifyFilter.setTextColor(R.color.blue);
                    refreshListData();
                    filterByNotify(1);
                    reloadListFilter();
                    break;
                case FILTER_2:
                    notifyFilter = FILTER_3;
                    txNotifyFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_dot, 0);
                    txNotifyFilter.setTextColor(R.color.black);
                    refreshListData();
                    filterByNotify(0);
                    reloadListFilter();
                    break;
                case FILTER_3:
                    notifyFilter = FILTER_1;
                    txNotifyFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    refreshListData();
                    reloadListFilter();
                    break;
            }
        } else {
            switch (botFilter) {
                case FILTER_1:
                    englishSort = FILTER_1;
                    txEnglishSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txEnglishSort.setTextColor(R.color.black);
                    notifyFilter = FILTER_1;
                    txNotifyFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    txNotifyFilter.setTextColor(R.color.black);
                    botFilter = FILTER_2;
                    txBotFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_dot_blue, 0);
                    txBotFilter.setTextColor(R.color.blue);
                    refreshListData();
                    filterByBot(1);
                    reloadListFilter();
                    break;
                case FILTER_2:
                    botFilter = FILTER_3;
                    txBotFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_dot, 0);
                    txBotFilter.setTextColor(R.color.black);
                    refreshListData();
                    filterByBot(0);
                    reloadListFilter();
                    break;
                case FILTER_3:
                    botFilter = FILTER_1;
                    txBotFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    refreshListData();
                    reloadListFilter();
                    break;
            }
        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByName(ArrayList<ItemData> list, int flags) {
        list.sort(new Comparator<ItemData>() {
            @Override
            public int compare(ItemData o1, ItemData o2) {
                if (flags == 0) {
                    return o1.english.toLowerCase().compareTo(o2.english.toLowerCase());
                } else {
                    return o2.english.toLowerCase().compareTo(o1.english.toLowerCase());
                }
            }
        });
    }

    public void filterByNotify(int flags) {
        for (int i = listData.size() - 1; i >= 0; i--) {
            ItemData itemData = listData.get(i);
            if (itemData.notification != flags) {
                listTmp.add(itemData);
                listData.remove(i);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshListData() {
        if (listTmp != null && listTmp.size() > 0) {
            listData.addAll(listTmp);
            listTmp.clear();
            sortList(listData);
        }
    }

    public void filterByBot(int flags) {
        for (int i = listData.size() - 1; i >= 0; i--) {
            ItemData itemData = listData.get(i);
            if (itemData.auto != flags) {
                listTmp.add(itemData);
                listData.remove(i);
            }
        }
    }

    public void showAlertDialog(Context context, String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setTitle(message)
                .setNegativeButton("Close", null)
                .show();
    }

    public void expandButtonSearch() {
//        imSearch.setVisibility(View.GONE);
//        txTitle.setVisibility(View.GONE);
//        imAdd.setVisibility(View.GONE);
//        imTranslate.setVisibility(View.GONE);
        Animation animExpandLeftToRight = AnimationUtils.loadAnimation(this, R.anim.anim_expand_left_to_right);
        Animation animShinkLeftToRight = AnimationUtils.loadAnimation(this, R.anim.anim_shink_left_to_right);
        edSearch.setAnimation(animExpandLeftToRight);
        ctHeaderButton.setAnimation(animShinkLeftToRight);
        edSearch.setVisibility(View.VISIBLE);
        ctHeaderButton.setVisibility(View.GONE);
    }

    public void shrinkButtonSearch() {
//        imSearch.setVisibility(View.VISIBLE);
//        txTitle.setVisibility(View.VISIBLE);
//        imAdd.setVisibility(View.VISIBLE);
//        imTranslate.setVisibility(View.VISIBLE);
        hideKeyboard(this, edSearch);
        edSearch.setText("");
        Animation animShinkRightToLeft = AnimationUtils.loadAnimation(this, R.anim.anim_shink_right_to_left);
        Animation animExpandRightToLeft = AnimationUtils.loadAnimation(this, R.anim.anim_expand_right_to_left);
        edSearch.setAnimation(animShinkRightToLeft);
        ctHeaderButton.setAnimation(animExpandRightToLeft);
        edSearch.setVisibility(View.GONE);
        ctHeaderButton.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void updateViewNotify(int id, int flags) {
        for (ItemData itemData : listData) {
            if (id == itemData.id) {
                if (flags == Notification.PERSON_SETUP_NOTIFY) {
                    itemData.notification = 0;
                } else {
                    itemData.auto = 0;
                }
                reloadList();
                break;
            }
        }
    }

    public void reloadList() {
        adapter = new ItemListAdapter(listData, MainActivity.this);
        rcListWord.setAdapter(adapter);
        shrinkButtonSearch();
    }

    public void reloadListFilter() {
        adapter.notifyDataSetChanged();
        shrinkButtonSearch();
    }

    private void setView() {
        rcListWord = findViewById(R.id.rc_list_item);
        imAdd = findViewById(R.id.im_add_new_word);
        edSearch = findViewById(R.id.ed_search);
        txTitle = findViewById(R.id.tx_title);
        imSearch = findViewById(R.id.im_search);
        swAutoNotify = findViewById(R.id.sw_auto_notify);
        imExpandOption = findViewById(R.id.im_expand_option);
        ctOption = findViewById(R.id.ct_option);
        ctHead = findViewById(R.id.ct_header_title);
        imExport = findViewById(R.id.im_export);
        imImport = findViewById(R.id.im_import);
        txEnglishSort = findViewById(R.id.tx_english_sort);
        txNotifyFilter = findViewById(R.id.tx_notify_filter);
        txBotFilter = findViewById(R.id.tx_bot_filter);
        imTranslate = findViewById(R.id.im_translate_word);
        imGame = findViewById(R.id.im_game);
        srRefresh = findViewById(R.id.sr_refresh);
        ctHeaderButton = findViewById(R.id.ct_header_button);
    }

    public void setAutoNotify() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, BotReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (2 * 60 * 60 * 1000), 2 * 60 * 60 * 1000, pendingIntent);
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
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (2 * 60 * 60 * 1000), 2 * 60 * 60 * 1000, pendingIntent);
    }

    public void destroyRepeatAlarm(ItemData itemData) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, itemData.id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void setTextForSearch(String text) {
        edSearch.setText(text);
    }

    public void speak(String english, String vietnamese){
        textToSpeechEnglish = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEnglish.setLanguage(Locale.ENGLISH);
                    textToSpeechEnglish.speak(english, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });

        textToSpeechVietnamese = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechVietnamese.speak(vietnamese, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });
    }

    public View.OnTouchListener deleteText(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                EditText editText = (EditText) v;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        shrinkButtonSearch();
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public void onBackPressed() {
        if(edSearch.getVisibility() == View.VISIBLE){
            shrinkButtonSearch();
        } else {
            super.onBackPressed();
        }
    }
}