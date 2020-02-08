package me.aflak.leaf.main.dagger;

import android.content.SharedPreferences;

public class UserManager {
    private final static String PREF_KEY = "user_id";
    private SharedPreferences sharedPreferences;

    public UserManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public byte getId() {
        return (byte) sharedPreferences.getInt(PREF_KEY, 1);
    }

    public void setId(byte id) {
        sharedPreferences.edit()
                .putInt(PREF_KEY, id)
                .apply();
    }
}
