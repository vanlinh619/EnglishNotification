package com.example.englishnotification.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ItemData implements Serializable {
    public int id;
    public String english;
    public String vietnamese;
    public String date;
    public int notification;
    //0 khong dat thong bao
    //1 dat thong bao
    public int auto;
    public int game;
    public String tags;
    public String types;
    public String relatedWords;
    public String synonyms;
    public String antonyms;
    public int forget;

    public ItemData(int id, String date, String english, String vietnamese, int notification, int auto, int game,
                    String tags, String types, String relatedWords, String synonyms, String antonyms, int forget) {
        this.id = id;
        this.english = english;
        this.vietnamese = vietnamese;
        this.date = date;
        this.notification = notification;
        this.auto = auto;
        this.game = game;
        this.tags = tags;
        this.types = types;
        this.relatedWords = relatedWords;
        this.synonyms = synonyms;
        this.antonyms = antonyms;
        this.forget = forget;
    }
}