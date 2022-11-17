package com.ale.englishnotification.model;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mean mean = (Mean) o;
        return id == mean.id && wordId == mean.wordId && Objects.equals(type, mean.type) && Objects.equals(meanWord, mean.meanWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, meanWord, wordId);
    }
}
