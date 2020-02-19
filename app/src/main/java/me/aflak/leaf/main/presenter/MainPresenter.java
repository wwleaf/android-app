package me.aflak.leaf.main.presenter;

import android.content.Context;

public interface MainPresenter {
    void onCreate(Context context);
    void onStart();
    void onPause();
    void onDestroy();
    void onConnectClicked(String id);
    void onMessage(String message, String id);
    void onToggle(boolean isChecked);
}
