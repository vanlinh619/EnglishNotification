package com.example.englishnotification.model.database;

import android.database.sqlite.SQLiteDatabase;

public class DataRelationWord {
    public static final String TABLE_WORD_WORD = "word_word";
    public static final String WORD_WORD_ID = "id";
    public static final String WORD_WORD_WORD_ID = "word_id";
    public static final String WORD_WORD_WORD_RELATION_ID = "word_relation_id";
    public static final String WORD_WORD_TYPE_RELATION = "type_relation";

    public static void createTable(SQLiteDatabase db){
        String createTableWordWord =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_WORD_WORD, WORD_WORD_ID, WORD_WORD_WORD_ID, WORD_WORD_WORD_RELATION_ID, WORD_WORD_TYPE_RELATION);

        db.execSQL(createTableWordWord);
    }
}
