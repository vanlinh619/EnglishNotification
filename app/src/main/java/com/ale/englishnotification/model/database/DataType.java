package com.ale.englishnotification.model.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ale.englishnotification.model.Type;

import java.util.ArrayList;

public class DataType {

    public static final String TABLE_TYPE = "type";
    public static final String TYPE_ID = "id";
    public static final String TYPE_NAME = "name";

    public static void createTable(SQLiteDatabase db){
        String createTableType =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT)",
                        TABLE_TYPE, TYPE_ID, TYPE_NAME);
        db.execSQL(createTableType);
    }

    public static void addNewType(Type type, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, ?);", TABLE_TYPE);

        db.execSQL(query, new String[]{type.name});
        db.close();
    }

    public static boolean typeExist(Type type, Database database){
        SQLiteDatabase db = database.getReadableDatabase();

        String getType = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_TYPE, TYPE_NAME);
        Cursor cursor = db.rawQuery(getType, new String[]{type.name});
        if(cursor.moveToNext()) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public static Type getNewType(Database database){
        SQLiteDatabase db = database.getReadableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_TYPE, TYPE_ID);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        Type type = new Type(cursor.getInt(0), cursor.getString(1));
        db.close();
        return type;
    }

    public static ArrayList<Type> getAll(Database database){
        SQLiteDatabase db = database.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TYPE;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Type> types = new ArrayList<>();
        while (cursor.moveToNext()){
            Type type = new Type(cursor.getInt(0), cursor.getString(1));
            types.add(type);
        }
        db.close();
        return types;
    }

    public static void updateType(Type type, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_TYPE, TYPE_NAME, TYPE_ID);
        db.execSQL(query, new String[]{type.name, type.id + ""});
        db.close();
    }

    public static void deleteType(int id, Database database) {
        SQLiteDatabase db = database.getWritableDatabase();

        String query = String.format("DELETE FROM %s WHERE %s = %s", TABLE_TYPE, TYPE_ID, id);

        db.execSQL(query);
        db.close();
    }

    public static boolean foreignExist(int typeId, Database database){
        SQLiteDatabase db = database.getWritableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s = %s", DataMean.TABLE_MEAN, DataMean.MEAN_TYPE_ID, typeId);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToNext()) {
            return true;
        }
        return false;
    }
}
