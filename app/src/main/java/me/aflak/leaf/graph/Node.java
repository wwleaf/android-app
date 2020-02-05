package me.aflak.leaf.graph;

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
}
