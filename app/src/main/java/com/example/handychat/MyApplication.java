package com.example.handychat;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
