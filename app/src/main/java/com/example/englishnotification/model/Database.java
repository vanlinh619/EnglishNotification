package com.example.englishnotification.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.englishnotification.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper implements Serializable {

    private static final String DATABASE_NAME = "englishNotification.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_WORD = "word";
    private static final String WORD_ID = "id";
    private static final String WORD_DATE = "date";
    private static final String WORD_ENGLISH = "english";
    private static final String WORD_VIETNAMESE = "vietnamese";
    private static final String WORD_NOTIFICATION = "notification";
    private static final String WORD_AUTO = "auto";
    private static final String WORD_GAME = "game";
    private static final String WORD_TAGS = "tags";
    private static final String WORD_TYPES = "types";
    private static final String WORD_RELATED_WORDS = "relatedWords";
    private static final String WORD_SYNONYMS = "synonyms";
    private static final String WORD_ANTONYMS = "antonyms";
    private static final String WORD_FORGET = "forget";

    private static final String TABLE_CONFIG = "config";
    private static final String CONFIG_ID = "id";
    private static final String CONFIG_AUTO_NOTIFY = "autonotify";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableProject =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s TEXT, " +
                                "%s INTEGER)",
                        TABLE_WORD, WORD_ID, WORD_DATE, WORD_ENGLISH, WORD_VIETNAMESE, WORD_NOTIFICATION, WORD_AUTO,
                        WORD_GAME, WORD_TAGS, WORD_TYPES, WORD_RELATED_WORDS, WORD_SYNONYMS, WORD_ANTONYMS, WORD_FORGET);

        String createTableConfig =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER)",
                        TABLE_CONFIG, CONFIG_ID, CONFIG_AUTO_NOTIFY);

        db.execSQL(createTableProject);
        db.execSQL(createTableConfig);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = String.format("DROP TABLE IF EXISTS %s", TABLE_WORD);
        String dropTableConfig = String.format("DROP TABLE IF EXISTS %s", TABLE_CONFIG);
        db.execSQL(dropTable);
        db.execSQL(dropTableConfig);

        onCreate(db);
    }

    public Config getConfig(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", TABLE_CONFIG);
        Cursor cursor = db.rawQuery(query, null);
        Config config = null;
        while (cursor.moveToNext()){
            config = new Config(cursor.getInt(0), cursor.getInt(1));
        }
        if(config == null){
            config = new Config(0, 0);
            addDefaultConfig();
        }
        return config;
    }

    private void addDefaultConfig() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, %s)", TABLE_CONFIG, 0);
        db.execSQL(query);
        db.close();
    }

    public void updateConfig(Config config){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s", TABLE_CONFIG,
                CONFIG_AUTO_NOTIFY, config.autoNotify, CONFIG_ID, config.id);
        db.execSQL(query);
        db.close();
    }

    public boolean addData(ItemData data){
        if(getItemEnglish(data.english) != null) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("INSERT INTO %s VALUES(null, '%s', '%s', '%s', %s, %s, %s, '%s', " +
                        "'%s', '%s', '%s', '%s', %s);",
                TABLE_WORD, data.date, data.english, data.vietnamese, data.notification, data.auto, data.game,
                data.tags, data.types, data.relatedWords, data.synonyms, data.antonyms, data.forget);

        db.execSQL(query);
        db.close();
        return true;
    }

    public void updateData(ItemData data){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = '%s', %s = '%s', %s = '%s', %s = %s, %s = %s, %s = %s, " +
                        "%s = '%s', %s = '%s', %s = '%s', %s = '%s', %s = '%s', %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_DATE, data.date, WORD_ENGLISH, data.english, WORD_VIETNAMESE, data.vietnamese,
                WORD_NOTIFICATION, data.notification, WORD_AUTO, data.auto, WORD_GAME, data.game,
                WORD_TAGS, data.tags, WORD_TYPES, data.types, WORD_RELATED_WORDS, data.relatedWords, WORD_SYNONYMS,
                data.synonyms, WORD_ANTONYMS, data.antonyms, WORD_FORGET, data.forget, WORD_ID, data.id);

        db.execSQL(query);
        db.close();
    }

    public void updateOffNotify(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_NOTIFICATION, 0, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    public void updateOffBotNotify(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_AUTO, 0, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    public ItemData getItemEnglish(String english){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE_WORD, WORD_ENGLISH, english);
        Cursor cursor = db.rawQuery(query, null);
        ItemData itemData = null;
        while (cursor.moveToNext()){
            itemData = new ItemData(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9), cursor.getString(10),
                    cursor.getString(11), cursor.getInt(12));
            break;
        }
        db.close();
        return itemData;
    }

    public void deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_WORD, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ItemData> getAll(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_WORD;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ItemData> listData = new ArrayList<>();
        while (cursor.moveToNext()){
            ItemData itemData = new ItemData(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9), cursor.getString(10),
                    cursor.getString(11), cursor.getInt(12));
            listData.add(itemData);
        }
        db.close();
        MainActivity.sortList(listData);
        return listData;
    }

    public ArrayList<ItemData> getDataForNotification(){
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = %s", TABLE_WORD, WORD_AUTO, 1);
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<ItemData> listData = new ArrayList<>();
        while (cursor.moveToNext()){
            ItemData itemData = new ItemData(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9), cursor.getString(10),
                    cursor.getString(11), cursor.getInt(12));
            listData.add(itemData);
        }
        db.close();
        return listData;
    }

    public ItemData getNewItem(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_WORD, WORD_ID);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        ItemData itemData = new ItemData(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                cursor.getInt(5), cursor.getInt(6), cursor.getString(7),
                cursor.getString(8), cursor.getString(9), cursor.getString(10),
                cursor.getString(11), cursor.getInt(12));
        db.close();
        return itemData;
    }
}
