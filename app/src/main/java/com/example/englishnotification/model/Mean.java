package com.example.englishnotification.model;

import java.io.Serializable;

public class Mean implements Serializable {
    public int id;
    public Type type;
    public String meanWord;
    public int wordId;

    public Mean(int id, String meanWord, int wordId) {
        this.id = id;
        this.meanWord = meanWord;
        this.wordId = wordId;
    }

    public Mean(int id, Type type, String meanWord, int wordId) {
        this.id = id;
        this.type = type;
        this.meanWord = meanWord;
        this.wordId = wordId;
    }
}
