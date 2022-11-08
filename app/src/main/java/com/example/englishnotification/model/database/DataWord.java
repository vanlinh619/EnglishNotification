package com.example.englishnotification.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.englishnotification.MainActivity;
import com.example.englishnotification.model.Word;

import java.util.ArrayList;

public class DataWord {
    public static final String TABLE_WORD = "word";
    public static final String WORD_ID = "id";
    public static final String WORD_DATE = "date";
    public static final String WORD_ENGLISH = "english";
    public static final String WORD_NOTIFICATION = "notification";
    public static final String WORD_AUTO = "auto";
    public static final String WORD_GAME = "game";
    public static final String WORD_FORGET = "forget";

    public static void createTable(SQLiteDatabase db){
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

        db.execSQL(createTableProject);
    }

    public static void updateOffNotify(int id, Database database){
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_NOTIFICATION, 0, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    public static void incrementOnceForget(Word word, Database database){
        SQLiteDatabase db = database.getWritableDatabase();

//        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE_WORD, WORD_ID, id);
//        Cursor cursor = db.rawQuery(query, null);
//        if(cursor.moveToNext()){
//            Word word = new Word(cursor.getInt(0), cursor.getString(1),
//                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
//                    cursor.getInt(5), cursor.getInt(6));
//
//
//        }

        word.forget++;
        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_FORGET, word.forget, WORD_ID, word.id);
        db.execSQL(query);

        db.close();
    }

    public static void updateWord(Word word, Database database){
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, " +
                        "%s = ?, %s = ? WHERE %s = ?", TABLE_WORD, WORD_DATE, WORD_ENGLISH, WORD_NOTIFICATION,
                WORD_AUTO, WORD_GAME, WORD_FORGET, WORD_ID);

        db.execSQL(query, new String[]{word.date, word.english, word.notification + "", word.auto + "",
                word.game + "", word.forget + "", word.id + ""});
        db.close();
    }

    public static boolean addNewWord(Word word, Database database){
        if(getItemEnglish(word.english, database) != null) {
            return false;
        }

        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("INSERT INTO %s VALUES(null, ?, ?, ?, ?, ?, ?);", TABLE_WORD);
        ArrayList<String> params = new ArrayList<>();
        params.add(word.date);
        params.add(word.english);
        params.add(word.notification + "");
        params.add(word.auto + "");
        params.add(word.game + "");
        params.add(word.forget + "");

        db.execSQL(query, params.toArray());
        db.close();
        return true;
    }

    public static Word getItemEnglish(String english, Database database){
        SQLiteDatabase db = database.getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_WORD, WORD_ENGLISH);
        Cursor cursor = db.rawQuery(query, new String[]{english});
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

    public static void updateOffBotNotify(int id, Database database) {
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
                TABLE_WORD, WORD_AUTO, 0, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    public static void deleteData(int id, Database database) {
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_WORD, WORD_ID, id);

        db.execSQL(query);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Word> getAll(Database database) {
        SQLiteDatabase db = database.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_WORD;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Word> listWord = new ArrayList<>();
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6));
            listWord.add(word);
        }
        db.close();
        MainActivity.sortList(listWord);
        return listWord;
    }

    public static ArrayList<Word> getDataForNotification(Database database) {
        SQLiteDatabase db = database.getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s = %s", TABLE_WORD, WORD_AUTO, 1);
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Word> listWord = new ArrayList<>();
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6));
            listWord.add(word);
        }
        db.close();
        return listWord;
    }

    public static Word getNewWord(Database database) {
        SQLiteDatabase db = database.getReadableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_WORD, WORD_ID);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        Word word = new Word(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                cursor.getInt(5), cursor.getInt(6));
        db.close();
        return word;
    }

    public static Word getWordByEnglish(String english, Database database) {
        SQLiteDatabase db = database.getReadableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TABLE_WORD, WORD_ENGLISH, english);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToNext()){
            Word word = new Word(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getInt(3), cursor.getInt(4),
                    cursor.getInt(5), cursor.getInt(6));
            db.close();
            return word;
        }

        db.close();
        return null;
    }
}
