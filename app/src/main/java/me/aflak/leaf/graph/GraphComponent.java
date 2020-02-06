package me.aflak.leaf.graph;

import javax.inject.Singleton;

import dagger.Component;
import me.aflak.leaf.app.AppModule;
import me.aflak.leaf.main.interactor.MainInteractor;

@Singleton
@Component(modules = {AppModule.class, GraphModule.class})
public interface GraphComponent {
    void inject(MainInteractor interactor);
}
