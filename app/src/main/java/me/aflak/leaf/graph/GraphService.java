package me.aflak.leaf.graph;

import android.content.Context;
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

    public void update(GraphService graphService) {
        for (DefaultEdge edge : graphService.graph.edgeSet()) {
            graph.addEdge(graphService.graph.getEdgeSource(edge), graphService.graph.getEdgeTarget(edge));
        }
    }

    public void connect(Node a, Node b) {
        graph.addEdge(a, b);
    }

    public List<Node> shortestPath(Node from, Node to) {
        GraphPath<Node, DefaultEdge> path = DijkstraShortestPath.findPathBetween(graph, from, to);
        List<Node> nodes = new ArrayList<>();
        for (DefaultEdge edge : path.getEdgeList()) {
            nodes.add(graph.getEdgeSource(edge));
        }
        nodes.add(to);
        return nodes;
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

    public void load(List<Pair<Node, Node>> edges) {
        for (Pair<Node, Node> pair : edges) {
            graph.addEdge(pair.first, pair.second);
        }
    }

    public List<Pair<Node, Node>> getEdges() {
        List<Pair<Node, Node>> edges = new ArrayList<>();
        for (DefaultEdge e : graph.edgeSet()) {
            edges.add(Pair.create(graph.getEdgeSource(e), graph.getEdgeTarget(e)));
        }
        return edges;
    }
}
