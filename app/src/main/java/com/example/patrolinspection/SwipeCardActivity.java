package com.example.patrolinspection;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.patrolinspection.psam.BaoAnBasicInfo;
import com.example.patrolinspection.psam.CommonUtil;
import com.example.patrolinspection.psam.DBHelper;
import com.example.patrolinspection.psam.PsamUtil;

import java.io.IOException;

public class SwipeCardActivity extends AppCompatActivity
{
    private String type;
    private String title;

    private String TAG = "SwipeCardActivity";

    private Context mContext;
    private PsamUtil mPsamUtil;

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
        setContentView(R.layout.activity_swipe_card);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        //copy 保安数据库
        try {
            DBHelper.addDataBase(this);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"数据库连接失败");
        }
        initReadBak();

       Intent intent = getIntent();
//        this.setTitle(intent.getStringExtra("title")); 也可以
        title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        type = intent.getStringExtra("type");





        CardView swipeCard = findViewById(R.id.swipe_card);
        swipeCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void initReadBak(){
        try {
            mPsamUtil = new PsamUtil(mContext);
        } catch (Exception e) {
            e.printStackTrace();
            mPsamUtil = null;
        } catch (Error e) {
            e.printStackTrace();
            mPsamUtil = null;
        }
        if(mPsamUtil == null || mPsamUtil.open() < 0){
            Toast.makeText(mContext, "Psam初始化失败", Toast.LENGTH_SHORT).show();
            if (mPsamUtil != null) mPsamUtil.close();
        }
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
    protected void onDestroy() {
        super.onDestroy();

        if(mPsamUtil != null) mPsamUtil.close();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            if(mPsamUtil != null){
                processIntent(intent);
            } else {
                Toast.makeText(mContext, "Psam初始化失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processIntent(Intent intent) {
        if (isCardReading) {
            return;
        }
        isCardReading = true;

        mProgressDialog = ProgressDialog.show(mContext, "", "读卡中,请稍后...");
        final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        new Thread(){
            public void run() {
                //get TAG from intent
                String icid = CommonUtil.bytesToHexString(tagFromIntent.getId());
                for (String tech : tagFromIntent.getTechList()) {
                    Log.d(TAG,"tech = " + tech);
                    if(tech.contains("IsoDep")){
                        authType = CARD_TYPE_SECURITY;
                        break;
                    } else {
                        authType = CARD_TYPE_IC;
                    }
                }

                IsoDep isodep = IsoDep.get(tagFromIntent);
                int st = -3;
                String ret = "";
                try {
                    isodep.connect();
                    st = mPsamUtil.sercurityAuth(isodep);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (st==1){
                    byte[] basicInfoBytes = null;

                    byte[] expendInfoBytes = null;
                    byte[] trackInfoBytes = null;
                    byte[] photoInfoBytes = null;
                    basicInfoBytes = mPsamUtil.getBasicInfo(isodep);
                    photoInfoBytes = mPsamUtil.getPhotoInfo(isodep);
                    //读照片
                    Log.e(TAG, "photoInfoBytes.length = "+photoInfoBytes.length);
//                    if(photoInfoBytes != null){
//                        //BitmapImageFactory类需要右键项目Properties--Android--Library--Add... 添加ImageDecoderService项目
//                        BitmapImageFactory bif = BitmapImageFactory.get(photoInfoBytes);
//                        Bitmap bm = null;
//                        if(bif != null){
//                            bm = bif.getImage();
//                            if(bm != null){
//                                Matrix matrix = new Matrix();
//                                matrix.postScale(2.0f,2.0f); //长和宽放大缩小的比例
//                                final Bitmap resizeBmp = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),matrix,true);
//
//                                runOnUiThread(new Runnable()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//                                        imgPhoto.setImageBitmap(resizeBmp);
//                                    }
//                                });
//
//                            }
//                        }
//                    }
                    //读基础数据
                    if (basicInfoBytes != null){
                        try {
                            BaoAnBasicInfo info = mPsamUtil.resolveBasicInfo(basicInfoBytes);
                            Log.e(TAG,info.toString());
                            //expendInfoBytes = mPsamUtil.getJGInfo(isodep);
                            //trackInfoBytes = mPsamUtil.getTrackInfo(isodep);
//                            mName = info.getBaoAnName().replace(" ", "");
//                            mIdentityCard = info.getId();
//                            mSecurityCard = info.getBaoAnID();
//                            String sex = info.getSex();
//                            String nation = info.getNation();
//                            String fzjg = info.getFzjg();
//                            String fzrq = info.getFzrq();

                        } catch (Exception e) {
                            e.printStackTrace();
                            ret = "卡数据解析错误";
                        }
                    }
                } else if(st==-1) {
                    ret = "无PSAM卡";
                    //Toast.makeText(mContext, "无PSAM卡", Toast.LENGTH_SHORT).show();
                } else if(st==-2){
                    ret = "无效卡";
                    //Toast.makeText(mContext, "无效卡", Toast.LENGTH_SHORT).show();
                } else if(st==-3){
                    ret = "未读到保安卡id，请重新读卡";
                    //Toast.makeText(mContext, "未读到保安卡id，请重新读卡", Toast.LENGTH_SHORT).show();
                } else {
                    ret = "读卡失败";
                    //Toast.makeText(mContext, "读卡失败", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, ret);

                switch (type){
                    case "patrolInspection":{
                        Intent intent = new Intent(SwipeCardActivity.this, FaceRecognitionActivity.class);
                        intent.putExtra("schedule",getIntent().getStringExtra("schedule"));
                        startActivity(intent);
                        break;
                    }
                    case "signIn":
                    case "signOut":{
                        Intent intent = new Intent(SwipeCardActivity.this, SignInOutActivity.class);
                        intent.putExtra("type",type);
                        intent.putExtra("title",title);
                        intent.putExtra("attendanceType",getIntent().getStringExtra("attendanceType"));
                        startActivity(intent);
                        break;
                    }
                    case "eventFound":{
                        Intent intent = new Intent(SwipeCardActivity.this,EventFoundActivity.class);
                        intent.putExtra("type","normal");
                        startActivity(intent);
                        break;
                    }
                    case "securityStaff":{
                        Intent intent = new Intent(mContext, PoliceRegisterActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                }
//                if (authType == CARD_TYPE_SECURITY && !ret.equals("")){
//                    Message msg = mHandler.obtainMessage(1);
//                    msg.obj = ret;
//                    mHandler.sendMessage(msg);
//                } else {
//                    if (authType == CARD_TYPE_IC) {
//                        mSecurityCard = icid;
//                    }
//                    //上次巡检是否异常退出
//                    boolean hasException = wxxjPreferences.getBoolean(WXXJPreferences.HAS_EXCEPTION, false);
//                    if (hasException){
//                        //上次巡检异常退出，判断保安是否为同一人
//                        String sCard = wxxjPreferences.getString(WXXJPreferences.SECURITY_CARD, "");
//                        if(sCard.equals(mSecurityCard)){
//                            mIdentityCard = wxxjPreferences.getString(WXXJPreferences.IDENTITY_CARD, "");
//                            //进入巡检列表
//                            Intent i = new Intent(mContext, XjlbActivity.class);
//                            i.putExtra("name", mName);
//                            i.putExtra("securityCard", mSecurityCard);
//                            i.putExtra("identityCard", mIdentityCard);
//                            i.putExtra("authType", authType);
//                            i.putExtra("planline", mPlanline);
//                            startActivity(i);
//                        } else {
//                            Toast.makeText(mContext, "不是上次巡检的保安员！", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        //是否要人脸识别
//                        boolean faceAuth = wxxjPreferences.getBoolean(WXXJPreferences.FACE_AUTH, false);
//                        if (faceAuth){
//                            Intent i = new Intent(mContext, FaceAuthActivity.class);
//                            i.putExtra("securityCard", mSecurityCard);
//                            i.putExtra("authType", authType);
//                            startActivityForResult(i, 101);
//                        } else {
//                            //读到保安数据传到UserAuthShowActivity，由UserAuthShowActivity判断保安是否合法
//                            Intent i = new Intent(mContext, UserAuthShowActivity.class);
//                            //线路数据
//                            i.putExtra("planline", mPlanline);
//                            //保安数据
//                            i.putExtra("name", mName);
//                            i.putExtra("securityCard", mSecurityCard);
//                            i.putExtra("identityCard", mIdentityCard);
//                            i.putExtra("authType", authType);
//                            i.putExtra("isStart", isStart);
//                            startActivity(i);
//                        }
//                    }
//                }

                mProgressDialog.dismiss();
                isCardReading = false;

            };
        }.start();
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
