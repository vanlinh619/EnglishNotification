package com.ale.englishnotification.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ale.englishnotification.model.Config;
import com.ale.englishnotification.model.Mean;
import com.ale.englishnotification.model.RelationWord;
import com.ale.englishnotification.model.Tag;
import com.ale.englishnotification.model.TagWord;
import com.ale.englishnotification.model.Type;
import com.ale.englishnotification.model.Word;

import java.io.Serializable;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper implements Serializable {

    public static final String DATABASE_NAME = "englishNotification.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONFIG = "config";
    public static final String CONFIG_ID = "id";
    public static final String CONFIG_AUTO_NOTIFY = "auto_notify";

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


        DataWord.createTable(db);
        DataType.createTable(db);
        DataTag.createTableTag(db);
        DataMean.createTable(db);
        DataTagWord.createTable(db);
        DataRelationWord.createTable(db);

        db.execSQL(createTableConfig);
    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String dropTable = String.format("DROP TABLE IF EXISTS %s", TABLE_WORD);
        String dropTableConfig = String.format("DROP TABLE IF EXISTS %s", TABLE_CONFIG);
//        db.execSQL(dropTable);
        db.execSQL(dropTableConfig);

        onCreate(db);
    }

    public synchronized Config getConfig(){
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

    private synchronized void addDefaultConfig() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(null, %s)", TABLE_CONFIG, 0);
        db.execSQL(query);
        db.close();
    }

    public synchronized void updateConfig(Config config){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s", TABLE_CONFIG,
                CONFIG_AUTO_NOTIFY, config.autoNotify, CONFIG_ID, config.id);
        db.execSQL(query);
        db.close();
    }

    //Word

    public synchronized boolean addNewWord(Word word){
        return DataWord.addNewWord(word, this);
    }

    public synchronized void updateWord(Word word){
        DataWord.updateWord(word, this);
    }

    public synchronized void updateOffNotify(int id){
        DataWord.updateOffNotify(id, this);
    }

    public synchronized void updateOffBotNotify(int id){
        DataWord.updateOffBotNotify(id, this);
    }

    public synchronized void deleteData(int id){
        DataWord.deleteData(id, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized ArrayList<Word> getAll(){
        return DataWord.getAll(this);
    }

    public synchronized ArrayList<Word> getDataForNotification(){
        return DataWord.getDataForNotification(this);
    }

    public synchronized Word getNewWord(){
        return DataWord.getNewWord(this);
    }

    public synchronized Word getWordByEnglish(String english){
        return DataWord.getWordByEnglish(english, this);
    }

    public synchronized void incrementOnceForget(Word word){
       DataWord.incrementOnceForget(word, this);
    }


    //Type

    public synchronized void addNewType(Type type) {
        DataType.addNewType(type, this);
    }

    public synchronized Type getNewType(){
        return DataType.getNewType(this);
    }

    public synchronized boolean typeExist(Type type){
        return DataType.typeExist(type, this);
    }

    public synchronized void updateType(Type type){
        DataType.updateType(type, this);
    }

    public synchronized void deleteType(int id){
        DataType.deleteType(id, this);
    }

    public synchronized boolean foreignTypeExist(int typeId){
        return DataType.foreignExist(typeId, this);
    }

    public synchronized ArrayList<Type> getAllType(){
        return DataType.getAll(this);
    }


    //Mean

    public synchronized void addMeans(ArrayList<Mean> means){
        DataMean.addMeans(means, this);
    }

    public synchronized void addMean(Mean mean){
        DataMean.addMean(mean, this);
    }

    public synchronized ArrayList<Mean> getAllMean(){
        return DataMean.getAll(this);
    }

    public synchronized void deleteMeans(int wordId){
        DataMean.deleteMeans(wordId, this);
    }


    //Tag

    public synchronized ArrayList<Tag> getAllTag(){
        return DataTag.getAll(this);
    }

    public synchronized void addNewTag(Tag tag){
        DataTag.addNewTag(tag, this);
    }

    public synchronized Tag getNewTag(){
        return DataTag.getNewTag(this);
    }

    public synchronized Tag getTagByName(String name){
        return DataTag.getTagByName(name, this);
    }

    public synchronized boolean existTag(Tag tag){
        return DataTag.existTag(tag, this);
    }

    public synchronized void updateTag(Tag tag){
        DataTag.updateTag(tag, this);
    }

    public synchronized boolean foreignTagExist(int tagId){
        return DataTag.foreignExist(tagId, this);
    }

    public synchronized void deleteTag(int id){
        DataTag.deleteTag(id, this);
    }


    //Tag Word

    public synchronized ArrayList<TagWord> getAllTagWord(){
        return DataTagWord.getAll(this);
    }

    public synchronized void addTagWords(int wordId, ArrayList<Tag> tags){
        DataTagWord.addTags(wordId, tags, this);
    }

    public synchronized void deleteTagWordByWordId(int wordId){
        DataTagWord.deleteByWordId(wordId, this);
    }

    public synchronized void deleteTagWordByTagId(int tagId){
        DataTagWord.deleteByTagId(tagId, this);
    }


    //Relation Word

    public synchronized void addNewRelationWord(Word word, Word relationWord, int type){
        DataRelationWord.addRelationWord(word, relationWord, type, this);
    }

    public synchronized void addNewRelationWord(RelationWord relationWord){
        DataRelationWord.addRelationWord(relationWord, this);
    }

    public synchronized void importRelationWord(Word word, Word wordRel, int type){
        Word w = getWordByEnglish(word.english);
        Word wRel = getWordByEnglish(wordRel.english);
        if(w != null && wRel != null){
            DataRelationWord.addRelationWord(w, wRel, type, this);
        }
    }

    public synchronized boolean existRelationWord(Word word, Word relWord, RelationWord relationWord){
        return DataRelationWord.existRelationWord(word, relWord, relationWord, this);
    }

    public synchronized RelationWord getNewRelationWord(){
        return DataRelationWord.getNewRelationWord(this);
    }

    public synchronized ArrayList<RelationWord> getAllRelationWord(){
        return DataRelationWord.getAll(this);
    }

    public synchronized void deleteRelation(int id){
        DataRelationWord.deleteRelation(id, this);
    }
}
