package me.aflak.leaf.main.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.aflak.leaf.main.presenter.MainPresenter;
import me.aflak.leaf.main.presenter.MainPresenterImpl;
import me.aflak.leaf.main.view.ChatFragment;
import me.aflak.leaf.main.view.MainView;

@Module
public class MainModule {
    private MainView view;

    public MainModule(MainView view) {
        this.view = view;
    }

    @Provides @Singleton
    public MainView provideMainView() {
        return view;
    }

    @Provides @Singleton
    public MainPresenter provideMainPresenter(MainView view) {
        return new MainPresenterImpl(view);
    }

    @Provides @Singleton
    public ChatFragment provideChatFragment() {
        return new ChatFragment();
    }
}
