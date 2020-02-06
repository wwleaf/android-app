package me.aflak.leaf.main.dagger;

import javax.inject.Singleton;

import dagger.Component;
import me.aflak.leaf.app.AppModule;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.main.view.MainActivity;

@Singleton
@Component(modules = {AppModule.class, GraphModule.class, MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
