package me.aflak.leaf.main.interactor;

import java.nio.ByteBuffer;
import java.util.Arrays;

class Serial {
    static byte[] formatMessage(byte[] message) {
        byte[] sizeBuff = ByteBuffer.allocate(2).put((byte) message.length).array();
        return ByteBuffer.allocate(message.length + sizeBuff.length).put(sizeBuff).put(message).array();
    }

    static byte[] parseMessage(byte[] message) {
        if (message.length > 2) {
            int length = ByteBuffer.wrap(message, 0, 2).get();
            if (message.length - 2 == length) {
                return Arrays.copyOfRange(message, 2, message.length);
            }
        }
        return null;
    }
}
