package com.example.patrolinspection;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.patrolinspection.adapter.PatrolInspectionAdapter;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolSchedule;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class PatrolInspectionActivity extends AppCompatActivity
{
    private List<PatrolSchedule> patrolScheduleList = new ArrayList<>();
    private PatrolInspectionAdapter adapter;

    private String planID;
    private String lineID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_inspection);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        lineID = intent.getStringExtra("line");
        planID = intent.getStringExtra("plan");
        PatrolLine patrolLine = LitePal.where("internetID = ?",lineID).findFirst(PatrolLine.class);
        String title = patrolLine.getPatrolLineName();
        actionBar.setTitle(title);

        initPI();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PatrolInspectionAdapter(patrolScheduleList);
        recyclerView.setAdapter(adapter);
    }

    private void initPI()
    {
        patrolScheduleList.clear();
        patrolScheduleList.addAll(LitePal.where("patrolLineId = ? and patrolPlanId = ?",lineID,planID).find(PatrolSchedule.class));
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
