package me.aflak.leaf.main.interactor;

import me.aflak.leaf.arduino.Message;

public interface MainInteractor {
    Message parseMessage(byte[] message);
    byte[] formatMessage(byte[] message, int destId);
    boolean shouldBroadcast(Message message);
    boolean isTarget(Message message);
    void processReceivedGraph(Message message);
    void setOnGraphListener(MainInteractorImpl.OnGraphListener listener);
    byte[] getGraphBroadcastMessage();
    void saveGraph();
    void setId(byte id);
    byte getId();
}
