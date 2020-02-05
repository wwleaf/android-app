package me.aflak.leaf.main.interactor;

public interface MainInteractor {
    void startTimer();
    void stopTimer();
    void processMessage(String message);
    void setOnGraphListener(MainInteractorImpl.OnGraphListener listener);
    String getMapMessage();
    void saveGraph();
}
