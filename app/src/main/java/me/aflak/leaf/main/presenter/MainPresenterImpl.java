package me.aflak.leaf.main.presenter;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.app.Utils;
import me.aflak.leaf.arduino.Message;
import me.aflak.leaf.graph.Node;
import me.aflak.leaf.main.interactor.MainInteractor;
import me.aflak.leaf.main.interactor.MainInteractorImpl;
import me.aflak.leaf.main.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView view;
    private MainInteractor interactor;
    private Arduino arduino;
    private UsbDevice device;
    private boolean debugEnabled;

    public MainPresenterImpl(MainView view, MainInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
        debugEnabled = false;
    }

    @Override
    public void onCreate(Context context) {
        view.hideConnectButton();
        view.hideChat();
        view.showDisconnectedIcon();

        arduino = new Arduino(context);
        interactor.setOnGraphListener(onGraphListener);
        view.showId(interactor.getId());
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
                if (debugEnabled) {
                    view.appendChatMessage("DEBUG: " + Arrays.toString(data));
                }

                Message message = interactor.parseMessage(data);
                if (message != null) {
                    interactor.updateGraph(message);
                    if (message.getCode() == Message.TARGET_MESSAGE_CODE) {
                        if (interactor.isTarget(message)) {
                            byte[] content = interactor.getDataFromTargetedMessage(message);
                            view.appendChatMessage("[" + message.getSourceId() + "] -> [" + interactor.getId() + "] [" + new String(content) + "]");
                        } else if (interactor.shouldForwardGraph(message)) {
                            arduino.send(interactor.getGraphForwardMessage(message));
                        }
                    } else if (message.getCode() == Message.BROADCAST_MESSAGE_CODE) {
                        view.appendChatMessage("[" + message.getSourceId() + "] -> [0] [" + new String(message.getData()) + "]");
                    }
                }
            }

            @Override
            public void onArduinoOpened() {
                view.showChat();
                me.aflak.leaf.app.Utils.executeNTimes(5, 1000 * 10, () -> broadcastGraph());
            }

            @Override
            public void onUsbPermissionDenied() {
                view.showMessage("We need these permissions to communicate with Leaf.");
            }
        });
    }

    @Override
    public void onPause() {
        interactor.saveGraph();
    }

    @Override
    public void onDestroy() {
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onConnectClicked(String id) {
        if (Utils.isNumeric(id)) {
            int sourceId = Integer.parseInt(id);
            if (interactor.isValidId(sourceId)) {
                interactor.setId(sourceId);
                arduino.open(device);
            } else {
                view.showMessage("Invalid id");
            }
        } else {
            view.showMessage("Id must be a number");
        }
    }

    @Override
    public void onMessage(String message, String id) {
        if (Utils.isNumeric(id)) {
            int intId = Integer.parseInt(id);
            if (interactor.isValidId(intId)) {
                byte destId = (byte) intId;
                byte[] data = interactor.formatMessage(message.getBytes(), destId);
                if (data != null) {
                    view.clearInput();
                    view.appendChatMessage("[" + interactor.getId() + "] -> [" + destId + "] [" + message + "]");
                    arduino.send(data);
                } else {
                    view.showMessage("Cannot reach id=" + id);
                }
            } else {
                view.showMessage("Invalid destination id");
            }
        } else {
            view.showMessage("Destination id must be a number");
        }
    }

    @Override
    public void onToggle(boolean isChecked) {
        debugEnabled = isChecked;
        view.showMessage("hey");
    }

    private void broadcastGraph() {
        byte[] message = interactor.getGraphBroadcastMessage();
        if (message != null) {
            arduino.send(message);
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
            broadcastGraph();
        }
    };
}
