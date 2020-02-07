package me.aflak.leaf.graph;

import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GraphService {
    private final static String PREF_KEY = "graph";
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Graph<Node, DefaultEdge> graph;

    public GraphService(SharedPreferences sharedPreferences, Gson gson) {
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
        this.graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
    }

    public List<Node> shortestPath(Node from, Node to) {
        if (graph.containsVertex(from) && graph.containsVertex(to)) {
            GraphPath<Node, DefaultEdge> path = DijkstraShortestPath.findPathBetween(graph, from, to);
            List<Node> nodes = new ArrayList<>();
            for (DefaultEdge edge : path.getEdgeList()) {
                nodes.add(graph.getEdgeSource(edge));
            }
            nodes.add(to);
            return nodes;
        }
        return null;
    }

    public void save() {
        List<Pair<Node, Node>> edges = getEdges();
        sharedPreferences.edit()
            .putString(PREF_KEY, gson.toJson(edges))
            .apply();
    }

    public void load() {
        String json = sharedPreferences.getString(PREF_KEY, null);
        if (json != null) {
            Type listType = new TypeToken<ArrayList<Node>>(){}.getType();
            List<Pair<Node, Node>> edges = gson.fromJson(json, listType);
            for (Pair<Node, Node> pair : edges) {
                graph.addEdge(pair.first, pair.second);
            }
        }
    }

    public boolean load(List<Pair<Node, Node>> edges) {
        boolean changed = false;
        for (Pair<Node, Node> pair : edges) {
            if (!graph.containsEdge(pair.first, pair.second)) {
                graph.addEdge(pair.first, pair.second);
                changed = true;
            }
        }
        return changed;
    }

    public List<Pair<Node, Node>> getEdges() {
        List<Pair<Node, Node>> edges = new ArrayList<>();
        for (DefaultEdge e : graph.edgeSet()) {
            edges.add(Pair.create(graph.getEdgeSource(e), graph.getEdgeTarget(e)));
        }
        return edges;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(graph.vertexSet());
    }

    public void addNode(Node node) {
        if (!graph.containsVertex(node)) {
            graph.addVertex(node);
        }
    }

    public void connect(Node from, Node to) {
        addNode(from);
        addNode(to);
        graph.addEdge(from, to);
    }
}
