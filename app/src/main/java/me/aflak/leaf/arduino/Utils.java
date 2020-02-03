package me.aflak.leaf.arduino;

public class Utils {
    public static byte[] formatArduinoMessage(String message) {
        int length = message.length();
        byte[] data = new byte[2 + length];
        data[0] = (byte) length;
        data[1] = (byte) (length >>> 8);
        System.arraycopy(message.getBytes(), 0, data, 2, length);
        return data;
    }
}
