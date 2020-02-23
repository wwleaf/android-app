package me.aflak.leaf.main.interactor;

import android.content.Context;

import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.model.Message;

public interface MainInteractor {
    // device
    void onCreate(Context context);
    void onStart(ArduinoListener arduinoListener);
    void openConnection();
    void closeConnection();
    void send(byte[] message);

    // reception
    Message parseMessage(byte[] message);
    void updateGraph(Message message);
    byte[] getDataFromTargetedMessage(Message message);
    boolean isTarget(Message message);

    // transmission
    byte[] formatMessage(byte[] message, byte destId);
    byte[] getGraphBroadcastMessage();
    byte[] getGraphForwardMessage(Message message);
    boolean shouldForwardGraph(Message message);

    // id related
    void setId(int id);
    byte getId();
    boolean isValidId(int id);
    boolean isValidDestinationId(int id);

    // graph
    void saveGraph();
    void setOnGraphListener(MainInteractorImpl.OnGraphListener listener);
}
