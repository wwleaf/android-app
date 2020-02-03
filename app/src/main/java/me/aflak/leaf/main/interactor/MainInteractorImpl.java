package me.aflak.leaf.main.interactor;

import javax.inject.Inject;

import me.aflak.leaf.MyApplication;
import me.aflak.leaf.graph.DaggerGraphComponent;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.graph.GraphService;

public class MainInteractorImpl implements MainInteractor {
    @Inject GraphService graphService;

    public MainInteractorImpl() {
        DaggerGraphComponent.builder()
                .graphModule(new GraphModule())
                .appModule(MyApplication.getApp().getAppModule())
                .build().inject(this);
    }

    @Override
    public void processMessage(String message) {
    }
}
