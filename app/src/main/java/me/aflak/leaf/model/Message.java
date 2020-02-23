package me.aflak.leaf.model;

public class Message {
    private byte code;
    private byte sourceId;
    private byte[] data;

    public Message(byte code, byte sourceId, byte[] data) {
        this.code = code;
        this.sourceId = sourceId;
        this.data = data;
    }

    public byte getCode() {
        return code;
    }

    public byte getSourceId() {
        return sourceId;
    }

    public byte[] getData() {
        return data;
    }
}
