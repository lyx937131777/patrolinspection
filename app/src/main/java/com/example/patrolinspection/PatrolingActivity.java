package com.example.patrolinspection;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patrolinspection.adapter.InformationPointAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.presenter.PatrolingPresenter;
import com.example.patrolinspection.psam.CommonUtil;
import com.example.patrolinspection.util.FileUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.TimeUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatrolingActivity extends AppCompatActivity
{
    //photo
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String imagePath = null;

    //NFC
    private String TAG = "PatrolingActivity";
    private Context mContext;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private IntentFilter ndef;

    //recycleView
    private List<PatrolPointRecord> patrolPointRecordList = new ArrayList<>();
    private InformationPointAdapter adapter;
    private PatrolPointRecord tempPointRecord;

    //View控件
    private TextView ipCount;
    private TextView lineInformation;
    private Button eventFound;
    private Button eventHandle;
    private Button endPatrol;
    private Button patrolPhoto;

    //参数
    private String recordID;
    private PatrolRecord patrolRecord;
    private PatrolSchedule patrolSchedule;
    private String lineID;
    private PatrolLine patrolLine;

    //计数
    private int countAll;
    private int countPatrolled;

    //presenter
    private PatrolingPresenter patrolingPresenter;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patroling);
        LogUtil.e("PatrolingActivity","onCreate"+ TimeUtil.dateToString(new Date(),"HH:mm:ss"));

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        patrolingPresenter = myComponent.patrolingPresenter();

        //NFC
        mContext = this;
        initReadBak();

        //获取参数
        final Intent intent = getIntent();
        recordID = intent.getStringExtra("record");
        patrolRecord = LitePal.where("internetID = ?",recordID).findFirst(PatrolRecord.class);
        patrolSchedule = LitePal.where("internetID = ?",patrolRecord.getPatrolScheduleId()).findFirst(PatrolSchedule.class);
        lineID = patrolSchedule.getPatrolLineId();
        patrolLine = LitePal.where("internetID = ?",lineID).findFirst(PatrolLine.class);
        actionBar.setTitle(patrolLine.getPatrolLineName());

        //显示信息
        lineInformation = findViewById(R.id.line_information);
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String realStartTime =  format.format(new Date(patrolRecord.getStartTimeLong()));
        if(patrolSchedule.getPlanType().equals("freeSchedule")){
            stringBuilder.append("开始时间： " + realStartTime + "\n");
            stringBuilder.append("此巡检为自由排班,无时间限制。");
        }else{
            String startTime =patrolSchedule.getStartTime();
            String endTime = patrolSchedule.getEndTime();
            int duringTime = patrolSchedule.getDuringMin();
            String realEndTime = format.format(new Date(patrolRecord.getRealEndLimit()));
            stringBuilder.append("计划时间： " + startTime + " - " + endTime + " 共计" + duringTime + "分钟\n");
            stringBuilder.append("实际开始时间： " + realStartTime + "\n请在 " + realEndTime+ " 之前完成巡检");
        }
        lineInformation.setText(stringBuilder.toString());

        //Button
        eventFound = findViewById(R.id.event_found);
        eventHandle = findViewById(R.id.event_handle);
        endPatrol = findViewById(R.id.end_patrol);
        patrolPhoto = findViewById(R.id.patrol_photo);

        eventFound.setEnabled(false);
        eventHandle.setEnabled(false);
        patrolPhoto.setEnabled(false);
        patrolPhoto.setText(MapUtil.getPhotoType(patrolLine.getPictureType()));

        patrolPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ContextCompat.checkSelfPermission(PatrolingActivity.this, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(PatrolingActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else
                {
                    takePhoto();
                }
            }
        });

        eventFound.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(tempPointRecord != null){
                    Intent intent2 = new Intent(PatrolingActivity.this, EventFoundActivity.class);
                    intent2.putExtra("type","patrol");
                    intent2.putExtra("record",recordID);
                    intent2.putExtra("point",tempPointRecord.getPointId());
                    intent2.putExtra("police",patrolRecord.getPoliceId());
                    intent2.putExtra("photoType",patrolLine.getPictureType());
                    startActivity(intent2);
                }else{
                    LogUtil.e(TAG,"button clicked!");
                }
            }
        });

        eventHandle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent2 = new Intent(PatrolingActivity.this, EventRecordActivity.class);
                startActivity(intent2);
            }
        });

        endPatrol.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(countAll == countPatrolled){
                    endPatrol();
                }else{
                    new AlertDialog.Builder(mContext).setTitle("警告").setMessage("还有未检查的巡检点，是否结束巡检？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    endPatrol();
                                }
                            }).setNegativeButton("否",null).show();
                }
            }
        });

        //recycleView
        ipCount = findViewById(R.id.ip_count);
        mProgressDialog = ProgressDialog.show(mContext, "", "信息点初始化中...");
        new Thread(){
            public void run(){
                initIP();
                mProgressDialog.dismiss();
            }
        }.start();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InformationPointAdapter(patrolPointRecordList);
        recyclerView.setAdapter(adapter);
        LogUtil.e("patrolingActivity","adapter配置完成"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
    }

    private void endPatrol(){
        LogUtil.e("PatrolingActivity","点击结束巡检"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
        if(tempPointRecord != null){
            tempPointRecord.setState("已巡检");
            tempPointRecord.save();
        }
        patrolingPresenter.updatePatrol(recordID,true);
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
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        mProgressDialog = ProgressDialog.show(mContext, "", "读卡中...");
        LogUtil.e("PatrolingPresenter","触发NFC"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
        new Thread(){
            public void run(){
                readNFC(intent);
            }
        }.start();
    }

    private void readNFC(Intent intent){
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            final String icid = CommonUtil.bytesToHexString(tagFromIntent.getId());
            boolean flag = true;
            for(PatrolPointRecord patrolPointRecord : patrolPointRecordList){
                String pointID = patrolPointRecord.getPointId();
                InformationPoint informationPoint = LitePal.where("internetID = ?",pointID).findFirst(InformationPoint.class);
                if(informationPoint.getNum().equals(icid)){
                    flag = false;
                    if(patrolPointRecord.getState().equals("未巡检")){
                        if(!patrolLine.isIscanJump()){
                            if(tempPointRecord == null && !patrolPointRecord.getOrderNo().equals("1")){
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(mContext,"该路线不可跳检，请按顺序巡检！",Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }else if(tempPointRecord != null){
                                int tempOrderNo = Integer.parseInt(tempPointRecord.getOrderNo());
                                int thisOrderNo = Integer.parseInt(patrolPointRecord.getOrderNo());
                                if(thisOrderNo - tempOrderNo != 1){
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(mContext,"该路线不可跳检，请按顺序巡检！",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    return;
                                }
                            }
                        }
                        Calendar calendar = Calendar.getInstance();
                        patrolPointRecord.setTime(calendar.getTimeInMillis());
                        patrolPointRecord.setState("巡检中");
                        patrolPointRecord.save();
                        if(tempPointRecord != null){
                            tempPointRecord.setState("已巡检");
                            tempPointRecord.save();
                        }
                        tempPointRecord = patrolPointRecord;
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                addCount();
                                eventFound.setEnabled(true);
                                eventHandle.setEnabled(true);
                                adapter.notifyDataSetChanged();
                                if(patrolLine.getPictureType().equals("must")){
                                    if (ContextCompat.checkSelfPermission(PatrolingActivity.this, Manifest
                                            .permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    {
                                        ActivityCompat.requestPermissions(PatrolingActivity.this, new
                                                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    } else
                                    {
                                        takePhoto();
                                    }
                                    patrolPhoto.setEnabled(true);
                                }else if(patrolLine.getPictureType().equals("optional")){
                                    patrolPhoto.setEnabled(true);
                                    patrolingPresenter.updatePatrol(recordID,false);
                                }else{
                                    patrolingPresenter.updatePatrol(recordID,false);
                                }
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(mContext,"请勿重复巡检",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    break;
                }
            }
            if(flag){
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(mContext,"该信息点不属于此线路",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        mProgressDialog.dismiss();
    }


    private void initIP()
    {
        LogUtil.e("PatrolingActivity","初始化信息点"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
        patrolPointRecordList.clear();
        List<PatrolPointRecord> tempList = LitePal.where("patrolRecordId = ?",recordID).find(PatrolPointRecord.class);
        LogUtil.e("PatrolingActivity","从本地获取信息点列表1"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
        if(tempList.size() > 0){
            for(PatrolPointRecord patrolPointRecord : tempList){
                patrolPointRecordList.add(patrolPointRecord);
                if(patrolPointRecord.getState().equals("巡检中")){
                    tempPointRecord = patrolPointRecord;
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            eventFound.setEnabled(true);
                            eventHandle.setEnabled(true);
                            if(!patrolLine.getPictureType().equals("forbid")){
                                patrolPhoto.setEnabled(true);
                            }
                        }
                    });
                }
            }
        }else{
            List<PatrolIP> patrolIPList = LitePal.where("patrolLineID = ?",lineID).find(PatrolIP.class);
            LogUtil.e("PatrolingActivity","从本地获取信息点列表2"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
            for(PatrolIP patrolIP : patrolIPList){
                PatrolPointRecord patrolPointRecord = new PatrolPointRecord(recordID,patrolIP);
                patrolPointRecordList.add(patrolPointRecord);
                patrolPointRecord.save();
            }
        }
        LogUtil.e("PatrolingActivity","信息点初始化完毕，开始统计数量"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
        countAll = patrolPointRecordList.size();
        countPatrolled = 0;
        for(PatrolPointRecord patrolPointRecord : patrolPointRecordList){
            if(patrolPointRecord.getTime() != 0){
                countPatrolled++;
            }
        }
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ipCount.setText(countPatrolled + "/" + countAll);
            }
        });

        LogUtil.e("PatrolingActivity","数量统计完毕"+TimeUtil.dateToString(new Date(),"HH:mm:ss"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                endPatrol.performClick();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        endPatrol.performClick();
    }

    public void addCount(){
        countPatrolled++;
        ipCount.setText(countPatrolled + "/" + countAll);
    }

    public void takePhoto(){
        // 创建File对象，用于存储拍照后的图片
        long time = System.currentTimeMillis();
        File outputImage = new File(getExternalCacheDir(), time+".jpeg");
        imagePath = outputImage.getAbsolutePath();
        try
        {
            if (outputImage.exists())
            {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24)
        {
            imageUri = Uri.fromFile(outputImage);
        } else
        {
            imageUri = FileProvider.getUriForFile(mContext, "com.example.patrolinspection.fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK)
                {
                    imagePath = FileUtil.compressImagePathToImagePath(imagePath);
                    Calendar calendar = Calendar.getInstance();
                    tempPointRecord.addPhoto(imagePath,calendar.getTimeInMillis());
                    tempPointRecord.save();
                    patrolingPresenter.updatePatrol(recordID,false);
                }else if(resultCode == RESULT_CANCELED){
                    if(patrolLine.getPictureType().equals("must")&& !tempPointRecord.hasPhoto()){
                        new AlertDialog.Builder(mContext).setTitle("警告").setMessage("此线路必须拍照，请为此信息点拍照！")
                                .setPositiveButton("是", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        takePhoto();
                                    }
                                }).setCancelable(false).show();
                    }
                }else{
                    Toast.makeText(mContext,"不正常的返回值",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "你拒绝了权限请求！", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
