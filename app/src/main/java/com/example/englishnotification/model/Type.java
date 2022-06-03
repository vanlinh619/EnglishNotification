package com.example.englishnotification.model;

import java.io.Serializable;

public class Type implements Serializable {
    public int id;
    public String name;

    public Type(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
