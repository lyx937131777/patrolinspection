package com.example.patrolinspection.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HeartbeatService extends Service
{
    public static boolean isRun = false;

    public HeartbeatService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        LogUtil.e("HeartbeatService","onCreat");
        isRun = true;
        new Thread(){
            public void run(){
                while(isRun){
                    try
                    {
                        TimeUnit.MINUTES.sleep(2);
                        //TODO 心跳
                        heartBeat();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }.start();
    }

    private void heartBeat(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = pref.getString("userID",null);
        String address = HttpUtil.LocalAddress + "/api/equipment/heartbeat";
        HttpUtil.heartbeatRequest(address, userID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("HeartbeatService",responsData);
                //TODO 处理心跳
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        isRun = true;
        LogUtil.e("HeartbeatService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isRun = false;
        LogUtil.e("HeartbeatService","onDestroy");
    }
}
