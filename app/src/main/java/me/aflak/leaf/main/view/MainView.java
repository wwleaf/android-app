package me.aflak.leaf.main.view;

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
    void showUsers(List<Integer> users);
    void clearInput();
}
