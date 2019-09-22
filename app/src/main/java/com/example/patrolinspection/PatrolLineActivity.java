package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.patrolinspection.adapter.PatrolInspectionAdapter;
import com.example.patrolinspection.adapter.PatrolLineAdapter;
import com.example.patrolinspection.db.PatrolInspection;

import java.util.ArrayList;
import java.util.List;

public class PatrolLineActivity extends AppCompatActivity
{
    private PatrolInspection[] patrolInspections = {new PatrolInspection("线路1","进行中","10:00","11:00","60min"),
            new PatrolInspection("线路2","未开始","12:00","14:00","120min"),
            new PatrolInspection("线路3","已结束","7:00","8:30","90min"),
            new PatrolInspection("线路4","漏检","9:00","9:30","30min"),
            new PatrolInspection("线路5","跳检","9:30","10:00","30min")};
    private List<PatrolInspection> patrolInspectionList = new ArrayList<>();
    private PatrolLineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_line);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initPL();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PatrolLineAdapter(patrolInspectionList);
        recyclerView.setAdapter(adapter);
    }

    private void initPL()
    {
        patrolInspectionList.clear();
        //DataSupport.deleteAll(Type.class);
        for (int i = 0; i < patrolInspections.length; i++)
        {
            patrolInspectionList.add(patrolInspections[i]);
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
