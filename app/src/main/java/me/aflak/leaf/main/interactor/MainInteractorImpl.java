package me.aflak.leaf.main.interactor;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;
import me.aflak.leaf.model.Message;
import me.aflak.leaf.graph.Edge;
import me.aflak.leaf.graph.Graph;
import me.aflak.leaf.graph.GraphManager;
import me.aflak.leaf.graph.Node;
import me.aflak.leaf.main.dagger.UserManager;

public class MainInteractorImpl implements MainInteractor {
    private final static byte DELIMITER = 124;
    private final static byte BROADCAST_ID = 0;
    private final static byte BROADCAST_GRAPH_CODE = 127;
    public final static byte BROADCAST_MESSAGE_CODE = 126;
    public final static byte TARGET_MESSAGE_CODE = 125;

    private Arduino arduino;
    private UsbDevice device;

    private GraphManager graphManager;
    private UserManager userManager;

    private OnGraphListener listener;
    private Graph graph;
    private byte userId;
    private Node selfNode;

    public MainInteractorImpl(GraphManager graphManager, UserManager userManager) {
        this.graphManager = graphManager;
        this.userManager = userManager;
        this.graph = graphManager.load();
        this.userId = -1;
    }

    @Override
    public void onCreate(Context context) {
        arduino = new Arduino(context);
//        arduino.addVendorId(1659);
//        arduino.setDelimiter(DELIMITER);
    }

    @Override
    public void onStart(ArduinoListener arduinoListener) {
        arduino.setArduinoListener(new ArduinoListener() {
            @Override
            public void onArduinoAttached(UsbDevice device) {
                MainInteractorImpl.this.device = device;
                arduinoListener.onArduinoAttached(device);
            }

            @Override
            public void onArduinoDetached() {
                arduinoListener.onArduinoDetached();
            }

            @Override
            public void onArduinoMessage(byte[] bytes) {
                arduinoListener.onArduinoMessage(bytes);
            }

            @Override
            public void onArduinoOpened() {
                arduinoListener.onArduinoOpened();
            }

            @Override
            public void onUsbPermissionDenied() {
                arduinoListener.onUsbPermissionDenied();
            }
        });
    }

    @Override
    public void openConnection() {
        arduino.open(device);
    }

