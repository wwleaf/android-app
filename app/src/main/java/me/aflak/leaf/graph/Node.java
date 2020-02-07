package me.aflak.leaf.graph;

import java.util.Objects;

import androidx.annotation.Nullable;

public class Node {
    private int id;
    private String name;

    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Node(int id) {
        this.id = id;
        this.name = "unnamed";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        Node n = (Node) obj;
        return id == n.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
