package com.ale.englishnotification.model;

import java.io.Serializable;

public class ItemDataExample implements Serializable {
    public String vietnamese;
    public String english;

    public ItemDataExample(String english) {
        this.english = english;
    }
}
