package com.example.englishnotification.model.database;

import android.database.sqlite.SQLiteDatabase;

import com.example.englishnotification.model.Mean;

import java.util.ArrayList;

public class DataMean {

    private static final String TABLE_MEAN = "mean";
    private static final String MEAN_ID = "id";
    private static final String MEAN_TYPE = "mean_type";
    private static final String MEAN_MEAN = "mean_word";

    public static void createTable(SQLiteDatabase db){
        String createTableMean =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s TEXT)",
                        TABLE_MEAN, MEAN_ID, MEAN_TYPE, MEAN_MEAN);

        db.execSQL(createTableMean);
    }

    public static void addMeans(ArrayList<Mean> means, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        StringBuffer sqlObject = new StringBuffer();
        ArrayList<String> paramsObject = new ArrayList<String>();
        sqlObject.append("INSERT INTO mean VALUES");
        for (int i = 0; i < means.size(); i++){
            Mean mean = means.get(i);
            if(i == 0){
                sqlObject.append("  (NULL, %s, '%s')");
            } else {
                sqlObject.append("  ,(NULL, %s, '%s')");
            }
            paramsObject.add(mean.type.id + "");
            paramsObject.add(mean.meanWord);
        }

        String query = String.format(sqlObject.toString(), paramsObject.toArray());

        db.execSQL(query);
        db.close();
    }

}
