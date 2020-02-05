package me.aflak.leaf.main.interactor;

import android.os.Handler;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.aflak.leaf.MyApplication;
import me.aflak.leaf.graph.DaggerGraphComponent;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.graph.GraphService;
import me.aflak.leaf.graph.Node;

public class MainInteractorImpl implements MainInteractor {
    private final static int INTERVAL_SEC = 60 * 5;
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
    public void processMessage(String message) {
        if (message.length() == 0) return;
        if (message.charAt(0) == 127) {
            List<Pair<Node, Node>> edges = new ArrayList<>();
            String[] pairs = message.substring(1).split(",");
            for (String pair : pairs) {
                String[] nodes = pair.split(":");
                Node n1 = new Node(Integer.parseInt(nodes[0]), "unnamed");
                Node n2 = new Node(Integer.parseInt(nodes[1]), "unnamed");
                edges.add(Pair.create(n1, n2));
            }
            graphService.load(edges);
            if (listener != null) {
                listener.onGraphChanged(graphService.getNodes());
            }
        }
    }

    @Override
    public String getMapMessage() {
//        List<Pair<Node, Node>> edges = graphService.getEdges();
        return "hello";
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
