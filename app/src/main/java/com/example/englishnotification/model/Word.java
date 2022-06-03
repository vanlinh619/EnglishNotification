package com.example.englishnotification.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Word implements Serializable {
    public int id;
    public String english;
    public ArrayList<Mean> means;
    public String date;
    public int notification;
    //0 khong dat thong bao
    //1 dat thong bao
    public int auto;
    public int game;
    public ArrayList<Tag> tags;
    public ArrayList<RelationWord> relationWords;
    public int forget;

    public Word(int id, String date, String english, int notification, int auto, int game, int forget) {
        this.id = id;
        this.english = english;
        this.date = date;
        this.notification = notification;
        this.auto = auto;
        this.game = game;
        this.forget = forget;
    }
}