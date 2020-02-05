package me.aflak.leaf.main.presenter;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.arduino.Utils;
import me.aflak.leaf.graph.GraphService;
import me.aflak.leaf.graph.Node;
import me.aflak.leaf.main.interactor.MainInteractor;
import me.aflak.leaf.main.interactor.MainInteractorImpl;
import me.aflak.leaf.main.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView view;
    private MainInteractor interactor;
    private Arduino arduino;
    private UsbDevice device;

    @Inject GraphService service;

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
            public void onArduinoMessage(byte[] bytes) {
                String message = new String(bytes);
                if (interactor.processMessage(message)) {
                    view.appendChatMessage("<- " + message);
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
    public void onConnectClicked() {
        arduino.open(device);
    }

    @Override
    public void onMessage(String message, int destId) {
        message = interactor.getMessage(message, destId);
        byte[] data = Utils.formatArduinoMessage(message);
        arduino.send(data);
        view.appendChatMessage("-> " + message);
    }

    private MainInteractorImpl.OnGraphListener onGraphListener = new MainInteractorImpl.OnGraphListener() {
        @Override
        public void onGraphChanged(List<Node> nodes) {
            List<Integer> ids = new ArrayList<>();
            for (Node n : nodes) {
                ids.add(n.getId());
            }
            view.showUsers(ids);
        }

        @Override
        public void onTick() {
            String message = interactor.getMapMessage();
            message = interactor.getBroadcastMessage(message);
            byte[] data = Utils.formatArduinoMessage(message);
            arduino.send(data);
        }
    };
}
