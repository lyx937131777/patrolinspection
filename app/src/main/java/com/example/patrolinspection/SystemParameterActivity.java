package com.example.patrolinspection;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

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

        nameText = findViewById(R.id.sp_phone_name);
        modelText = findViewById(R.id.sp_phone_model);
        idText = findViewById(R.id.sp_phone_id);
        appVersionText = findViewById(R.id.sp_app_version);
        androidVersionText = findViewById(R.id.sp_android_version);
        heartbeatText = findViewById(R.id.sp_heartbeat);
        heartbeatWorkText =findViewById(R.id.sp_heartbeat_work);

        nameText.setText(Build.BRAND);
        modelText.setText(Build.MODEL);
        idText.setText(Build.SERIAL);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        heartbeatText.setText(preferences.getInt("heartbeat",0)+"分钟");
        heartbeatWorkText.setText(preferences.getInt("heartbeatWork",0)+"分钟");

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
