package com.example.vendingmachineapp.Models;

import java.io.Serializable;

public class VendingMachineCoin implements Serializable {
    private int id;
    private int machineId;
    private int coinId;
    private int count;
    private boolean isActive;
    private int coinValue;

    public VendingMachineCoin(int id, int machineId, int coinId, int count, boolean isActive, int coinValue) {
        this.id = id;
        this.machineId = machineId;
        this.coinId = coinId;
        this.count = count;
        this.isActive = isActive;
        this.coinValue = coinValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getCoinId() {
        return coinId;
    }

    public void setCoinId(int coinId) {
        this.coinId = coinId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCoinValue() {
        return coinValue;
    }

    public void setCoinValue(int coinValue) {
        this.coinValue = coinValue;
    }
}
