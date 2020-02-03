package me.aflak.leaf.main.view;

public interface MainView {
    void showConnectedIcon();
    void showDisconnectedIcon();
    void showConnectButton();
    void hideConnectButton();
    void showMessage(String message);
    void showChat();
    void hideChat();
    void appendChatMessage(String message);
}
