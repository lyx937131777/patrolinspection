package com.example.patrolinspection;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.NfcUtil;

public class NfcActivity extends AppCompatActivity
{
    private static final String TAG = "NfcActivity";
    private String type;
    private String title;

    private PendingIntent mPendingIntent;
    private NfcAdapter mNfcAdapter;
    private Intent startIP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        LogUtil.e(TAG,"onCreate");
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
//        this.setTitle(intent.getStringExtra("title")); 也可以
        title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        type = intent.getStringExtra("type");

        startIP = new Intent(NfcActivity.this, InformationPointActivity.class);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, startIP, 0);
        if (mNfcAdapter == null) {
            Toast.makeText(NfcActivity.this, "nfc is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e(TAG, "onResume: ");
        if (mNfcAdapter != null) { //有nfc功能
            if (mNfcAdapter.isEnabled()) {
                //nfc功能打开了
                //隐式启动
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            } else {
                Toast.makeText(NfcActivity.this, "请打开nfc功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e(TAG,"onPause");
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        LogUtil.e(TAG, "onNewIntent: ");
//        setIntent(intent);
//        if (mNfcAdapter != null) { //有nfc功能
//            if (mNfcAdapter.isEnabled()) {//nfc功能打开了
//                String id = NfcUtil.getID(getIntent());
//                startIP.putExtra("id",id);
//                //startActivity(startIP);
//            } else {
//                Toast.makeText(NfcActivity.this, "请打开nfc功能", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

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
