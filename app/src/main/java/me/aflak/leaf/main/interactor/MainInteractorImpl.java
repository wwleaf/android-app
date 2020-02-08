package me.aflak.leaf.main.interactor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.aflak.leaf.arduino.Message;
import me.aflak.leaf.graph.Edge;
import me.aflak.leaf.graph.Graph;
import me.aflak.leaf.graph.GraphManager;
import me.aflak.leaf.graph.Node;

public class MainInteractorImpl implements MainInteractor {
    private GraphManager graphManager;
    private OnGraphListener listener;
    private Graph graph;
    private byte userId;
    private Node selfNode;

    public MainInteractorImpl(GraphManager graphManager) {
        this.graphManager = graphManager;
        this.graph = graphManager.load();
        this.userId = 1;
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
        boolean hasChanged = false;

        if (code == Message.BROADCAST_MESSAGE_CODE) {
            hasChanged = graph.connect(selfNode, new Node(message.getSourceId()));
        } else if (code == Message.BROADCAST_GRAPH_CODE) {
            Set<Edge> edges = new HashSet<>();
            edges.add(new Edge(selfNode, new Node(message.getSourceId())));
            for (int i=0 ; i<data.length ; i+=2) {
                Node n1 = new Node(data[i]);
                Node n2 = new Node(data[i + 1]);
                edges.add(new Edge(n1, n2));
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
        if (destId == Message.BROADCAST_ID) {
            byte[] newMessage = new byte[message.length + 2];
            newMessage[0] = Message.BROADCAST_MESSAGE_CODE;
            newMessage[1] = userId;
            System.arraycopy(message, 0, newMessage, 2, message.length);
            return newMessage;
        }

        // shortest path to target
        List<Node> path = graph.shortestPath(selfNode, new Node(destId));
        if (path == null || path.isEmpty()) {
            return null;
        }

        byte[] newMessage = new byte[3 + path.size() + message.length];
        newMessage[0] = Message.TARGET_MESSAGE_CODE;
        newMessage[1] = userId;
        newMessage[2] = (byte) path.size();
        int pos = 3;
        for (Node node : path) {
            newMessage[pos++] = (byte) node.getId();
        }
        System.arraycopy(message, 0, newMessage, pos, message.length);
        return newMessage;
    }

    @Override
    public byte[] getGraphBroadcastMessage() {
        Set<Edge> edges = graph.getEdges();
        int byteCount = 2 * edges.size() + 2;
        byte[] message = new byte[byteCount];
        message[0] = Message.BROADCAST_GRAPH_CODE;
        message[1] = userId;
        int pos = 2;
        for (Edge edge : edges) {
            message[pos++] = (byte) edge.getFrom().getId();
            message[pos++] = (byte) edge.getTo().getId();
        }
        return message;
    }

    @Override
    public byte[] getGraphForwardMessage(Message message) {
        byte[] data = message.getData();
        byte[] newMessage = Arrays.copyOf(data, data.length);
        newMessage[1] = userId;
        return newMessage;
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
    public void setId(byte id) {
        userId = id;
        selfNode = new Node(userId);
        graph.addNode(selfNode);
    }

    @Override
    public byte getId() {
        return userId;
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
