package me.aflak.leaf.graph;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GraphModule {
    @Provides @Singleton
    public GraphManager provideGraphService(SharedPreferences sharedPreferences, Gson gson) {
        return new GraphManager(sharedPreferences, gson);
    }
}
