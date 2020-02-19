package me.aflak.leaf.main.presenter;

import android.content.Context;

public interface MainPresenter {
    void onCreate(Context context);
    void onStart();
    void onPause();
    void onDestroy();
    void onConnectClicked(int id);
    void onMessage(String message, int destId);
    void onToggle(boolean isChecked);
}
