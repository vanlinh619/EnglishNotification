package com.example.englishnotification;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

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
                                "%s INTEGER)",
                        TABLE_WORD, WORD_ID, WORD_DATE, WORD_ENGLISH, WORD_VIETNAMESE, WORD_NOTIFICATION, WORD_AUTO);

        db.execSQL(createTableProject);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = String.format("DROP TABLE IF EXISTS %s", TABLE_WORD);
        db.execSQL(dropTable);

        onCreate(db);
    }

    public void addData(ItemData data){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("INSERT INTO %s VALUES(null, '%s', '%s', '%s', %s, %s);",
                TABLE_WORD, data.date, data.english, data.vietnamese, data.notification, data.auto);

        db.execSQL(query);
        db.close();
    }

    public void updateData(ItemData data){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = '%s', %s = '%s', %s = '%s', %s = %s, %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_DATE, data.date, WORD_ENGLISH, data.english, WORD_VIETNAMESE, data.vietnamese,
                WORD_NOTIFICATION, data.notification, WORD_AUTO, data.auto, WORD_ID, data.id);

        db.execSQL(query);
        db.close();
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
                    cursor.getInt(5));
            listData.add(itemData);
        }
        db.close();
        listData.sort(new Comparator<ItemData>() {
            @Override
            public int compare(ItemData o1, ItemData o2) {
                return o1.id <= o2.id ? 1 : -1;
            }
        });
        return listData;
    }

    public ArrayList<ItemData> getDataForNotification(){
        SQLiteDatabase db = getReadableDatabase();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(new Date());
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE_WORD, WORD_DATE, date);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ItemData> listData = new ArrayList<>();
        while (cursor.moveToNext()){
            ItemData itemData = new ItemData(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getInt(4),
                    cursor.getInt(5));
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
                cursor.getInt(5));
        db.close();
        return itemData;
    }
}
