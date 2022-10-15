package com.example.englishnotification.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishnotification.model.RelationWord;
import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Word;

import java.util.ArrayList;

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

    public static void addRelationWord(Word word, Word relationWord, int type, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, %s, %s, %s);",
                TABLE_WORD_WORD, word.id, relationWord.id, type);

        db.execSQL(query);
        db.close();
    }

    public static void addRelationWord(RelationWord relationWord, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, %s, %s, %s);",
                TABLE_WORD_WORD, relationWord.wordId, relationWord.relationWordId, relationWord.relationType);

        db.execSQL(query);
        db.close();
    }

    public static RelationWord getNewRelationWord(Database database){
        SQLiteDatabase db = database.getReadableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_WORD_WORD, WORD_WORD_ID);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        RelationWord relationWord = new RelationWord(cursor.getInt(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getInt(3));
        db.close();
        return relationWord;
    }

    public static boolean existRelationWord(Word word, Word relWord, RelationWord relationWord, Database database){
        SQLiteDatabase db = database.getReadableDatabase();
        String query = String.format("SELECT * " +
                        "FROM %s ww " +
                        "LEFT JOIN %s w ON ww.%s = w.%s " +
                        "LEFT JOIN %s wr ON ww.%s = wr.%s " +
                        "WHERE " +
                        "((w.%s = '%s' AND wr.%s = '%s') " +
                        "OR (w.%s = '%s' AND wr.%s = '%s')) " +
                        "AND ww.%s = %s",
                TABLE_WORD_WORD, DataWord.TABLE_WORD, WORD_WORD_WORD_ID, DataWord.WORD_ID,
                DataWord.TABLE_WORD, WORD_WORD_WORD_RELATION_ID, DataWord.WORD_ID, DataWord.WORD_ENGLISH,
                word.english, DataWord.WORD_ENGLISH, relWord.english, DataWord.WORD_ENGLISH,
                relWord.english, DataWord.WORD_ENGLISH, word.english, WORD_WORD_TYPE_RELATION, relationWord.relationType);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToNext()){
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public static ArrayList<RelationWord> getAll(Database database){
        SQLiteDatabase db = database.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_WORD_WORD;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<RelationWord> relationWords = new ArrayList<>();
        while (cursor.moveToNext()){
            RelationWord relationWord = new RelationWord(cursor.getInt(0), cursor.getInt(1),
                    cursor.getInt(2), cursor.getInt(3));
            relationWords.add(relationWord);
        }
        db.close();
        return relationWords;
    }

    public static void deleteRelation(int id, Database database) {
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_WORD_WORD, WORD_WORD_ID, id);

        db.execSQL(query);
        db.close();
    }
}
