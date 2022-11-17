package com.ale.englishnotification.model;

import java.io.Serializable;
import java.util.Objects;

public class Type implements Serializable {
    public int id;
    public String name;

    public Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return id == type.id && Objects.equals(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
