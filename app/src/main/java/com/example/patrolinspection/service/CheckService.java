package com.example.patrolinspection.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//10秒检查一次数据合理性的服务 保障本地数据稳定
public class CheckService extends Service
{
    public static final int SLEEP_TIME = 10;
    public static boolean isRun = false;
    public CheckService()
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
                        TimeUnit.SECONDS.sleep(SLEEP_TIME);
                        check();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }.start();
    }

    private void check(){
        checkSchedule();
    }

    private void checkSchedule(){
        List<PatrolSchedule> patrolScheduleList = LitePal.findAll(PatrolSchedule.class);
        List<String> idList = new ArrayList<>();
        for(PatrolSchedule patrolSchedule : patrolScheduleList){
            String id = patrolSchedule.getInternetID();
            if(idList.contains(id)){
                patrolSchedule.delete();
            }else{
                idList.add(id);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        isRun = true;
        LogUtil.e("CheckService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isRun = false;
        LogUtil.e("CheckService","onDestroy");
    }
}
