package com.example.patrolinspection.service;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.patrolinspection.MainActivity;
import com.example.patrolinspection.NoticeActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.SchoolEventActivity;
import com.example.patrolinspection.SystemParameterActivity;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.PointPhotoRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MyApplication;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
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
    private boolean download;

    private int photoCount;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case 1:
                {
                    AlertDialog.Builder builder =new AlertDialog.Builder(getApplicationContext()).setTitle
                            ("提示").setMessage("上海巡检APP发现有新版本，是否立刻更新？")
                            .setNeutralButton("否", new DialogInterface
                                    .OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("是", new DialogInterface
                            .OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent(HeartbeatService.this, SystemParameterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.show();
                    break;
                }
            }
        }
    };

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
        photoCount = 0;
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
                if(updateList.contains("announce")){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("newNotice",true);
                    editor.apply();
                    String CHANNEL_ID = "channel_id_1";
                    String CHANNEL_NAME = "channel_notice";
                    Intent intent = new Intent(HeartbeatService.this, NoticeActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(HeartbeatService.this, 0, intent, 0);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        //只在Android O之上需要渠道
                        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                        //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
                        //通知才能正常弹出
                        manager.createNotificationChannel(notificationChannel);
                    }
                    Notification notification = new NotificationCompat.Builder(HeartbeatService.this, CHANNEL_ID)
                            .setContentTitle("新公告提醒")
                            .setContentText("有新的公告，请及时查看")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.logo)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();
                    manager.notify(1,notification);
                    if(getApplicationContext() instanceof MainActivity){
                        ((MainActivity)getApplicationContext()).refresh();
                    }
                }
                if(Utility.checkHeartbeatBoolean(responsData,"schoolEvent")){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("newSchoolEvent",true);
                    editor.apply();
                    String CHANNEL_ID = "channel_id_2";
                    String CHANNEL_NAME = "channel_school_event";
                    Intent intent = new Intent(HeartbeatService.this, SchoolEventActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(HeartbeatService.this, 0, intent, 0);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        //只在Android O之上需要渠道
                        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                        //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
                        //通知才能正常弹出
                        manager.createNotificationChannel(notificationChannel);
                    }
                    Notification notification = new NotificationCompat.Builder(HeartbeatService.this, CHANNEL_ID)
                            .setContentTitle("新护校事件提醒")
                            .setContentText("有新的护校事件，请及时查看")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.logo)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();
                    manager.notify(2,notification);
                    if(getApplicationContext() instanceof MainActivity){
                        ((MainActivity)getApplicationContext()).refresh();
                    }
                }

                if(!Utility.checkHeartbeatBoolean(responsData, "schoolLogin")){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("schoolPolice","null");
                    editor.apply();
                }
                PackageManager manager = getPackageManager();
                String version = "未知";
                try {
                    PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                    version = info.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String latestVersion = Utility.checkHeartbeatString(responsData,"versionNo");
                LogUtil.e(TAG,version);
                LogUtil.e(TAG,latestVersion);
                if(!latestVersion.equals(version)){
                    LogUtil.e(TAG,"有新版需要更新");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("latestVersion",latestVersion);
                    editor.putString("latestVersionDownloadUrl",Utility.checkHeartbeatString(responsData,"versionPath"));
                    editor.apply();
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        });
        uploadPatrolRecordPhoto();
        uploadEventRecord();
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

    public void uploadPatrolRecordPhoto(){
        List<PatrolRecord> patrolRecordList = LitePal.where("upload = ?","0").find(PatrolRecord.class);
        for(PatrolRecord patrolRecord : patrolRecordList){
            final String patrolRecordID = patrolRecord.getInternetID();
            List<PatrolPointRecord> patrolPointRecordList = LitePal.where("patrolRecordId = ?",patrolRecordID).order("time").find(PatrolPointRecord.class);
            for(final PatrolPointRecord patrolPointRecord : patrolPointRecordList){
                for(final PointPhotoRecord pointPhotoRecord : patrolPointRecord.getPointPhotoInfos()){
                    if(pointPhotoRecord.getPhotoURL().equals("")){
                        LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   ++");
                        photoCount++;
                        String address = HttpUtil.LocalAddress + "/api/file";
                        final String userID = pref.getString("userID",null);
                        HttpUtil.fileRequest(address, userID, new File(pointPhotoRecord.getPhotoPath()), new Callback()
                        {
                            @Override
                            public void onFailure(Call call, IOException e)
                            {
                                e.printStackTrace();
                                LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   --");
                                photoCount--;
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException
                            {
                                final String responsData = response.body().string();
                                LogUtil.e("DataUpdatingPresenter",responsData);
                                String photo = Utility.checkString(responsData,"msg");
                                pointPhotoRecord.setPhotoURL(photo);
                                pointPhotoRecord.save();
                                patrolPointRecord.save();
                                LogUtil.e("DataUpdatingPresenter","photoCount: "+photoCount+ "   --");
                                photoCount--;
                                if(photoCount == 0){
                                    uploadPatrolRecord();
                                }
                            }
                        });
                    }
                }
            }
        }
        if(photoCount == 0){
            uploadPatrolRecord();
        }
    }

    public void uploadPatrolRecord(){
        List<PatrolRecord> patrolRecordList = LitePal.where("upload = ?","0").find(PatrolRecord.class);
        for(PatrolRecord patrolRecord : patrolRecordList) {
            final String patrolRecordID = patrolRecord.getInternetID();
            String address = HttpUtil.LocalAddress + "/api/patrolRecord/put";
            String companyID = pref.getString("companyID",null);
            String userID = pref.getString("userID",null);
            HttpUtil.endPatrolRequest(address, userID, companyID, patrolRecordID, new Callback()
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
                    LogUtil.e("DataUpdatingPresenter",responsData);
                    PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
                    patrolRecord.setUpload(true);
                    patrolRecord.save();
                }
            });
        }
    }

    public void uploadEventRecord(){
        List<EventRecord> eventRecordList = LitePal.where("upload = ?","0").find(EventRecord.class);
        for(final EventRecord eventRecord : eventRecordList){
            if(!eventRecord.getPhotoPath().equals("") && eventRecord.getPhotoURL().equals("")){
                String address = HttpUtil.LocalAddress + "/api/file";
                final String userID = pref.getString("userID",null);
                HttpUtil.fileRequest(address, userID, new File(eventRecord.getPhotoPath()), new Callback()
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
                        LogUtil.e("EventFoundPresenter",responsData);
                        String photo = Utility.checkString(responsData,"msg");
                        eventRecord.setPhotoURL(photo);
                        eventRecord.save();
                        postEventRecord(eventRecord);
                    }
                });
            }else{
                postEventRecord(eventRecord);
            }
        }
    }

    public void postEventRecord(final EventRecord eventRecord){
        String userID = pref.getString("userID",null);
        String photo = eventRecord.getPhotoURL();
        String address = HttpUtil.LocalAddress + "/api/eventRecord";
        String companyID = pref.getString("companyID",null);
        String reportUnit = eventRecord.getReportUnit();
        String disposalOperateType = eventRecord.getDisposalOperateType();
        long time = eventRecord.getTime();
        String eventID = eventRecord.getEventId();
        String policeID = eventRecord.getPoliceId();
        String detail = eventRecord.getDetail();
        String patrolRecordID = eventRecord.getPatrolRecordId();
        String pointID = eventRecord.getPointId();
        HttpUtil.postEventRecordRequest(address, userID, companyID, eventID, policeID, reportUnit, detail, disposalOperateType,photo, time,
                patrolRecordID, pointID, new Callback()
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
                        LogUtil.e("EventFoundPresenter",responsData);
                        if(Utility.checkString(responsData,"code").equals("000")){
                            eventRecord.setUpload(true);
                            eventRecord.save();
                        }
                    }
                });
    }


}
