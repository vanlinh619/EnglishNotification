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

    public static void addNewTag(Tag tag, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, '%s');",
                TABLE_TAG, tag.name);

        db.execSQL(query);
        db.close();
    }

    public static Tag getNewTag(Database database){
        SQLiteDatabase db = database.getReadableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_TAG, TAG_ID);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        Tag tag = new Tag(cursor.getInt(0), cursor.getString(1));
        db.close();
        return tag;
    }

    public static void updateTag(Tag tag, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("UPDATE %s SET %s = '%s' WHERE %s = %s", TABLE_TAG, TAG_NAME,
                tag.name, TAG_ID, tag.id);
        db.execSQL(query);
        db.close();
    }

    public static boolean foreignExist(int tagId, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = %s", DataTagWord.TABLE_TAG_WORD, DataTagWord.TAG_WORD_TAG_ID, tagId);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public static void deleteTag(int id, Database database) {
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_TAG, TAG_ID, id);

        db.execSQL(query);
        db.close();
    }
}
