package com.example.englishnotification;

import java.io.Serializable;

public class ItemData implements Serializable {
    public int id;
    public String english;
    public String vietnamese;
    public String date;
    public int notification;
    //0 khong dat thong bao
    //1 dat thong bao
    public int auto;

    public ItemData(int id, String date, String english, String vietnamese, int notification, int auto) {
        this.id = id;
        this.english = english;
        this.vietnamese = vietnamese;
        this.date = date;
        this.notification = notification;
        this.auto = auto;
    }
}