package me.aflak.leaf.main.entities;

import androidx.annotation.NonNull;

public class Destination {
    private String display;
    private byte id;

    public Destination(String display, byte id) {
        this.display = display;
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return display;
    }
}
