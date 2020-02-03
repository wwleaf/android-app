package me.aflak.leaf.main.dagger;

import javax.inject.Singleton;

import dagger.Component;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.graph.GraphService;
import me.aflak.leaf.main.view.MainActivity;

@Singleton
@Component(modules = {MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
