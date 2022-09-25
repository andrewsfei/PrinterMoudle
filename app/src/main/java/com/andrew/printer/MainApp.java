package com.andrew.printer;

import android.app.Application;
import android.content.Context;


public class MainApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
