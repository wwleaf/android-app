package me.aflak.leaf.main.presenter;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.app.Utils;
import me.aflak.leaf.models.Message;
import me.aflak.leaf.graph.Node;
import me.aflak.leaf.main.interactor.MainInteractor;
import me.aflak.leaf.main.interactor.MainInteractorImpl;
import me.aflak.leaf.main.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView view;
    private MainInteractor interactor;
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

        interactor.onCreate(context);
        interactor.setOnGraphListener(onGraphListener);
        view.showId(interactor.getId());
    }

    @Override
    public void onStart() {
        interactor.onStart(new ArduinoListener() {
            @Override
            public void onArduinoAttached(UsbDevice device) {
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
                    if (message.getCode() == MainInteractorImpl.TARGET_MESSAGE_CODE) {
                        if (interactor.isTarget(message)) {
                            byte[] content = interactor.getDataFromTargetedMessage(message);
                            view.appendChatMessage("[" + message.getData()[1] + "] -> [" + interactor.getId() + "] [" + new String(content) + "]");
                        } else if (interactor.shouldForwardGraph(message)) {
                            interactor.send(interactor.getGraphForwardMessage(message));
                        }
                    } else if (message.getCode() == MainInteractorImpl.BROADCAST_MESSAGE_CODE) {
                        view.appendChatMessage("[" + message.getSourceId() + "] -> [0] [" + new String(message.getData()) + "]");
                    }
                }
            }

            @Override
            public void onArduinoOpened() {
                view.showChat();
                view.showUsers(Collections.singletonList(Pair.create("0 (broadcast)", (byte) 0)));
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
        interactor.closeConnection();
    }

    @Override
    public void onConnectClicked(String id) {
        if (Utils.isNumeric(id)) {
            int sourceId = Integer.parseInt(id);
            if (interactor.isValidId(sourceId)) {
                interactor.setId(sourceId);
                interactor.openConnection();
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
            if (interactor.isValidDestinationId(intId)) {
                byte destId = (byte) intId;
                byte[] data = interactor.formatMessage(message.getBytes(), destId);
                if (data != null) {
                    view.clearInput();
                    view.appendChatMessage("[" + interactor.getId() + "] -> [" + destId + "] [" + message + "]");
                    interactor.send(data);
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
    }

    private void broadcastGraph() {
        byte[] message = interactor.getGraphBroadcastMessage();
        if (message != null) {
            interactor.send(message);
        }
    }

    private MainInteractorImpl.OnGraphListener onGraphListener = new MainInteractorImpl.OnGraphListener() {
        @Override
        public void onGraphChanged(Set<Node> nodes) {
            List<Pair<String, Byte>> ids = new ArrayList<>();
            ids.add(Pair.create("0 (broadcast)", (byte) 0));
            for (Node n : nodes) {
                ids.add(Pair.create(String.valueOf(n.getId()), (byte) n.getId()));
            }
            view.showUsers(ids);
            broadcastGraph();
        }
    };
}
