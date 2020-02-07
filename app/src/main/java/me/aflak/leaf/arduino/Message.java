package me.aflak.leaf.arduino;

public class Message {
    public final static byte BROADCAST_ID = 0;
    public final static byte BROADCAST_GRAPH_CODE = 127;
    public final static byte BROADCAST_MESSAGE_CODE = 126;
    public final static byte TARGET_MESSAGE_CODE = 125;

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

    public void setCode(byte code) {
        this.code = code;
    }

    public byte getSourceId() {
        return sourceId;
    }

    public void setSourceId(byte sourceId) {
        this.sourceId = sourceId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
