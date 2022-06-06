package com.example.englishnotification.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.TagWord;

import java.util.ArrayList;

public class DataTagWord {
    public static final String TABLE_TAG_WORD = "tag_word";
    public static final String TAG_WORD_ID = "id";
    public static final String TAG_WORD_TAG_ID = "tag_id";
    public static final String TAG_WORD_WORD_ID = "word_id";

    public static void createTable(SQLiteDatabase db){
        String createTableTagWord =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_TAG_WORD, TAG_WORD_ID, TAG_WORD_TAG_ID, TAG_WORD_WORD_ID);

        db.execSQL(createTableTagWord);
    }

    public static ArrayList<TagWord> getAll(Database database){
            SQLiteDatabase db = database.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_TAG_WORD;
            Cursor cursor = db.rawQuery(query, null);

            ArrayList<TagWord> tagWords = new ArrayList<>();
            while (cursor.moveToNext()){
                TagWord tagWord = new TagWord(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
                tagWords.add(tagWord);
            }
            db.close();
            return tagWords;
    }

    public static void addTags(int wordId, ArrayList<Tag> tags, Database database){
        if(tags.size() >= 1){
            SQLiteDatabase db = database.getWritableDatabase();
            StringBuffer sqlObject = new StringBuffer();
            ArrayList<String> paramsObject = new ArrayList<>();
            sqlObject.append("INSERT INTO %s VALUES");
            paramsObject.add(TABLE_TAG_WORD);
            for (int i = 0; i < tags.size(); i++){
                Tag tag = tags.get(i);
                if(i == 0){
                    sqlObject.append("  (NULL, %s, %s)");
                } else {
                    sqlObject.append("  ,(NULL, %s, %s)");
                }
                paramsObject.add(tag.id + "");
                paramsObject.add(wordId + "");
            }

            String query = String.format(sqlObject.toString(), paramsObject.toArray());

            db.execSQL(query);
            db.close();
        }
    }

    public static void deleteByWordId(int wordId, Database database) {
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_TAG_WORD, TAG_WORD_WORD_ID, wordId);

        db.execSQL(query);
        db.close();
    }
}
