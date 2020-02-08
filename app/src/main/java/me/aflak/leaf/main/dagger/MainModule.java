package me.aflak.leaf.main.dagger;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.aflak.leaf.graph.GraphManager;
import me.aflak.leaf.main.interactor.MainInteractor;
import me.aflak.leaf.main.interactor.MainInteractorImpl;
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
    MainView provideMainView() {
        return view;
    }

    @Provides @Singleton
    MainInteractor provideMainInteractor(GraphManager graphManager, UserManager userManager) {
        return new MainInteractorImpl(graphManager, userManager);
    }

    @Provides @Singleton
    MainPresenter provideMainPresenter(MainView view, MainInteractor interactor) {
        return new MainPresenterImpl(view, interactor);
    }

    @Provides @Singleton
    UserManager provideUserManager(SharedPreferences sharedPreferences) {
        return new UserManager(sharedPreferences);
    }

    @Provides @Singleton
    ChatFragment provideChatFragment() {
        return new ChatFragment();
    }
}
