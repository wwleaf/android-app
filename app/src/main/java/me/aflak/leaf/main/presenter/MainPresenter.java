package me.aflak.leaf.main.presenter;

import android.content.Context;

public interface MainPresenter {
    void onCreate(Context context);
    void onStart();
    void onDestroy();
    void onConnectClicked();
    void onHelloWorld();
}