package me.aflak.leaf.graph;

import java.util.Objects;

import androidx.annotation.Nullable;

public class Edge {
    private Node from, to;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Edge)) {
            return false;
        }

        Edge e = (Edge) obj;
        return e.getFrom().equals(from) && e.getTo().equals(to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
