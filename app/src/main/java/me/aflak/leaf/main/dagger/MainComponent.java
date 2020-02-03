package me.aflak.leaf.main.dagger;

import javax.inject.Singleton;

import dagger.Component;
import me.aflak.leaf.main.view.MainActivity;

@Singleton
@Component(modules = {MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
