package com.example.englishnotification.model;

import java.io.Serializable;

public class Tag implements Serializable {
    public int id;
    public String name;

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
