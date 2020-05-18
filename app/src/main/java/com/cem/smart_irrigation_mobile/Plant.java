package com.cem.smart_irrigation_mobile;

public class Plant {
    public String name;
    public int id;

    public Plant(String name, int id ){
        this.id = id;
        this.name = name;

    }

    public String toString() {
        return name;
    }
}
