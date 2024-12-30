package com.example.test;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private String password;

    // Constructor
    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
