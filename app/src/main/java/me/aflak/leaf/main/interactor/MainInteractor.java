package me.aflak.leaf.main.interactor;

public interface MainInteractor {
    void processMessage(String message);
    void setOnGraphListener(MainInteractorImpl.OnGraphListener listener);
}
