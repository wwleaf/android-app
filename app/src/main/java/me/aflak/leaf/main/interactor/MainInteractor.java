package me.aflak.leaf.main.interactor;

import me.aflak.leaf.arduino.Message;

public interface MainInteractor {
    // reception
    Message parseMessage(byte[] message);
    void updateGraph(Message message);
    byte[] getDataFromTargetedMessage(Message message);
    boolean isTarget(Message message);

    // transmission
    byte[] formatMessage(byte[] message, int destId);
    byte[] getGraphBroadcastMessage();
    byte[] getGraphForwardMessage(Message message);
    boolean shouldForwardGraph(Message message);

    void setId(int id);
    byte getId();
    boolean isValidId(int id);
    void saveGraph();
    void setOnGraphListener(MainInteractorImpl.OnGraphListener listener);
}
