package com.example.patrolinspection;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.psam.CommonUtil;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//只刷NFC的界面
public class SwipeNfcActivity extends AppCompatActivity
{
    private String type;
    private String title;

    private String TAG = "SwipeNfcActivity";

    private Context mContext;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private IntentFilter ndef;

    private ProgressDialog mProgressDialog;
    private boolean isCardReading = false;

    private int authType = CARD_TYPE_IC;
    /**保安卡*/
    public static final int CARD_TYPE_SECURITY = 1;
    /**IC卡*/
    public static final int CARD_TYPE_IC = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_nfc);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContext = this;
        initReadBak();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        type = intent.getStringExtra("type");
    }

    private void initReadBak(){
        //NFC适配器，所有的关于NFC的操作从该适配器进行
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(!ifNFCUse()){
            finish();
            return;
        }
        //将被调用的Intent，用于重复被Intent触发后将要执行的跳转
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //设定要过滤的标签动作，这里只接收ACTION_NDEF_DISCOVERED类型
        ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ndef.addCategory("*/*");
        mFilters = new IntentFilter[] { ndef };// 过滤器
        mTechLists = new String[][] {
                new String[] { NfcA.class.getName() },
                new String[] { IsoDep.class.getName() }
        };// 允许扫描的标签类型
    }


    private boolean ifNFCUse() {
        if (nfcAdapter == null) {
            Log.e(TAG, "设备不支持NFC！");
            Toast.makeText(mContext, "此设备没有NFC功能", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
            Log.e(TAG,"请在系统设置中先启用NFC功能！");
            Toast.makeText(mContext, "请打开NFC", Toast.LENGTH_SHORT).show();
            startActivity(new Intent("android.settings.NFC_SETTINGS"));
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 前台分发系统,这里的作用在于第二次检测NFC标签时该应用有最高的捕获优先权.
        if (nfcAdapter != null)nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null)nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
           processIntentByNfc(intent);
        }
    }
    private void processIntentByNfc(Intent intent)
    {
        if (isCardReading) {
            return;
        }
        isCardReading = true;

        mProgressDialog = ProgressDialog.show(mContext, "", "读卡中,请稍后...");
        final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        new Thread(){
            public void run() {
                //get TAG from intent
                final String icid = CommonUtil.bytesToHexString(tagFromIntent.getId());
                LogUtil.e("SwipeCardActivity","icid: " + icid);
                for (String tech : tagFromIntent.getTechList()) {
                    Log.d(TAG,"tech = " + tech);
                    if(tech.contains("IsoDep")){
                        authType = CARD_TYPE_SECURITY;
                        break;
                    } else {
                        authType = CARD_TYPE_IC;
                    }
                }
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    String address = HttpUtil.LocalAddress + "/api/police/cardNo";
                    String userID = preferences.getString("userID",null);
                    HttpUtil.findPoliceByIcCardRequest(address, userID, icid, new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "服务器连接错误", Toast
                                            .LENGTH_LONG).show();
                                }
                            });
                            mProgressDialog.dismiss();
                            isCardReading = false;
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException
                        {
                            final String responseData = response.body().string();
                            LogUtil.e("SwipeNfcActivity",responseData);
                            if(Utility.checkString(responseData,"code").equals("500")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "IC卡号："+icid+"，尚未注册！", Toast
                                                .LENGTH_LONG).show();
                                    }
                                });
                                mProgressDialog.dismiss();
                                isCardReading = false;
                            }else{
                                LitePal.deleteAll(Police.class,"icCardNo = ?",icid);
                                Police internetPolice = Utility.handlePolice(responseData);
                                internetPolice.save();
                                processPolice(internetPolice);
                            }
                        }
                    });

            };
        }.start();
    }

    private void processPolice(final Police police){
        LogUtil.e("SwipeNfcActivity","id: "+police.getInternetID());
        LogUtil.e("SwipeNfcActivity","name: "+police.getRealName());
        LogUtil.e("SwipeNfcActivity","companyId: "+police.getCompanyId());
        LogUtil.e("SwipeNfcActivity","icid: "+police.getIcCardNo());
        LogUtil.e("SwipeNfcActivity","duty: "+police.getMainDutyId() + "  " + MapUtil.getDuty(police.getMainDutyId()));
        if(!police.isInService()){
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(mContext,"该保安已离职！",Toast.LENGTH_LONG).show();
                }
            });
            mProgressDialog.dismiss();
            isCardReading = false;
            return;
        }
        switch (type){
            case "patrolInspection":{
               if(!police.isOnDuty()){
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(mContext,"该保安尚未签到！",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    String scheduleID = getIntent().getStringExtra("schedule");
                    PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",scheduleID).findFirst(PatrolSchedule.class);
                    PatrolPlan patrolPlan = LitePal.where("internetID = ?",patrolSchedule.getPatrolPlanId()).findFirst(PatrolPlan.class);
                    if(patrolPlan.getPatrolPlanType().equals("specialDate")){
                        Date now = new Date(System.currentTimeMillis());
                        Date endDate = TimeUtil.stringToDate(patrolPlan.getEndDate());
                        if(now.after(endDate)){
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(mContext,"已过24：00点，请先返回主界面再开始巡检。",Toast.LENGTH_LONG).show();
                                }
                            });
                            mProgressDialog.dismiss();
                            isCardReading = false;
                            break;
                        }
                    }else if(!patrolPlan.getPatrolPlanType().equals("freeSchedule")){
                        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        LogUtil.e("PatrolLineActivity","week: "+week);
                        PatrolPlan patrolPlan2 = LitePal.where("patrolPlanType = ?", MapUtil.getPlanType(String.valueOf(week))).findFirst(PatrolPlan.class);
                        if(patrolPlan2 == null){
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(mContext,"已过24：00点，请先返回主界面再开始巡检。",Toast.LENGTH_LONG).show();
                                }
                            });
                            mProgressDialog.dismiss();
                            isCardReading = false;
                            break;
                        }
                        String planID = patrolPlan2.getInternetID();
                        if(!planID.equals(patrolSchedule.getPatrolPlanId())){
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(mContext,"已过24：00点，请先返回主界面再开始巡检。",Toast.LENGTH_LONG).show();
                                }
                            });
                            mProgressDialog.dismiss();
                            isCardReading = false;
                            break;
                        }
                    }

                    String lineID = patrolSchedule.getPatrolLineId();
                    PatrolLine patrolLine = LitePal.where("internetID = ?",lineID).findFirst(PatrolLine.class);
                    if(patrolLine.getPoliceIds() != null && patrolLine.getPoliceIds().contains(police.getInternetID())){
                        if(patrolLine.getPatrolLineType().equals("publicSecurity") && (!police.isOfficialPolice())){
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(mContext,"自建保安无法巡检治安巡检线路",Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Intent intent = new Intent(mContext, FaceRecognitionActivity.class);
                            intent.putExtra("schedule",getIntent().getStringExtra("schedule"));
                            intent.putExtra("police",police.getInternetID());
                            startActivityForResult(intent,0);
                        }
                    }else{
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(mContext,"该保安无法巡检此线路",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                mProgressDialog.dismiss();
                isCardReading = false;
                break;
            }
            case "signIn":
            case "signOut":{
                Intent intent = new Intent(mContext, SignInOutActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("title",title);
                intent.putExtra("attendanceType",getIntent().getStringExtra("attendanceType"));
                intent.putExtra("police",police.getInternetID());
                startActivity(intent);
                finish();
                mProgressDialog.dismiss();
                isCardReading = false;
                break;
            }
            case "eventFound":{
                if(!police.isOnDuty()){
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(mContext,"该保安尚未签到！",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Intent intent = new Intent(mContext,EventFoundActivity.class);
                    intent.putExtra("type","normal");
                    intent.putExtra("police",police.getInternetID());
                    startActivity(intent);
                    finish();
                }
                mProgressDialog.dismiss();
                isCardReading = false;
                break;
            }
            case "eventHandle":{
                if(!police.isOnDuty()){
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(mContext,"该保安尚未签到！",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Intent intent = new Intent(mContext,EventHandleActivity.class);
                    intent.putExtra("eventRecord",getIntent().getStringExtra("eventRecord"));
                    intent.putExtra("police",police.getInternetID());
                    startActivityForResult(intent,0);
                }
                mProgressDialog.dismiss();
                isCardReading = false;
                break;
            }
            case "schoolEvent":{
                if(police.getSchoolAttendanceSchoolId() == null){
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(mContext,"请先进行护校签到！",Toast.LENGTH_LONG).show();
                        }
                    });
                    mProgressDialog.dismiss();
                    isCardReading = false;
                }else{
                    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    String userID = preferences.getString("userID",null);
                    String address = HttpUtil.LocalAddress + "/api/equipment/school_login";
                    HttpUtil.schoolLoginRequest(address, userID, police.getInternetID(), new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e)
                        {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "服务器连接错误", Toast
                                            .LENGTH_LONG).show();
                                }
                            });
                            mProgressDialog.dismiss();
                            isCardReading = false;
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException
                        {
                            final String responseData = response.body().string();
                            LogUtil.e("SwipeNfcActivity",responseData);
                            if(Utility.checkString(responseData,"code").equals("500")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, Utility.checkString(responseData,"msg"), Toast.LENGTH_LONG).show();
                                    }
                                });
                                mProgressDialog.dismiss();
                                isCardReading = false;
                            }else{
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("schoolPolice",police.getInternetID());
                                editor.apply();
                                Intent intent = new Intent(mContext,SchoolEventActivity.class);
                                startActivity(intent);
                                finish();
                                mProgressDialog.dismiss();
                                isCardReading = false;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
