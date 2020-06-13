package com.example.patrolinspection;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.patrolinspection.adapter.TypeAdapter;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.Type;
import com.example.patrolinspection.service.CheckService;
import com.example.patrolinspection.service.HeartbeatService;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private Type[] types = {new Type("巡检", R.drawable.patrol_inspection,R.drawable.patrol_inspection_press,"patrolInspection"),
            new Type("发现异常", R.drawable.event_found,R.drawable.event_found_press,"eventFound"),
            new Type("异常事件列表", R.drawable.event_list,R.drawable.event_list_press,"eventList"),
            new Type("公告", R.drawable.notice,R.drawable.notice_press,"notice"),
            new Type("数据更新", R.drawable.data_updating,R.drawable.data_updating_press,"dataUpdating"),
            new Type("系统参数", R.drawable.system_parameter,R.drawable.system_parameter_press,"systemParameter"),
            new Type("签到/签退", R.drawable.sign,R.drawable.sign_press,"sign"),
            new Type("保安/信息点注册",R.drawable.information_register,R.drawable.information_register_press,"informationRegister"),
            new Type("护校事件",R.drawable.school_event,R.drawable.school_event_press,"schoolEvent"),
            new Type("护校事件",R.drawable.school_event_new,R.drawable.school_event_press,"schoolEvent"),
            new Type("公告", R.drawable.notice_new,R.drawable.notice_press,"notice"),
            };
    private List<Type> typeList = new ArrayList<>();
    private TypeAdapter adapter;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        initTypes();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TypeAdapter(typeList);
        recyclerView.setAdapter(adapter);

        //TODO 测试时间
        Date date1 = new Date();
        Date date2 = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        Date date3 = new Date(calendar.getTimeInMillis());
        LogUtil.e("MainActivity","date1: " + Utility.dateToString(date1,"yyyy-MM-dd HH:mm:ss")+ "   long: "+date1.getTime());
        LogUtil.e("MainActivity","date2: " + Utility.dateToString(date2,"yyyy-MM-dd HH:mm:ss")+ "   long: "+date2.getTime());
        LogUtil.e("MainActivity","date3: " + Utility.dateToString(date3,"yyyy-MM-dd HH:mm:ss")+ "   long: "+date3.getTime());

        String time = Utility.dateToString(date1,"yyyy-MM-dd")+" 10:00";
        Date date4 = Utility.stringToDate(time,"yyyy-MM-dd HH:mm");
        LogUtil.e("MainActivity","date4: " + Utility.dateToString(date4,"yyyy-MM-dd HH:mm:ss") + "   long: "+date4.getTime());

//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setTime(date4.getTime());
//        SystemClock.setCurrentTimeMillis(date4.getTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (!hasPermission())
            {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    private void initTypes()
    {
        typeList.clear();
        //DataSupport.deleteAll(Type.class);
        for (int i = 0; i < 3; i++)
        {
            typeList.add(types[i]);
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean("newNotice",false)){
            typeList.add(types[10]);
        }else{
            typeList.add(types[3]);
        }
        for (int i = 4; i < 6; i++)
        {
            typeList.add(types[i]);
        }
        if(!pref.getString("equipmentType",null).equals("phone") || pref.getBoolean("isAppAttendance",false)){
            typeList.add(types[6]);
        }
        if(!pref.getString("equipmentType",null).equals("phone")){
            typeList.add(types[7]);
        }

        LogUtil.e("MainActivity","schoolLogin: " + pref.getBoolean("isSchoolLogin",false));
        if(pref.getBoolean("isSchoolLogin",false)){
            if(pref.getBoolean("newSchoolEvent",false)){
                typeList.add(types[9]);
            }else {
                typeList.add(types[8]);
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        refresh();
        if(!HeartbeatService.isRun){
            Intent intent = new Intent(this,HeartbeatService.class);
            startService(intent);
        }
        if(!CheckService.isRun){
            Intent intent = new Intent(this,CheckService.class);
            startService(intent);
        }
        PatrolRecord patrolRecord = LitePal.where("state = ?","进行中").findFirst(PatrolRecord.class);
        if(patrolRecord != null){
            Intent intent = new Intent(this, PatrolingActivity.class);
            intent.putExtra("record",patrolRecord.getInternetID());
            startActivityForResult(intent,0);
        }
    }

    public void refresh()
    {
        initTypes();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LogUtil.e("MainActivity","onDestroy");
        Intent intent = new Intent(this, HeartbeatService.class);
        stopService(intent);
        Intent intent2 = new Intent(this, CheckService.class);
        stopService(intent2);
        LogUtil.e("MainActivity","onDestroy22222");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS)
        {
            if (!hasPermission())
            {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                Toast.makeText(this, "请打开权限！", Toast.LENGTH_SHORT).show();
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }


    //检测用户是否对本app开启了“Apps with usage access”权限
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermission()
    {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
        {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    public void onBackPressed()
    {

    }
}
