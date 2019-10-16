package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

import com.example.patrolinspection.util.LogUtil;

public class SignActivity extends AppCompatActivity
{

    private AppCompatCheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        checkBox = findViewById(R.id.sign_defend_school);
        CardView signIn = findViewById(R.id.sign_in);
        CardView signOut = findViewById(R.id.sign_out);
        signIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SignActivity.this, SwipeNfcActivity.class);
                intent.putExtra("type","signIn");
                intent.putExtra("title","签到");
                if(checkBox.isChecked()){
                    intent.putExtra("attendanceType","school");
                }else{
                    intent.putExtra("attendanceType","common");
                }
                startActivity(intent);
            }
        });
        signOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SignActivity.this, SwipeNfcActivity.class);
                intent.putExtra("type","signOut");
                intent.putExtra("title","签退");
                if(checkBox.isChecked()){
                    intent.putExtra("attendanceType","school");
                }else{
                    intent.putExtra("attendanceType","common");
                }
                startActivity(intent);
            }
        });

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
