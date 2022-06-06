package com.example.englishnotification.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.englishnotification.MainActivity;
import com.example.englishnotification.model.Config;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.TagWord;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.Word;

import java.io.Serializable;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper implements Serializable {

    public static final String DATABASE_NAME = "englishNotification.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONFIG = "config";
    public static final String CONFIG_ID = "id";
    public static final String CONFIG_AUTO_NOTIFY = "auto_notify";

    public static final String TABLE_WORD_WORD = "word_word";
    public static final String WORD_WORD_ID = "id";
    public static final String WORD_WORD_WORD_ID = "word_id";
    public static final String WORD_WORD_WORD_RELATION_ID = "word_relation_id";
    public static final String WORD_WORD_TYPE_RELATION = "type_relation";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableConfig =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER)",
                        TABLE_CONFIG, CONFIG_ID, CONFIG_AUTO_NOTIFY);

        String createTableWordWord =
                String.format("CREATE TABLE IF NOT EXISTS %s(" +
                                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s INTEGER, " +
                                "%s INTEGER, " +
                                "%s INTEGER)",
                        TABLE_WORD_WORD, WORD_WORD_ID, WORD_WORD_WORD_ID, WORD_WORD_WORD_RELATION_ID, WORD_WORD_TYPE_RELATION);

        DataWord.createTable(db);
        DataType.createTable(db);
        DataTag.createTableTag(db);
        DataMean.createTable(db);
        DataTagWord.createTable(db);

        db.execSQL(createTableConfig);
        db.execSQL(createTableWordWord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String dropTable = String.format("DROP TABLE IF EXISTS %s", TABLE_WORD);
        String dropTableConfig = String.format("DROP TABLE IF EXISTS %s", TABLE_CONFIG);
//        db.execSQL(dropTable);
        db.execSQL(dropTableConfig);

        onCreate(db);
    }

    public Config getConfig(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", TABLE_CONFIG);
        Cursor cursor = db.rawQuery(query, null);
        Config config = null;
        while (cursor.moveToNext()){
            config = new Config(cursor.getInt(0), cursor.getInt(1));
        }
        if(config == null){
            config = new Config(0, 0);
            addDefaultConfig();
        }
        return config;
    }

    private void addDefaultConfig() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, %s)", TABLE_CONFIG, 0);
        db.execSQL(query);
        db.close();
    }

    public void updateConfig(Config config){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s", TABLE_CONFIG,
                CONFIG_AUTO_NOTIFY, config.autoNotify, CONFIG_ID, config.id);
        db.execSQL(query);
        db.close();
    }

    public boolean addNewWord(Word word){
        return DataWord.addNewWord(word, this);
    }

    public void updateWord(Word word){
        DataWord.updateWord(word, this);
    }

    public void updateOffNotify(int id){
        DataWord.updateOffNotify(id, this);
    }

    public void updateOffBotNotify(int id){
        DataWord.updateOffBotNotify(id, this);
    }

    public void deleteData(int id){
        DataWord.deleteData(id, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Word> getAll(){
        return DataWord.getAll(this);
    }

    public ArrayList<Word> getDataForNotification(){
        return DataWord.getDataForNotification(this);
    }

    public Word getNewWord(){
        return DataWord.getNewWord(this);
    }

    public void addNewType(Type type) {
        DataType.addNewType(type, this);
    }

    public Type getNewType(){
        return DataType.getNewType(this);
    }

    public void updateType(Type type){
        DataType.updateType(type, this);
    }

    public void deleteType(int id){
        DataType.deleteType(id, this);
    }

    public boolean foreignTypeExist(int typeId){
        return DataType.foreignExist(typeId, this);
    }

    public ArrayList<Type> getAllType(){
        return DataType.getAll(this);
    }

    public void addMeans(ArrayList<Mean> means){
        DataMean.addMeans(means, this);
    }

    public ArrayList<Mean> getAllMean(){
        return DataMean.getAll(this);
    }

    public void deleteMeans(int wordId){
        DataMean.deleteMeans(wordId, this);
    }

    public ArrayList<Tag> getAllTag(){
        return DataTag.getAll(this);
    }

    public void addNewTag(Tag tag){
        DataTag.addNewTag(tag, this);
    }

    public Tag getNewTag(){
        return DataTag.getNewTag(this);
    }

    public void updateTag(Tag tag){
        DataTag.updateTag(tag, this);
    }

    public boolean foreignTagExist(int tagId){
        return DataTag.foreignExist(tagId, this);
    }

    public void deleteTag(int id){
        DataTag.deleteTag(id, this);
    }

    public ArrayList<TagWord> getAllTagWord(){
        return DataTagWord.getAll(this);
    }

    public void addTagWords(int wordId, ArrayList<Tag> tags){
        DataTagWord.addTags(wordId, tags, this);
    }

    public void deleteTagWordByWordId(int wordId){
        DataTagWord.deleteByWordId(wordId, this);
    }
}
