package me.aflak.leaf.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context context;

    AppModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    Context provideContext() {
        return context;
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides @Singleton
    Gson provideGson() {
        return new Gson();
    }
}
