package com.example.patrolinspection.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

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
    private static final String TAG = "HeartbeatService";
    public static boolean isRun = false;
    public static final String  MY_APP = "com.example.patrolinspection";
    public static final int SLEEP_TIME = 2;
    private String topActivity = "com.example.patrolinspection";
    private int heartbeat;
    private int heartbeatWork;
    private int time;
    private int countTime;
    private boolean isScreenOff;
    private SharedPreferences pref;

    //广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case Intent.ACTION_SCREEN_OFF:
                        LogUtil.e(TAG, "屏幕关闭，变黑");
                        isScreenOff = true;
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        LogUtil.e(TAG, "屏幕开启，变亮");
                        isScreenOff = false;
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        LogUtil.e(TAG, "解锁成功");
                        isScreenOff = false;
                        break;
                    default:
                        break;
                }
            }
        }
    };

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
        //注册广播 获得屏幕亮暗信息
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));

        isRun = true;
        countTime = 0;
        isScreenOff = false;
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        heartbeat = pref.getInt("heartbeat",2);
        heartbeatWork = pref.getInt("heartbeatWork",60);

        new Thread(){
            public void run(){
                while(isRun){
                    try
                    {
                        getTopApp();
                        TimeUnit.MINUTES.sleep(SLEEP_TIME);
                        //TODO 心跳
                        countTime += SLEEP_TIME;
                        if(countTime >= time){
                            countTime = 0;
                            heartBeat();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }.start();
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
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
        isRun = false;
        LogUtil.e("HeartbeatService","onDestroy");
    }
    private void getTopApp()
    {
        if(isScreenOff){
            time = heartbeat;
            LogUtil.e("HeartbeatService","黑屏了");
            return;
        }
        LogUtil.e("HeartbeatService","亮屏中");
        LogUtil.e("HeartbeatService",""+Build.VERSION.SDK_INT + "     "+Build.VERSION_CODES.LOLLIPOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            UsageStatsManager m = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null)
            {
                long now = System.currentTimeMillis();
                //获取3秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                        now - 3 * 1000, now);

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty()))
                {
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

    private void heartBeat(){
        String userID = pref.getString("userID",null);
        String address = HttpUtil.LocalAddress + "/api/equipment/heartbeat";
        HttpUtil.heartbeatRequest(address, userID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("HeartbeatService",responsData);
                //TODO 处理心跳 line schedule point plan event
                List<String> updateList = Utility.handleUpdateList(responsData);
                if(updateList.contains("plan")){
                    updatePatrolPlan();
                }
                if(updateList.contains("schedule")){
                    updatePatrolSchedule();
                }
                if(updateList.contains("line")){
                    updatePatrolLine();
                }
                if(updateList.contains("point")){
                    updateInformationPoint();
                }
                if(updateList.contains("event")){
                    updateEvent();
                }
            }
        });
    }


    public void updatePatrolSchedule(){
        String address = HttpUtil.LocalAddress + "/api/schedule/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingSchedule",responsData);
                List<PatrolSchedule> patrolScheduleList = Utility.handlePatrolScheduleList(responsData);
                LitePal.deleteAll(PatrolSchedule.class);
                if(patrolScheduleList != null && patrolScheduleList.size() > 0){
                    for(PatrolSchedule patrolSchedule : patrolScheduleList){
                        String lineID = patrolSchedule.getPatrolLineId();
                        List<PatrolLine> patrolLineList = LitePal.where("internetID = ?",lineID).find(PatrolLine.class);
                        if(patrolLineList.size() > 0){
                            patrolSchedule.save();
                        }
                    }
                }
            }
        });
    }


    public void updatePatrolPlan(){
        String address = HttpUtil.LocalAddress + "/api/plan/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingPlan",responsData);
                List<PatrolPlan> patrolPlanList = Utility.handlePatrolPlanList(responsData);
                LitePal.deleteAll(PatrolPlan.class);
                LitePal.saveAll(patrolPlanList);
            }
        });
    }

    public void updateInformationPoint(){
        String address = HttpUtil.LocalAddress + "/api/point/all";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingInformationPoint",responsData);
                List<InformationPoint> informationPointList  = Utility.handleInformationPointList(responsData);
                LitePal.deleteAll(InformationPoint.class);
                LitePal.saveAll(informationPointList);
            }
        });
    }

    public void updatePatrolLine(){
        String address = HttpUtil.LocalAddress + "/api/line/byEquipment";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingByEquipmentRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("DataUpdatingPatrolLine",responsData);
                List<PatrolLine> patrolLineList = Utility.handlePatrolLineList(responsData);
                LitePal.deleteAll(PatrolLine.class);
                LitePal.deleteAll(PatrolIP.class);
//                LitePal.saveAll(patrolLineList);
                for(PatrolLine patrolLine : patrolLineList){
                    if(patrolLine.getPointLineModels() != null){
                        for(PatrolIP patrolIP: patrolLine.getPointLineModels()){
                            patrolIP.setPatrolLineID(patrolLine.getInternetID());
                            LogUtil.e("DataUpdatingPatrolLine",patrolIP.getPatrolLineID() + " " + patrolIP.getOrderNo() + " " + patrolIP.getPointId());
                            patrolIP.save();
                        }
                    }
                    patrolLine.save();
                }
                updatePatrolSchedule();
            }
        });
    }

    public void updateEvent(){
        String address = HttpUtil.LocalAddress + "/api/event/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e(TAG,responsData);
                List<Event> eventList = Utility.handleEventList(responsData);
                LitePal.deleteAll(Event.class);
                LitePal.saveAll(eventList);
            }
        });
    }
}
