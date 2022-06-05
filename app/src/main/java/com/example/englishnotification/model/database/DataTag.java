package com.example.englishnotification.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Type;

import java.util.ArrayList;

public class DataTag {
    private static final String TABLE_TAG = "tag";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

    public static void createTableTag(SQLiteDatabase db){
        String createTableTag =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT)",
                        TABLE_TAG, TAG_ID, TAG_NAME);

        db.execSQL(createTableTag);
    }

    public static ArrayList<Tag> getAll(Database database){
        SQLiteDatabase db = database.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TAG;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Tag> tags = new ArrayList<>();
        while (cursor.moveToNext()){
            Tag tag = new Tag(cursor.getInt(0), cursor.getString(1));
            tags.add(tag);
        }
        db.close();
        return tags;
    }
}
