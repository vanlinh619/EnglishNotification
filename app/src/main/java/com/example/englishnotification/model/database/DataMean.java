package com.example.englishnotification.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishnotification.MainActivity;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.UtilContent;
import com.example.englishnotification.model.Word;

import java.util.ArrayList;

public class DataMean {

    public static final String TABLE_MEAN = "mean";
    public static final String MEAN_ID = "id";
    public static final String MEAN_TYPE_ID = "type_id";
    public static final String MEAN_MEAN_WORD = "mean_word";
    public static final String MEAN_WORD_ID = "word_id";

    public static void createTable(SQLiteDatabase db){
        String createTableMean =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s TEXT, " +
                                "%s INTEGER)",
                        TABLE_MEAN, MEAN_ID, MEAN_TYPE_ID, MEAN_MEAN_WORD, MEAN_WORD_ID);

        db.execSQL(createTableMean);
    }

    public static void addMeans(ArrayList<Mean> means, Database database){
        if(means.size() >= 1){
            SQLiteDatabase db = database.getWritableDatabase();
            StringBuffer sqlObject = new StringBuffer();
            ArrayList<String> paramsObject = new ArrayList<String>();
            sqlObject.append("INSERT INTO mean VALUES");
            for (int i = 0; i < means.size(); i++){
                Mean mean = means.get(i);
                if(i == 0){
                    sqlObject.append("  (NULL, %s, '%s', %s)");
                } else {
                    sqlObject.append("  ,(NULL, %s, '%s', %s)");
                }
                paramsObject.add(mean.type.id + "");
                paramsObject.add(mean.meanWord);
                paramsObject.add(mean.wordId + "");
            }

            String query = String.format(sqlObject.toString(), paramsObject.toArray());

            db.execSQL(query);
            db.close();
        }
    }

    public static ArrayList<Mean> getAll(Database database){
        SQLiteDatabase db = database.getReadableDatabase();

        String query = String.format("SELECT mean.id, mean.type_id, mean.mean_word, mean.word_id, type.name " +
                "FROM mean LEFT JOIN type ON mean.type_id = type.id");
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Mean> means = new ArrayList<>();
        while (cursor.moveToNext()){
            Mean mean = new Mean(cursor.getInt(0), new Type(cursor.getInt(1), cursor.getString(4)),
                    cursor.getString(2), cursor.getInt(3));
            means.add(mean);
        }
        db.close();
        return means;
    }

    public static void deleteMeans(int wordId, Database database){
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_MEAN, MEAN_WORD_ID, wordId);

        db.execSQL(query);
        db.close();
    }

}
