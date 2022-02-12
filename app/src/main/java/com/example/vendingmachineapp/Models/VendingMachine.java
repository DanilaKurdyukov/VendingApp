package com.example.vendingmachineapp.Models;

import java.io.Serializable;

public class VendingMachine implements Serializable {
    private int id;
    private String secretCode;

    public VendingMachine(int id, String secretCode) {
        this.id = id;
        this.secretCode = secretCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
