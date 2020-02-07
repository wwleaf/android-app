package me.aflak.leaf.main.interactor;

import android.os.Handler;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import me.aflak.leaf.arduino.Message;
import me.aflak.leaf.graph.GraphService;
import me.aflak.leaf.graph.Node;

public class MainInteractorImpl implements MainInteractor {
    private final static int INTERVAL_SEC = 60 * 5;

    private GraphService graphService;
    private OnGraphListener listener;
    private Handler handler;
    private Runnable handlerTask;
    private byte userId = 1;

    public MainInteractorImpl(GraphService graphService) {
        this.graphService = graphService;

        graphService.load();
        handler = new Handler();
        handlerTask = () -> {
            if (listener != null) {
                listener.onTick();
            }
            handler.postDelayed(handlerTask, INTERVAL_SEC);
        };
    }

    @Override
    public void setId(byte id) {
        userId = id;
    }

    @Override
    public byte getId() {
        return userId;
    }

    @Override
    public void startTimer() {
        handlerTask.run();
    }

    @Override
    public void stopTimer() {
        handler.removeCallbacks(handlerTask);
    }

    @Override
    public Message parseMessage(byte[] message) {
        if (message.length < 3) {
            return null;
        }
        byte[] data = new byte[message.length - 2];
        System.arraycopy(message, 2, data, 0, message.length - 2);
        return new Message(message[0], message[1], data);
    }

    @Override
    public void processReceivedGraph(Message message) {
        List<Pair<Node, Node>> edges = new ArrayList<>();
        byte[] data = message.getData();
        if (data.length % 2 == 0) {
            edges.add(Pair.create(new Node(userId), new Node(message.getSourceId())));
            for (int i = 0; i < data.length; i += 2) {
                Node n1 = new Node(data[i]);
                Node n2 = new Node(data[i + 1]);
                edges.add(Pair.create(n1, n2));
            }
            boolean hasChanged = graphService.load(edges);
            if (listener != null && hasChanged) {
                listener.onGraphChanged(graphService.getNodes());
            }
        }
    }

    @Override
    public byte[] formatMessage(byte[] message, int destId) {
        if (destId == Message.BROADCAST_ID) {
            byte[] newMessage = new byte[message.length + 2];
            newMessage[0] = Message.BROADCAST_MESSAGE_CODE;
            newMessage[1] = userId;
            System.arraycopy(message, 0, newMessage, 2, message.length);
            return newMessage;
        }

        // shortest path to target
        List<Node> path = graphService.shortestPath(new Node(userId), new Node(destId));
        if (path.isEmpty()) {
            return null;
        }

        byte[] newMessage = new byte[message.length + 2 + path.size()];
        newMessage[0] = Message.TARGET_MESSAGE_CODE;
        newMessage[1] = userId;
        int pos = 2;
        for (Node node : path) {
            newMessage[pos++] = (byte) node.getId();
        }
        System.arraycopy(message, 0, newMessage, pos, message.length);
        return newMessage;
    }

    @Override
    public byte[] getGraphBroadcastMessage() {
        List<Pair<Node, Node>> edges = graphService.getEdges();
        if (edges.isEmpty()) {
            return  null;
        }

        int byteCount = edges.size() + 1;
        byte[] message = new byte[byteCount];
        message[0] = Message.BROADCAST_GRAPH_CODE;
        message[1] = userId;
        int pos = 2;
        for (Pair<Node, Node> pair : edges) {
            message[pos++] = (byte) pair.first.getId();
            message[pos++] = (byte) pair.second.getId();
        }
        return message;
    }

    @Override
    public void saveGraph() {
        graphService.save();
    }

    @Override
    public void setOnGraphListener(OnGraphListener listener) {
        this.listener = listener;
    }

    public interface OnGraphListener {
        void onGraphChanged(List<Node> nodes);
        void onTick();
    }
}
