package com.example.englishnotification;

public class ItemData {
    public int id = 0;
    public String english = "";
    public String vietnamese = "";
    public String date = "";

    public ItemData(int id, String date, String english, String vietnamese) {
        this.id = id;
        this.english = english;
        this.vietnamese = vietnamese;
        this.date = date;
    }
}