package me.aflak.leaf.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

    public void connect(Node from, Node to) {
        nodes.add(from);
        nodes.add(to);
        edges.add(new Edge(from, to));
    }

    public List<Node> shortestPath(Node from, Node to) {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            return null;
        }

        Set<_Edge> _edges = new HashSet<>();
        for (Edge e : this.edges) {
            _edges.add(new _Edge(e));
        }

        Queue<_Node> queue = new LinkedList<>();
        queue.add(new _Node(from));
        _Node _to = null;

        while (!queue.isEmpty()) {
            _Node n = queue.remove();
            if (n.node.equals(to)) {
                _to = n;
                break;
            }

            for (_Edge e : _edges) {
                if (e._from.node.equals(n.node)) {
                    if (!e._to.discovered) {
                        e._to.discovered = true;
                        e._to.parent = n;
                        queue.add(e._to);
                    }
                }

                if (e._to.node.equals(n.node)) {
                    if (!e._from.discovered) {
                        e._from.discovered = true;
                        e._from.parent = n;
                        queue.add(e._from);
                    }
                }
            }
        }

        if (_to == null) {
            return null;
        }

        List<Node> path = new ArrayList<>();
        while (_to.parent != null) {
            path.add(_to.node);
            _to = _to.parent;
        }
        path.add(from);
        Collections.reverse(path);

        return path;
    }

    private static class _Edge {
        _Node _from;
        _Node _to;

        _Edge(Edge edge) {
            _from = new _Node(edge.getFrom());
            _to = new _Node(edge.getTo());
        }
    }

    private static class _Node {
        Node node;
        _Node parent;
        boolean discovered;

        _Node(Node node) {
            this.node = node;
            parent = null;
            discovered = false;
        }
    }
}
