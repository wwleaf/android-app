package me.aflak.leaf.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    private Set<Node> nodes;
    private Set<Edge> edges;

    public Graph() {
        nodes = new HashSet<>();
        edges = new HashSet<>();
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addNodes(Set<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        this.nodes.add(edge.getFrom());
        this.nodes.add(edge.getTo());
        this.edges.add(edge);
    }

    public boolean addEdges(Set<Edge> edges) {
        boolean changed = false;
        for (Edge edge : edges) {
            if (!this.edges.contains(edge)) {
                this.nodes.add(edge.getFrom());
                this.nodes.add(edge.getTo());
                edges.add(edge);
                changed = true;
            }
        }
        return changed;
    }

    public boolean hasEdge(Node from, Node to) {
        return edges.contains(new Edge(from, to));
    }

    public boolean hasNode(Node node) {
        return nodes.contains(node);
    }

    public void connect(Node from, Node to) {
        nodes.add(from);
        nodes.add(to);
        edges.add(new Edge(from, to));
    }

    public List<Node> shortestPath(Node from, Node to) {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            return null;
        }

        // TODO dijkstra
        return Arrays.asList(from, to);
    }
}
