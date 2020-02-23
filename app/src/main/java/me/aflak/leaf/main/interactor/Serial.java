package me.aflak.leaf.main.interactor;

import java.nio.ByteBuffer;

class Serial {
    static byte[] formatMessage(byte[] message) {
        byte[] sizeBuff = ByteBuffer.allocate(2).putInt(message.length).array();
        return ByteBuffer.allocate(message.length + sizeBuff.length).put(sizeBuff).put(message).array();
    }

    static byte[] parseMessage(byte[] message) {
        if (message.length > 2) {
            int length = ByteBuffer.wrap(message, 0, 2).getInt();
            if (message.length - 2 == length) {
                return ByteBuffer.wrap(message, 2, message.length - 2).array();
            }
        }
        return null;
    }
}
