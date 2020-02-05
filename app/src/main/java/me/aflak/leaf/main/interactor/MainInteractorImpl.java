package me.aflak.leaf.main.interactor;

import android.os.Handler;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import me.aflak.leaf.MyApplication;
import me.aflak.leaf.graph.DaggerGraphComponent;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.graph.GraphService;
import me.aflak.leaf.graph.Node;

public class MainInteractorImpl implements MainInteractor {
    private final static int INTERVAL_SEC = 60 * 5;
    private final static char ID = 1;
    private final static char BROADCAST_ID = 0;
    private final static char GRAPH_BROADCAST_CODE = 127;
    private final static char TARGET_BROADCAST_CODE = 126;
    private OnGraphListener listener;
    private Handler handler;
    private Runnable handlerTask;

    @Inject GraphService graphService;

    public MainInteractorImpl() {
        DaggerGraphComponent.builder()
                .graphModule(new GraphModule())
                .appModule(MyApplication.getApp().getAppModule())
                .build().inject(this);

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
    public void startTimer() {
        handlerTask.run();
    }

    @Override
    public void stopTimer() {
        handler.removeCallbacks(handlerTask);
    }

    @Override
    public boolean processMessage(String message) {
        if (message.length() == 0) {
            return false;
        }

        if (message.charAt(0) == GRAPH_BROADCAST_CODE) {
            List<Pair<Node, Node>> edges = new ArrayList<>();
            String[] pairs = message.substring(1).split(",");
            int fromId = Integer.parseInt(pairs[0]);
            edges.add(Pair.create(new Node(ID), new Node(fromId)));
            for (int i=1 ; i<pairs.length ; i++) {
                String[] nodes = pairs[i].split(":");
                Node n1 = new Node(Integer.parseInt(nodes[0]));
                Node n2 = new Node(Integer.parseInt(nodes[1]));
                edges.add(Pair.create(n1, n2));
            }
            boolean hasChanged = graphService.load(edges);
            if (listener != null && hasChanged) {
                listener.onGraphChanged(graphService.getNodes());
            }
            return false;
        }

        return true;
    }

    @Override
    public String getMessage(String message, int destId) {
        if (destId == BROADCAST_ID) {
            return getBroadcastMessage(message);
        }
        // prefix shortest path...
        message = TARGET_BROADCAST_CODE + message;
        return message;
    }

    @Override
    public String getBroadcastMessage(String message) {
        return message;
    }

    @Override
    public String getMapMessage() {
        List<Pair<Node, Node>> edges = graphService.getEdges();
        List<String> edgesString = edges.stream().map(pair -> pair.first.getId() + "," + pair.second.getId()).collect(Collectors.toList());
        return GRAPH_BROADCAST_CODE + ID + "," + String.join(":", edgesString);
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
