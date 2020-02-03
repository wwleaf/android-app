package me.aflak.leaf;

import android.app.Application;

import me.aflak.leaf.app.AppComponent;
import me.aflak.leaf.app.AppModule;
import me.aflak.leaf.app.DaggerAppComponent;

public class MyApplication extends Application {
    private AppModule appModule;
    private static MyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appModule = new AppModule(this);
    }

    public static MyApplication getApp() {
        return app;
    }

    public AppModule getAppModule() {
        return appModule;
    }
}
