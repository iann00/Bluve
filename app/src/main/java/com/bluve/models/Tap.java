package com.bluve.models;

public class Tap {
    /**
     * Representa o estado atual da torneira: true para ligada e false para desligada
     */
    public boolean open;

    /**
     * Consumo total da torneira, isso é, a soma de todos os usos anteriores
     */
    public int totalUsage;

    /**
     * Último consumo da torneira
     */
    public int lastUsage;

    /**
     * Total de horas que a torneira passou aberta
     */
    public int totalTime;

    /**
     * Tempo que durou a última abertura da torneira
     */
    public int lastTime;


    public Tap() {

    }

    public Tap(boolean open) {
        this.open = open;
    }

    public Tap(boolean open,
               int totalUsage,
               int lastUsage,
               int totalTime,
               int lastTime) {
        this.open = open;
        this.totalUsage = totalUsage;
        this.lastUsage = lastUsage;
        this.totalTime = totalTime;
        this.lastTime = lastTime;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setTotalUsage(int totalUsage) {
        this.totalUsage = totalUsage;
    }

    public int getTotalUsage() {
        return totalUsage;
    }

    public void setLastUsage(int lastUsage) {
        this.lastUsage = lastUsage;
    }

    public int getLastUsage() {
        return lastUsage;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getLastTime() {
        return lastTime;
    }
}
