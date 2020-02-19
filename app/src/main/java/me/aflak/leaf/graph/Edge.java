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
        boolean b1 = e.getFrom().equals(from) && e.getTo().equals(to);
        boolean b2 = e.getFrom().equals(to) && e.getTo().equals(from);
        return b1 || b2;
    }

    @Override
    public int hashCode() {
        Node refSmaller, refBigger;
        if (from.getId() < to.getId()) {
            refSmaller = from;
            refBigger = to;
        } else {
            refSmaller = to;
            refBigger = from;
        }
        return Objects.hash(refSmaller, refBigger);
    }
}
