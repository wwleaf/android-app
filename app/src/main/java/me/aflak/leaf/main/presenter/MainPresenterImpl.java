package me.aflak.leaf.main.presenter;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.arduino.Message;
import me.aflak.leaf.arduino.Utils;
import me.aflak.leaf.graph.Node;
import me.aflak.leaf.main.interactor.MainInteractor;
import me.aflak.leaf.main.interactor.MainInteractorImpl;
import me.aflak.leaf.main.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView view;
    private MainInteractor interactor;
    private Arduino arduino;
    private UsbDevice device;

    public MainPresenterImpl(MainView view, MainInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onCreate(Context context) {
        view.hideConnectButton();
        view.hideChat();

        arduino = new Arduino(context);
        interactor.setOnGraphListener(onGraphListener);
        interactor.startTimer();
    }

    @Override
    public void onStart() {
        arduino.setArduinoListener(new ArduinoListener() {
            @Override
            public void onArduinoAttached(UsbDevice device) {
                MainPresenterImpl.this.device = device;
                view.showConnectedIcon();
                view.showConnectButton();
            }

            @Override
            public void onArduinoDetached() {
                view.hideChat();
                view.showDisconnectedIcon();
                view.hideConnectButton();
            }

            @Override
            public void onArduinoMessage(byte[] data) {
                Message message = interactor.parseMessage(data);
                if (message != null) {
                    if (message.getCode() == Message.TARGET_MESSAGE_CODE) {
                        byte[] msg = message.getData();
                        boolean shouldBroadcast = interactor.shouldBroadcast(message);
                        if (shouldBroadcast) {
                            byte[] newMessage = new byte[msg.length + 2];
                            newMessage[0] = message.getCode();
                            newMessage[1] = interactor.getId();
                            System.arraycopy(msg, 0, newMessage, 2, msg.length);
                            byte[] arduinoMessage = Utils.formatArduinoMessage(newMessage);
                            arduino.send(arduinoMessage);
                        } else if (interactor.isTarget(message)) {
                            int startIndex = msg[0] + 1;
                            byte[] newMessage = new byte[msg.length - startIndex];
                            System.arraycopy(msg, startIndex, newMessage, 0, newMessage.length);
                            view.appendChatMessage("[" + message.getSourceId() + "] -> [" + interactor.getId() + "] [" + new String(newMessage) + "]");
                        }
                    } else if (message.getCode() == Message.BROADCAST_MESSAGE_CODE) {
                        view.appendChatMessage("[" + message.getSourceId() + "] -> [0] [" + new String(message.getData()) + "]");
                    } else if (message.getCode() == Message.BROADCAST_GRAPH_CODE) {
                        interactor.processReceivedGraph(message);
                    }
                }
            }

            @Override
            public void onArduinoOpened() {
                view.showChat();
            }

            @Override
            public void onUsbPermissionDenied() {
                view.showMessage("We need these permissions to communicate with Leaf.");
            }
        });
    }

    @Override
    public void onDestroy() {
        interactor.stopTimer();
        interactor.saveGraph();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onConnectClicked(int id) {
        if (id > 0 && id <= 127) {
            interactor.setId((byte) id);
            arduino.open(device);
        } else {
            view.showMessage("Invalid id");
        }
    }

    @Override
    public void onMessage(String message, int destId) {
        byte[] data = interactor.formatMessage(message.getBytes(), destId);
        if (data != null) {
            view.clearInput();
            view.appendChatMessage("[" + interactor.getId() + "] -> [" + destId + "] [" + message + "]");
            data = Utils.formatArduinoMessage(data);
            arduino.send(data);
        } else {
            view.showMessage("Invalid destination id");
        }
    }

    private MainInteractorImpl.OnGraphListener onGraphListener = new MainInteractorImpl.OnGraphListener() {
        @Override
        public void onGraphChanged(Set<Node> nodes) {
            List<Integer> ids = new ArrayList<>();
            for (Node n : nodes) {
                ids.add(n.getId());
            }
            view.showUsers(ids);
        }

        @Override
        public void onTick() {
            byte[] message = interactor.getGraphBroadcastMessage();
            if (message != null) {
                byte[] data = Utils.formatArduinoMessage(message);
                arduino.send(data);
            }
        }
    };
}
