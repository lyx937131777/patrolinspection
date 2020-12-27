package com.example.patrolinspection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.patrolinspection.adapter.TypeAdapter;
import com.example.patrolinspection.db.Type;
import com.example.patrolinspection.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

//签到or签退界面（四or两个菜单供选择
public class SignActivity extends AppCompatActivity
{

    private Type[] types = {new Type("common", R.drawable.sign_in,R.drawable.sign_in, "signIn"),
            new Type("common", R.drawable.sign_out,R.drawable.sign_out,"signOut"),
            new Type("school", R.drawable.sign_in_school,R.drawable.sign_in_school, "signIn"),
            new Type("school", R.drawable.sign_out_school,R.drawable.sign_out_school,"signOut"),
    };
    private List<Type> typeList = new ArrayList<>();
    private TypeAdapter adapter;
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

        initTypes();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TypeAdapter(typeList);
        recyclerView.setAdapter(adapter);
    }

    private void initTypes()
    {
        typeList.clear();
        int n = 2;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean("isSchool",false)){
            n = 4;
        }
        for (int i = 0; i < n; i++)
        {
            typeList.add(types[i]);
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
