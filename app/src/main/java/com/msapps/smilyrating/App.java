package com.msapps.smilyrating;

import android.app.Application;

/**
 * Created by sujith on 15/10/16.
 */
public class App extends Application {

    private static final String TAG = "App";

    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static App getInstance() {
        return sApp;
    }
}
