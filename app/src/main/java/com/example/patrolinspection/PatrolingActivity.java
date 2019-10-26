package com.example.patrolinspection;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.provider.MediaStore;
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
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patroling);

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
        final String startTime =patrolSchedule.getStartTime();
        String endTime = patrolSchedule.getEndTime();
        int duringTime = patrolSchedule.getDuringMin();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String realStartTime =  format.format(new Date(patrolRecord.getStartTimeLong()));
        String realEndTime = format.format(new Date(patrolRecord.getRealEndLimit()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("计划时间： " + startTime + " - " + endTime + " 共计" + duringTime + "分钟\n");
        stringBuilder.append("实际开始时间： " + realStartTime + "\n请在 " + realEndTime+ " 之前完成巡检");
        TextView lineInformation = findViewById(R.id.line_information);
        lineInformation.setText(stringBuilder.toString());

        //recycleView
        ipCount = findViewById(R.id.ip_count);
        initIP();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InformationPointAdapter(patrolPointRecordList);
        recyclerView.setAdapter(adapter);

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
                takePhoto();
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

            }
        });

        endPatrol.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(countAll == countPatrolled){
                    patrolingPresenter.updatePatrol(recordID,true);
                }else{
                    new AlertDialog.Builder(mContext).setTitle("警告").setMessage("还有未检查的巡检点，是否结束巡检？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    patrolingPresenter.updatePatrol(recordID,true);
                                }
                            }).setNegativeButton("否",null).show();
                }
            }
        });
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
            final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            final String icid = CommonUtil.bytesToHexString(tagFromIntent.getId());
            boolean flag = true;
            for(PatrolPointRecord patrolPointRecord : patrolPointRecordList){
                String pointID = patrolPointRecord.getPointId();
                InformationPoint informationPoint = LitePal.where("internetID = ?",pointID).findFirst(InformationPoint.class);
                if(informationPoint.getNum().equals(icid)){
                    flag = false;
                    if(patrolPointRecord.getState().equals("未巡检")){
                        Calendar calendar = Calendar.getInstance();
                        patrolPointRecord.setTime(calendar.getTimeInMillis());
                        patrolPointRecord.setState("巡检中");
                        patrolPointRecord.save();
                        if(tempPointRecord != null){
                            tempPointRecord.setState("已巡检");
                            tempPointRecord.save();
                        }
                        tempPointRecord = patrolPointRecord;
                        addCount();
                        eventFound.setEnabled(true);
                        eventHandle.setEnabled(true);
                        adapter.notifyDataSetChanged();
                        //TODO 判断是否要拍照
                        if(patrolLine.getPictureType().equals("must")){
                            takePhoto();
                            patrolPhoto.setEnabled(true);
                        }else if(patrolLine.getPictureType().equals("optional")){
                            patrolPhoto.setEnabled(true);
                            patrolingPresenter.updatePatrol(recordID,false);
                        }else{
                            patrolingPresenter.updatePatrol(recordID,false);
                        }
                    }else{
                        Toast.makeText(mContext,"请勿重复巡检",Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
            if(flag){
                Toast.makeText(mContext,"该信息点不属于此线路",Toast.LENGTH_LONG).show();
            }
        }
    }


    private void initIP()
    {
        patrolPointRecordList.clear();
        List<PatrolIP> patrolIPList = LitePal.where("patrolLineID = ?",lineID).find(PatrolIP.class);
        for(PatrolIP patrolIP : patrolIPList){
            PatrolPointRecord patrolPointRecord = new PatrolPointRecord(recordID,patrolIP);
            patrolPointRecordList.add(patrolPointRecord);
            patrolPointRecord.save();
        }
        countAll = patrolPointRecordList.size();
        countPatrolled = 0;
        ipCount.setText(countPatrolled + "/" + countAll);
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
        File outputImage = new File(getExternalCacheDir(), time+".png");
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
                    tempPointRecord.setPhotoPath(imagePath);
                    tempPointRecord.save();
                    patrolingPresenter.updatePatrol(recordID,false);
                }else if(resultCode == RESULT_CANCELED){
                    if(patrolLine.getPictureType().equals("must")&& tempPointRecord.getPhotoPath().equals("")){
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
}
