package com.ale.englishnotification.model;

import java.io.Serializable;

public class RelationWord implements Serializable {
    public static final int RELATED = 0;
    public static final int SYNONYM = 1;
    public static final int ANTONYM = 2;

    public int id;
    public int wordId;
    public int relationWordId;
    public int relationType;

    public RelationWord(int id, int wordId, int relationWordId, int relationType) {
        this.id = id;
        this.wordId = wordId;
        this.relationWordId = relationWordId;
        this.relationType = relationType;
    }
}
