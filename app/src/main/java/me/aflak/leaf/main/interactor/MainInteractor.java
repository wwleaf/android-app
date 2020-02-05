package me.aflak.leaf.main.interactor;

public interface MainInteractor {
    void startTimer();
    void stopTimer();
    boolean processMessage(String message);
    String getMessage(String message, int destId);
    String getBroadcastMessage(String message);
    void setOnGraphListener(MainInteractorImpl.OnGraphListener listener);
    String getMapMessage();
    void saveGraph();
}
