package com.example.englishnotification;

import java.io.Serializable;

public class ItemData implements Serializable {
    public int id;
    public String english;
    public String vietnamese;
    public String date;

    public ItemData(int id, String date, String english, String vietnamese) {
        this.id = id;
        this.english = english;
        this.vietnamese = vietnamese;
        this.date = date;
    }
}