package com.example.patrolinspection.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

public class MyApplication extends Application
{
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        LitePalApplication.initialize(context);
    }

    public static Context getContext()
    {
        return context;
    }
}
