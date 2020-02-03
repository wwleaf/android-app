package me.aflak.leaf.main.presenter;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.main.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView view;
    private Arduino arduino;
    private UsbDevice device;

    public MainPresenterImpl(MainView view) {
        this.view = view;
    }

    @Override
    public void onCreate(Context context) {
        arduino = new Arduino(context);
        view.hideConnectButton();
        view.hideChat();
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
                view.appendChatMessage("<- " + message);
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
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onConnectClicked() {
        arduino.open(device);
    }

    private byte[] formatArduinoMessage(String message) {
        int length = message.length();
        byte[] data = new byte[2 + length];
        data[0] = (byte) length;
        data[1] = (byte) (length >>> 8);
        System.arraycopy(message.getBytes(), 0, data, 2, length);
        return data;
    }

    @Override
    public void onHelloWorld() {
        String message = "Hello World";
        byte[] data = formatArduinoMessage(message);
        arduino.send(data);
        view.appendChatMessage("-> " + message);
    }
}
