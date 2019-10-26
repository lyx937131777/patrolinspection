package com.example.patrolinspection.service;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HeartbeatService extends Service
{
    public static boolean isRun = false;
    public static final String  MY_APP = "com.example.patrolinspection";
    private String topActivity = "com.example.patrolinspection";
    private int heartbeat;
    private int heartbeatWork;
    private int time;

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
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        heartbeat = pref.getInt("heartbeat",2);
        heartbeatWork =pref.getInt("heartbeatWork",60);
        new Thread(){
            public void run(){
                while(isRun){
                    try
                    {
                        getTopApp();
                        TimeUnit.MINUTES.sleep(time);
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

    private void getTopApp()
    {
        LogUtil.e("HeartbeatService",""+Build.VERSION.SDK_INT + "     "+Build.VERSION_CODES.LOLLIPOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            UsageStatsManager m = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null)
            {
                LogUtil.e("HeartbeatService","xixixixiixixixi");
                long now = System.currentTimeMillis();
                //获取3秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                        now - 3 * 1000, now);

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty()))
                {
                    LogUtil.e("HeartbeatService","hahhahahahha");
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++)
                    {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed())
                        {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                }
                LogUtil.e("HeartbeatService", "top running app is : " + topActivity);

            }
        }
        if(topActivity.equals(MY_APP)){
            time = heartbeatWork;
        }else{
            time = heartbeat;
        }
        LogUtil.e("HeartbeatService","time: "+time);
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
