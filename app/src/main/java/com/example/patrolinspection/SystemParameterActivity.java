package com.example.patrolinspection;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patrolinspection.psam.CommonUtil;
import com.example.patrolinspection.service.DownloadService;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;

public class SystemParameterActivity extends AppCompatActivity
{
    private TextView nameText;
    private TextView modelText;
    private TextView idText;
    private TextView appVersionText;
    private TextView androidVersionText;
    private TextView heartbeatText;
    private TextView heartbeatWorkText;
    private TextView icidText;
    private Button updateSystem;

    //NFC读卡
    private String TAG = "SystemParameterActivity";
    private Context mContext;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private IntentFilter ndef;


    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection()
    {

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_parameter);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        initReadBak();

        nameText = findViewById(R.id.sp_phone_name);
        modelText = findViewById(R.id.sp_phone_model);
        idText = findViewById(R.id.sp_phone_id);
        appVersionText = findViewById(R.id.sp_app_version);
        androidVersionText = findViewById(R.id.sp_android_version);
        heartbeatText = findViewById(R.id.sp_heartbeat);
        heartbeatWorkText =findViewById(R.id.sp_heartbeat_work);
        icidText = findViewById(R.id.sp_icid);
        updateSystem = findViewById(R.id.update_system);

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent); // 启动服务
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务

        nameText.setText(Build.BRAND);
        modelText.setText(Build.MODEL);
        if(Build.SERIAL.equals("unknown")){
            if(Build.getSerial().toUpperCase().equals("UNKNOWN")){
                idText.setText(Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            }else{
                idText.setText(Build.getSerial().toUpperCase());
            }
        }else{
            idText.setText(Build.SERIAL.toUpperCase());
        }

        androidVersionText.setText(Build.VERSION.RELEASE);

        PackageManager manager = getPackageManager();
        String version = "未知";
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appVersionText.setText(version);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        heartbeatText.setText(preferences.getInt("heartbeat",0)+"分钟");
        heartbeatWorkText.setText(preferences.getInt("heartbeatWork",0)+"分钟");

        final String finalVersion = version;
        updateSystem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String latestVersion = preferences.getString("latestVersion","");
                if(latestVersion.equals(finalVersion) || latestVersion.equals("")){
                    Toast.makeText(mContext,"目前已是最新版本",Toast.LENGTH_LONG).show();
                }else{
                    if (downloadBinder == null)
                    {
                        Toast.makeText(mContext,"downloadBinder为空",Toast.LENGTH_LONG).show();
                        return;
                    }
                    downloadBinder.startDownload(HttpUtil.getPhotoURL(preferences.getString("latestVersionDownloadUrl","")));
                }
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initReadBak(){
        //NFC适配器，所有的关于NFC的操作从该适配器进行
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(!ifNFCUse()){
//            finish();
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
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String icid = CommonUtil.bytesToHexString(tagFromIntent.getId());
            icidText.setText(icid);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "你拒绝了下载权限，将无法进行系统更新", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(connection);
    }
}
