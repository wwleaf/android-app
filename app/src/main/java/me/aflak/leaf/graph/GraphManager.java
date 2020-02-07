package me.aflak.leaf.graph;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

public class GraphManager {
    private final static String PREF_KEY = "graph";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public GraphManager(SharedPreferences sharedPreferences, Gson gson) {
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
    }

    public void save(Graph graph) {
        Set<Edge> edges = graph.getEdges();
        sharedPreferences.edit()
                .putString(PREF_KEY, gson.toJson(edges))
                .apply();
    }

    public Graph load() {
        String json = sharedPreferences.getString(PREF_KEY, null);
        if (json != null) {
            Type listType = new TypeToken<Set<Edge>>(){}.getType();
            Set<Edge> edges = gson.fromJson(json, listType);
            Graph graph = new Graph();
            graph.addEdges(edges);
            return graph;
        }
        return new Graph();
    }
}
