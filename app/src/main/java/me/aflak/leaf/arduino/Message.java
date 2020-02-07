package me.aflak.leaf.arduino;

public class Message {
    public final static byte BROADCAST_ID = 0;
    public final static byte BROADCAST_GRAPH_CODE = 127;
    public final static byte BROADCAST_MESSAGE_CODE = 126;
    public final static byte TARGET_MESSAGE_CODE = 125;

    private int code;
    private int sourceId;
    private byte[] data;

    public Message(int code, int sourceId, byte[] data) {
        this.code = code;
        this.sourceId = sourceId;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
