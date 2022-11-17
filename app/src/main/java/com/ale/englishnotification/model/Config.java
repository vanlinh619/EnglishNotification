package com.ale.englishnotification.model;

import java.io.Serializable;

public class Config implements Serializable {
    public int id;
    public int autoNotify;

    public Config(int id, int autoNotify){
        this.id = id;
        this.autoNotify = autoNotify;
    }
}