    @Override
    public void closeConnection() {
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void send(byte[] message) {
        arduino.send(message);
    }

    private static byte[] formatMessage(byte[] message) {
        int length = message.length;
        byte[] data = new byte[2 + length];
        data[0] = (byte) length;
        data[1] = (byte) (length >>> 8);
//        data[data.length - 1] = DELIMITER;
        System.arraycopy(message, 0, data, 2, length);
        return data;
    }

    @Override
    public Message parseMessage(byte[] message) {
        if (message.length < 2) {
            return null;
        }
        byte[] data = new byte[message.length - 2];
        System.arraycopy(message, 2, data, 0, message.length - 2);
        return new Message(message[0], message[1], data);
    }

    @Override
    public void updateGraph(Message message) {
        byte code = message.getCode();
        byte[] data = message.getData();
        byte sourceId = message.getSourceId();
        boolean hasChanged = false;

        if (code == BROADCAST_MESSAGE_CODE) {
            hasChanged = graph.connect(selfNode, new Node(sourceId));
        } else if (code == BROADCAST_GRAPH_CODE) {
            Set<Edge> edges = new HashSet<>();
            edges.add(new Edge(selfNode, new Node(sourceId)));
            for (int i=0 ; i<data.length ; i+=2) {
                Node n1 = new Node(data[i]);
                Node n2 = new Node(data[i + 1]);
                edges.add(new Edge(n1, n2));
            }
            hasChanged = graph.addEdges(edges);
        } else if (code == TARGET_MESSAGE_CODE) {
            Set<Edge> edges = new HashSet<>();
            edges.add(new Edge(selfNode, new Node(sourceId)));
            byte nodeCount = data[0];
            for (int i=1 ; i<nodeCount ; i++) {
                edges.add(new Edge(new Node(data[i]), new Node(data[i + 1])));
            }
            hasChanged = graph.addEdges(edges);
        }

        if (listener != null && hasChanged) {
            listener.onGraphChanged(graph.getNodes());
        }
    }

    @Override
    public byte[] getDataFromTargetedMessage(Message message) {
        byte[] data = message.getData();
        int startIndex = data[0] + 1;
        byte[] content = new byte[data.length - startIndex];
        System.arraycopy(data, startIndex, content, 0, content.length);
        return content;
    }

    @Override
    public boolean isTarget(Message message) {
        byte[] data = message.getData();
        byte nodeCount = data[0];
        return userId == data[nodeCount];
    }

    @Override
    public byte[] formatMessage(byte[] message, int destId) {
        if (destId == BROADCAST_ID) {
            return formatBroadcastMessage(message);
        }
        return formatTargetedMessage(message, destId);
    }

    private byte[] formatBroadcastMessage(byte[] message) {
        byte[] data = new byte[2 + message.length];
        data[0] = BROADCAST_MESSAGE_CODE;
        data[1] = userId;
        System.arraycopy(message, 0, data, 2, message.length);
        return formatMessage(data);
    }

    private byte[] formatTargetedMessage(byte[] message, int destId) {
        List<Node> path = graph.shortestPath(selfNode, new Node(destId));
        if (path == null || path.isEmpty()) {
            return null;
        }

        byte[] data = new byte[3 + path.size() + message.length];
        data[0] = TARGET_MESSAGE_CODE;
        data[1] = userId;
        data[2] = (byte) path.size();
        int pos = 3;
        for (Node node : path) {
            data[pos++] = (byte) node.getId();
        }
        System.arraycopy(message, 0, data, pos, message.length);
        return formatMessage(data);
    }

    @Override
    public byte[] getGraphBroadcastMessage() {
        Set<Edge> edges = graph.getEdges();
        int byteCount = 2 * edges.size() + 2;
        byte[] message = new byte[byteCount];
        message[0] = BROADCAST_GRAPH_CODE;
        message[1] = userId;
        int pos = 2;
        for (Edge edge : edges) {
            message[pos++] = (byte) edge.getFrom().getId();
            message[pos++] = (byte) edge.getTo().getId();
        }
        return formatMessage(message);
    }

    @Override
    public byte[] getGraphForwardMessage(Message message) {
        byte[] data = message.getData();
        byte[] newMessage = Arrays.copyOf(data, data.length);
        newMessage[1] = userId;
        return formatMessage(newMessage);
    }

    @Override
    public boolean shouldForwardGraph(Message message) {
        byte[] data = message.getData();
        byte nodeCount = data[0];
        int sourcePos = -1;
        for (int i=1 ; i<1 + nodeCount ; i++) {
            if (data[i] == message.getSourceId()) {
                sourcePos = i;
            }
            if (userId == data[i]) {
                return sourcePos != -1 && i < nodeCount;
            }
        }
        return false;
    }

    @Override
    public void setId(int id) {
        userId = (byte) id;
        userManager.setId(userId);
        selfNode = new Node(userId);
    }

    @Override
    public byte getId() {
        if (userId == -1) {
            userId = userManager.getId();
        }
        return userId;
    }

    @Override
    public boolean isValidId(int id) {
        return id > 0 && id <= 127;
    }

    @Override
    public boolean isValidDestinationId(int id) {
        return id >= 0 && id <= 127;
    }

    @Override
    public void saveGraph() {
        graphManager.save(graph);
    }

    @Override
    public void setOnGraphListener(OnGraphListener listener) {
        this.listener = listener;
    }

    public interface OnGraphListener {
        void onGraphChanged(Set<Node> nodes);
    }
}
