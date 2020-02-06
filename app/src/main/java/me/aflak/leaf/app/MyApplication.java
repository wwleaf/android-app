package me.aflak.leaf.app;

import android.app.Application;

public class MyApplication extends Application {
    private AppModule appModule;
    private static MyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appModule = new AppModule(getApplicationContext());
    }

    public static MyApplication getApp() {
        return app;
    }

    public AppModule getAppModule() {
        return appModule;
    }
}
