package com.example.englishnotification.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.englishnotification.MainActivity;
import com.example.englishnotification.model.Config;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.Word;

import java.io.Serializable;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper implements Serializable {

    private static final String DATABASE_NAME = "englishNotification.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_WORD = "word";
    private static final String WORD_ID = "id";
    private static final String WORD_DATE = "date";
    private static final String WORD_ENGLISH = "english";
    private static final String WORD_NOTIFICATION = "notification";
    private static final String WORD_AUTO = "auto";
    private static final String WORD_GAME = "game";
    private static final String WORD_FORGET = "forget";

    private static final String TABLE_CONFIG = "config";
    private static final String CONFIG_ID = "id";
    private static final String CONFIG_AUTO_NOTIFY = "auto_notify";

    private static final String TABLE_TAG = "tag";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

    private static final String TABLE_TAG_WORD = "tag_word";
    private static final String TAG_WORD_ID = "id";
    private static final String TAG_WORD_TAG_ID = "tag_id";
    private static final String TAG_WORD_WORD_ID = "word_id";

    private static final String TABLE_WORD_WORD = "word_word";
    private static final String WORD_WORD_ID = "id";
    private static final String WORD_WORD_WORD_ID = "word_id";
    private static final String WORD_WORD_WORD_RELATION_ID = "word_relation_id";
    private static final String WORD_WORD_TYPE_RELATION = "type_relation";

    private static final String TABLE_MEAN = "mean";
    private static final String MEAN_ID = "id";
    private static final String MEAN_TYPE = "mean_type";
    private static final String MEAN_MEAN = "mean_word";

    private static final String TABLE_MEAN_WORD = "mean_word";
    private static final String MEAN_WORD_ID = "id";
    private static final String MEAN_WORD_TYPE_ID = "type_id";
    private static final String MEAN_WORD_WORD_ID = "word_id";

    public DataType dataType;

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
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_WORD, WORD_ID, WORD_DATE, WORD_ENGLISH, WORD_NOTIFICATION, WORD_AUTO,
                        WORD_GAME, WORD_FORGET);

        String createTableConfig =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER)",
                        TABLE_CONFIG, CONFIG_ID, CONFIG_AUTO_NOTIFY);

        String createTableTag =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT)",
                        TABLE_TAG, TAG_ID, TAG_NAME);



        String createTableTagWord =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_TAG_WORD, TAG_WORD_ID, TAG_WORD_TAG_ID, TAG_WORD_WORD_ID);

        String createTableMean =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s TEXT)",
                        TABLE_MEAN, MEAN_ID, MEAN_TYPE, MEAN_MEAN);

        String createTableMeanWord =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_MEAN_WORD, MEAN_WORD_ID, MEAN_WORD_TYPE_ID, MEAN_WORD_WORD_ID);

        String createTableWordWord =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_WORD_WORD, WORD_WORD_ID, WORD_WORD_WORD_ID, WORD_WORD_WORD_RELATION_ID, WORD_WORD_TYPE_RELATION);

        dataType.createTable(db);

        db.execSQL(createTableProject);
        db.execSQL(createTableConfig);
        db.execSQL(createTableTag);
        db.execSQL(createTableTagWord);
        db.execSQL(createTableMean);
        db.execSQL(createTableMeanWord);
        db.execSQL(createTableWordWord);
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

    public boolean addNewWord(Word data){
        if(getItemEnglish(data.english) != null) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("INSERT INTO %s VALUES(null, '%s', '%s', %s, %s, %s, %s);",
                TABLE_WORD, data.date, data.english, data.notification, data.auto, data.game,
                data.forget);

        db.execSQL(query);
        db.close();
        return true;
    }

    public void updateWord(Word data){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = '%s', %s = '%s', %s = %s, %s = %s, " +
                        "%s = %s, %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_DATE, data.date, WORD_ENGLISH, data.english, WORD_NOTIFICATION, data.notification,
                WORD_AUTO, data.auto, WORD_GAME, data.game, WORD_FORGET, data.forget, WORD_ID, data.id);

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

    public Word getItemEnglish(String english){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE_WORD, WORD_ENGLISH, english);
        Cursor cursor = db.rawQuery(query, null);
        Word word = null;
        while (cursor.moveToNext()){
            word = new Word(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6));
            break;
        }
        db.close();
        return word;
    }

    public void deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_WORD, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Word> getAll(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_WORD;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Word> listData = new ArrayList<>();
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6));
            listData.add(word);
        }
        db.close();
        MainActivity.sortList(listData);
        return listData;
    }

    public ArrayList<Word> getDataForNotification(){
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = %s", TABLE_WORD, WORD_AUTO, 1);
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Word> listData = new ArrayList<>();
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6));
            listData.add(word);
        }
        db.close();
        return listData;
    }

    public Word getNewWord(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_WORD, WORD_ID);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        Word word = new Word(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                cursor.getInt(5), cursor.getInt(6));
        db.close();
        return word;
    }

    public void addNewType(Type type) {
        dataType.addNewType(type, this);
    }

    public ArrayList<Type> getAllType(){
        return dataType.getAll(this);
    }
}
