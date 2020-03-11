package me.aflak.leaf.main.view;

import android.util.Pair;

import java.util.List;

public interface MainView {
    void showConnectedIcon();
    void showDisconnectedIcon();
    void showConnectButton();
    void hideConnectButton();
    void showMessage(String message);
    void showChat();
    void hideChat();
    void appendChatMessage(String message);
    void showUsers(List<Pair<String, Byte>> users);
    void showId(byte id);
    void clearInput();
    void showToast(String message);
}
